// src/main/java/com/seeds/NergetBackend/service/JobService.java
package com.seeds.NergetBackend.service;

import com.seeds.NergetBackend.entity.Job;
import com.seeds.NergetBackend.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final EmbeddingService embeddingService;

    /** 새 Job 생성 (업로드 완료 시 호출) */
    @Transactional
    public Job createJob(String userId, List<String> uris) {
        Job job = Job.builder()
                .userId(userId)
                .status(Job.Status.PENDING)
                .progress(0)
                .build();
        return jobRepository.save(job);
    }

    /** Job 진행 중으로 표시 */
    @Transactional
    public void markRunning(String jobId, int progress) {
        Job job = jobRepository.findById(jobId).orElseThrow();
        job.setStatus(Job.Status.RUNNING);
        job.setProgress(progress);
        jobRepository.save(job);
    }

    /** Job 실패 처리 */
    @Transactional
    public void markFailed(String jobId, String errorMsg) {
        Job job = jobRepository.findById(jobId).orElseThrow();
        job.setStatus(Job.Status.FAILED);
        job.setError(errorMsg);
        jobRepository.save(job);
    }

    /** Job 완료 처리 + 벡터 저장 */
    @Transactional
    public void markDone(String jobId, List<float[]> vectors) {
        Job job = jobRepository.findById(jobId).orElseThrow();
        float[] agg = embeddingService.aggregate(vectors); // 4차원 평균
        job.fromArray(agg);
        job.setStatus(Job.Status.DONE);
        job.setProgress(100);
        jobRepository.save(job);
    }

    /** Job 상태 조회 */
    @Transactional(readOnly = true)
    public Job getJob(String jobId) {
        return jobRepository.findById(jobId).orElseThrow();
    }

    /** 초기 벡터만 필요할 때 */
    @Transactional(readOnly = true)
    public float[] requireInitVector(String jobId) {
        Job job = getJob(jobId);
        if (job.getStatus() != Job.Status.DONE) {
            throw new IllegalStateException("Job not finished");
        }
        return job.toArray();
    }
}