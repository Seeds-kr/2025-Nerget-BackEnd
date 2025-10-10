// src/main/java/com/seeds/NergetBackend/controller/FlowController.java
package com.seeds.NergetBackend.domain.flow.controller;

import com.seeds.NergetBackend.domain.flow.entity.Job;
import com.seeds.NergetBackend.domain.flow.entity.ImageVector;
import com.seeds.NergetBackend.domain.flow.service.ImageVectorService;
import com.seeds.NergetBackend.domain.flow.service.JobService;
import com.seeds.NergetBackend.global.common.AiWorkerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/flow")
@Tag(name = "플로우 관리", description = "이미지 업로드 및 AI 처리 플로우 관리 API")
public class FlowController {

    private final JobService jobService;
    private final ImageVectorService imageVectorService;
    private final AiWorkerService aiWorkerService;

    @Operation(summary = "이미지 처리 시작", description = "S3에 업로드된 이미지들을 AI로 처리하기 시작합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작업 시작 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JobStartResp.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "jobId": "job-12345",
                                        "countRegistered": 3
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/start")
    public ResponseEntity<JobStartResp> start(
            @Parameter(description = "이미지 처리 시작 요청", required = true)
            @RequestBody StartReq req) {
        try {
            Job job;

            if (req.s3Keys == null || req.s3Keys.isEmpty()) {
                // 사진 업로드 안 한 경우 → 바로 DONE Job 생성
                job = jobService.createEmptyJob(req.userId);
            } else {
                // 1) Job 생성 (온보딩 Job 1:1 제약 적용)
                job = jobService.createJob(req.userId, req.s3Keys);

                // 2) 이미지들을 Job과 연결하여 PENDING 등록
                imageVectorService.registerPendingBatch(req.userId, req.s3Keys, req.metaJson, job.getId());

                // 3) AI 분석 시작 (즉시 실행)
                aiWorkerService.processJob(job.getId(), req.s3Keys);
            }

            JobStartResp resp = new JobStartResp();
            resp.jobId = job.getId();
            resp.countRegistered = (req.s3Keys == null) ? 0 : req.s3Keys.size();
            return ResponseEntity.ok(resp);
        } catch (IllegalStateException e) {
            // 온보딩 Job이 이미 존재하는 경우
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "작업 상태 조회", description = "AI 이미지 처리 작업의 현재 상태와 진행률을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상태 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JobStatusResp.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "jobId": "job-12345",
                                        "status": "DONE",
                                        "progress": 100,
                                        "initVector": [0.1, 0.2, 0.3, 0.4]
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "작업을 찾을 수 없음")
    })
    @GetMapping("/{jobId}/status")
    public ResponseEntity<JobStatusResp> status(
            @Parameter(description = "작업 ID", required = true, example = "job-12345")
            @PathVariable String jobId) {
        Job job = jobService.getJob(jobId);

        JobStatusResp resp = new JobStatusResp();
        resp.jobId = job.getId();
        resp.status = job.getStatus().name();
        resp.progress = job.getProgress();
        if (job.getStatus() == Job.Status.DONE) {
            resp.initVector = job.toArray();
        } else if (job.getStatus() == Job.Status.FAILED) {
            resp.error = job.getError();
        }
        return ResponseEntity.ok(resp);
    }

    // ====== 요청/응답 DTO (간단 내장) ======

    @Data
    public static class StartReq {
        public String userId;          // 선택: 인증 토큰에서 가져오면 생략 가능
        public List<String> s3Keys;    // 업로드 완료된 S3 객체 키(최대 4장)
        public String metaJson;        // 선택: 공통 메타
    }

    @Data
    public static class JobStartResp {
        public String jobId;
        public int countRegistered;
    }

    @Data
    public static class JobStatusResp {
        public String jobId;
        public String status;      // PENDING | RUNNING | DONE | FAILED
        public int progress;       // 0~100
        public float[] initVector; // DONE일 때만
        public String error;       // FAILED일 때만
    }
}