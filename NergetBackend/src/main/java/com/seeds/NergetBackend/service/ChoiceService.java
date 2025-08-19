// src/main/java/com/seeds/NergetBackend/service/ChoiceService.java
package com.seeds.NergetBackend.service;

import com.seeds.NergetBackend.dto.MbtiResultDto;
import com.seeds.NergetBackend.repository.ImageVectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChoiceService {

    private final JobService jobService;              // 초기 4장 job 상태/결과 확인
    private final VectorStore vectorStore;            // 후보 벡터 조회
    private final ImageVectorRepository imageRepo;    // 저장된 벡터 직접 조회 가능

    /**
     * 사용자가 12개 후보 중 선택(좋아요/싫어요)했을 때 결과 계산
     */
    public MbtiResultDto processChoicesFromIdsOrNull(String jobId, List<String> likeIds, List<String> dislikeIds) {
        com.seeds.NergetBackend.entity.Job job = jobService.getJob(jobId);
        if (job.getStatus() != com.seeds.NergetBackend.entity.Job.Status.DONE) return null;

        float[] vInit = job.toArray(); // 초기 벡터
        float[] meanLike = average(vectorStore.getVectorsByIds(likeIds));
        float[] meanHate = average(vectorStore.getVectorsByIds(dislikeIds));
        float[] vSwipe = subtract(meanLike, meanHate);

        float alpha = 0.6f;
        float[] vFinal3 = combineWeighted(vInit, vSwipe, alpha);

        float v4 = polarization(vFinal3);
        float[] vFinal = new float[]{vFinal3[0], vFinal3[1], vFinal3[2], v4};
        String mbti = toMbti(vFinal);
        return new MbtiResultDto(mbti, "초기 + 스와이프 결합 결과", vFinal);
    }

    /** 여러 벡터 평균 */
    private float[] average(List<float[]> vecs) {
        int dim = 4;
        float[] avg = new float[dim];
        if (vecs == null || vecs.isEmpty()) return avg;
        for (float[] v : vecs) {
            for (int i = 0; i < dim; i++) {
                float val = (v != null && v.length > i) ? v[i] : 0f;
                avg[i] += val;
            }
        }
        for (int i = 0; i < dim; i++) avg[i] /= vecs.size();
        return avg;
    }

    /** 두 벡터의 가중 평균 */
    private float[] combineWeighted(float[] a, float[] b, float alpha) {
        int dim = 4;
        float[] out = new float[dim];
        for (int i = 0; i < dim; i++) {
            float av = (a != null && a.length > i) ? a[i] : 0f;
            float bv = (b != null && b.length > i) ? b[i] : 0f;
            out[i] = alpha * av + (1 - alpha) * bv;
        }
        return out;
    }

    /** 벡터 차감 (좋아요 - 싫어요) */
    private float[] subtract(float[] a, float[] b) {
        int dim = 4;
        float[] out = new float[dim];
        for (int i = 0; i < dim; i++) {
            float av = (a != null && a.length > i) ? a[i] : 0f;
            float bv = (b != null && b.length > i) ? b[i] : 0f;
            out[i] = av - bv;
        }
        return out;
    }

    /** 4축: 1,2,3의 양극화 정도 */
    private float polarization(float[] v) {
        float pol = (Math.abs(v[0]) + Math.abs(v[1]) + Math.abs(v[2])) / 3f;
        return 2f * pol - 1f; // [-1,1]
    }

    /** MBTI 라벨링 */
    private String toMbti(float[] v) {
        String axis1 = (v[0] >= 0 ? "S" : "B");
        String axis2 = (v[1] >= 0 ? "F" : "C");
        String axis3 = (v[2] >= 0 ? "G" : "P");
        String axis4 = (v[3] >= 0 ? "E" : "N");
        return axis1 + axis2 + axis3 + axis4;
    }
}