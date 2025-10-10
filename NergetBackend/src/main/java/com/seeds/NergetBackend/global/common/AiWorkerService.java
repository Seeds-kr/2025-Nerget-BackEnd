// src/main/java/com/seeds/NergetBackend/service/AiWorkerService.java
package com.seeds.NergetBackend.global.common;

import com.seeds.NergetBackend.domain.flow.entity.ImageVector;
import com.seeds.NergetBackend.domain.flow.service.ImageVectorService;
import com.seeds.NergetBackend.domain.flow.repository.ImageVectorRepository;
import com.seeds.NergetBackend.domain.flow.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 실제로는 외부 AI API를 호출해서 벡터를 가져오는 역할.
 * 여기서는 구조만 정의 (AIClient 등으로 위임 가능)
 */
@Service
@RequiredArgsConstructor
public class AiWorkerService {

    private final ImageVectorService imageVectorService;
    private final ImageVectorRepository imageRepo;
    private final JobService jobService;
    private final EmbeddingService embeddingService;

    /**
     * 특정 Job에 대한 업로드 이미지들을 AI에 보내고
     * 벡터 계산 완료 후 DB/Job 업데이트
     */
    @Transactional
    public void processJob(String jobId, List<String> s3Keys) {
        List<float[]> vectors = new ArrayList<>();

        try {
            // 테스트용: 즉시 완료 처리 (AI 분석 생략)
            for (String key : s3Keys) {
                // Mock 벡터 생성
                float[] vec = embeddingService.embed(key);
                vectors.add(vec);

                // 각 이미지 DONE 처리
                ImageVector iv = imageRepo.findAll().stream()
                        .filter(x -> x.getS3Key().equals(key))
                        .findFirst()
                        .orElseThrow();
                imageVectorService.saveVectorDone(iv.getId(), vec, null);
            }

            // 모든 벡터 평균 → Job 완료 처리 (즉시)
            jobService.markDone(jobId, vectors);

        } catch (Exception ex) {
            jobService.markFailed(jobId, ex.getMessage());
        }
    }
}