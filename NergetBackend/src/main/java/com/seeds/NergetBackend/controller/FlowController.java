// src/main/java/com/seeds/NergetBackend/controller/FlowController.java
package com.seeds.NergetBackend.controller;

import com.seeds.NergetBackend.entity.Job;
import com.seeds.NergetBackend.entity.ImageVector;
import com.seeds.NergetBackend.service.ImageVectorService;
import com.seeds.NergetBackend.service.JobService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/flow")
public class FlowController {

    private final JobService jobService;
    private final ImageVectorService imageVectorService;

    /**
     * 업로드 완료 후 호출:
     * - 이미지 S3 키들을 PENDING으로 등록
     * - Job 생성 (즉시 응답)
     * 실제 AI 처리(벡터 산출)는 별도 워커에서 수행 (다음 클래스에서 제공 예정)
     */
    @PostMapping("/start")
    public ResponseEntity<JobStartResp> start(@RequestBody StartReq req) {
        Job job;

        if (req.s3Keys == null || req.s3Keys.isEmpty()) {
            // 사진 업로드 안 한 경우 → 바로 DONE Job 생성
            job = jobService.createEmptyJob(req.userId);
        } else {
            // 1) 이미지들을 PENDING 등록
            List<ImageVector> pendings =
                    imageVectorService.registerPendingBatch(req.userId, req.s3Keys, req.metaJson);

            // 2) Job 생성
            job = jobService.createJob(req.userId, req.s3Keys);
        }

        JobStartResp resp = new JobStartResp();
        resp.jobId = job.getId();
        resp.countRegistered = (req.s3Keys == null) ? 0 : req.s3Keys.size();
        return ResponseEntity.ok(resp);
    }

    /**
     * Job 상태 조회:
     * - PENDING | RUNNING | DONE | FAILED
     * - 진행률(0~100)
     * - (DONE인 경우) 초기 벡터 4D
     */
    @GetMapping("/{jobId}/status")
    public ResponseEntity<JobStatusResp> status(@PathVariable String jobId) {
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