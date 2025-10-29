package com.seeds.NergetBackend.domain.candidate.service;

import com.seeds.NergetBackend.domain.candidate.dto.CandidatesResponse;
import com.seeds.NergetBackend.domain.flow.entity.Job;
import com.seeds.NergetBackend.domain.flow.service.JobService;
import com.seeds.NergetBackend.shared.vector.VectorStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final JobService jobService;   // 초기 4장 분석 완료 여부 확인
    private final VectorStore vectorStore; // 후보 12장/벡터 제공

    /** Job 상태와 관계없이 후보 이미지 제공 (사진 업로드 Skip 지원) */
    public CandidatesResponse getCandidatesOrNull(String jobId, int count) {
        try {
            // Job이 있으면 상태 확인
            Job job = jobService.getJob(jobId);
            if (job.getStatus() != Job.Status.DONE) {
                // Job이 DONE이 아니어도 스와이핑 가능 (사진 업로드 Skip)
                return new CandidatesResponse(vectorStore.getRandomCandidates(count));
            }
        } catch (Exception e) {
            // Job이 없어도 스와이핑 가능 (사진 업로드 Skip)
            // 로그만 남기고 계속 진행
        }
        return new CandidatesResponse(vectorStore.getRandomCandidates(count));
    }
}