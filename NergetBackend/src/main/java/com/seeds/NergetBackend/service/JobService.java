package com.seeds.NergetBackend.service;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
public class JobService {

    /** 간단히 메모리에 보관 (실서비스는 DB 권장) */
    private final Map<String, JobStatus> jobs = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * 초기 4장 분석 잡 시작
     * - 즉시 jobId 발급
     * - 내부 스케줄러가 진행률 올리면서 마지막에 임베딩 계산 & 최종 벡터 저장
     */
    public String startInitialAnalysis(String flowId, List<String> uris, EmbeddingService embeddingService) {
        String jobId = UUID.randomUUID().toString();
        jobs.put(jobId, JobStatus.pending(flowId));

        // 0.6초 간격으로 진행률 0→25→50→75→100
        scheduler.scheduleAtFixedRate(() -> {
            JobStatus s = jobs.get(jobId);
            if (s == null || s.progress >= 100) return;

            int next = Math.min(100, s.progress + 25);
            if (next < 100) {
                jobs.put(jobId, s.toRunning(next));
            } else {
                // 마지막 단계에서 실제(모킹) 임베딩 계산
                try {
                    List<float[]> vecs = embeddingService.embedAll(uris);
                    float[] agg = embeddingService.aggregate(vecs);
                    jobs.put(jobId, s.toDone(agg));
                } catch (Exception ex) {
                    jobs.put(jobId, s.toFailed("embedding error: " + ex.getMessage()));
                }
            }
        }, 0, 600, TimeUnit.MILLISECONDS);

        return jobId;
    }

    /** (다음 단계에서 JobController가 사용할 조회용 메서드) */
    public JobStatus getStatus(String jobId) {
        JobStatus s = jobs.get(jobId);
        if (s == null) throw new IllegalArgumentException("job not found");
        return s;
    }

    // ===== 상태 객체 =====
    @Getter
    public static class JobStatus {
        private final String flowId;
        private final String status;      // PENDING | RUNNING | DONE | FAILED
        private final int progress;       // 0~100
        private final float[] resultVector;
        private final String error;

        private JobStatus(String flowId, String status, int progress, float[] resultVector, String error) {
            this.flowId = flowId;
            this.status = status;
            this.progress = progress;
            this.resultVector = resultVector;
            this.error = error;
        }

        public static JobStatus pending(String flowId) {
            return new JobStatus(flowId, "PENDING", 0, null, null);
        }

        public JobStatus toRunning(int progress) {
            return new JobStatus(flowId, "RUNNING", progress, null, null);
        }

        public JobStatus toDone(float[] resultVector) {
            return new JobStatus(flowId, "DONE", 100, resultVector, null);
        }

        public JobStatus toFailed(String error) {
            return new JobStatus(flowId, "FAILED", progress, null, error);
        }
    }
}