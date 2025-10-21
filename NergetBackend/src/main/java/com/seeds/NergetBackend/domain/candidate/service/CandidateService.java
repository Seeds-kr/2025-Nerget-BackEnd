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

    /** 초기 분석이 DONE일 때만 12장 후보 제공, 아니면 null 반환 */
    public CandidatesResponse getCandidatesOrNull(String jobId, int count) {
        Job job = jobService.getJob(jobId);
        if (job.getStatus() != Job.Status.DONE) return null;
        return new CandidatesResponse(vectorStore.getRandomCandidates(count));
    }
}