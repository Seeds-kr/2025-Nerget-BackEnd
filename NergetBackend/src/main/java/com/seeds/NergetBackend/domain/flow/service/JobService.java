// src/main/java/com/seeds/NergetBackend/service/JobService.java
package com.seeds.NergetBackend.domain.flow.service;

import com.seeds.NergetBackend.domain.flow.entity.Job;
import com.seeds.NergetBackend.domain.flow.repository.JobRepository;
import com.seeds.NergetBackend.shared.ai.EmbeddingService;
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
        return createJob(userId, uris, Job.Type.ONBOARDING);
    }

    /** 새 Job 생성 (타입 지정) */
    @Transactional
    public Job createJob(String userId, List<String> uris, Job.Type type) {
        // 사용자당 동일 타입 Job은 1개만 허용 - 있으면 재사용
        if (type == Job.Type.ONBOARDING) {
            var existing = jobRepository.findByUserIdAndType(userId, type);
            if (existing.isPresent()) {
                Job existingJob = existing.get();
                // 테스트용: 기존 Job을 PENDING으로 리셋
                existingJob.setStatus(Job.Status.PENDING);
                existingJob.setProgress(0);
                return jobRepository.save(existingJob);
            }
        }

        Job job = Job.builder()
                .userId(userId)
                .type(type)
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
        return jobRepository.findById(jobId).orElseThrow(
            () -> new IllegalArgumentException("Job not found: " + jobId)
        );
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

    @Transactional
    public Job createEmptyJob(String userId) {
        return createEmptyJob(userId, Job.Type.ONBOARDING);
    }

    @Transactional
    public Job createEmptyJob(String userId, Job.Type type) {
        // 사용자당 동일 타입 Job은 1개만 허용 - 있으면 재사용
        if (type == Job.Type.ONBOARDING) {
            var existing = jobRepository.findByUserIdAndType(userId, type);
            if (existing.isPresent()) {
                Job existingJob = existing.get();
                // 테스트용: 기존 Job을 DONE 상태로 업데이트하여 재사용
                existingJob.setStatus(Job.Status.DONE);
                existingJob.setProgress(100);
                return jobRepository.save(existingJob);
            }
        }

        Job job = Job.builder()
                .userId(userId)
                .type(type)
                .status(Job.Status.DONE)  // 바로 완료 처리
                .progress(100)
                .build();
        return jobRepository.save(job);
    }
}