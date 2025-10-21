// src/main/java/com/seeds/NergetBackend/controller/FlowController.java
package com.seeds.NergetBackend.domain.flow.controller;

import com.seeds.NergetBackend.domain.flow.entity.Job;
import com.seeds.NergetBackend.domain.flow.entity.ImageVector;
import com.seeds.NergetBackend.domain.flow.service.ImageVectorService;
import com.seeds.NergetBackend.domain.flow.service.JobService;
import com.seeds.NergetBackend.shared.ai.AiWorkerService;
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
    private final AiWorkerService aiWorkerService;

    @PostMapping("/start")
    public ResponseEntity<JobStartResp> start(
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

    @GetMapping("/{jobId}/status")
    public ResponseEntity<JobStatusResp> status(
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