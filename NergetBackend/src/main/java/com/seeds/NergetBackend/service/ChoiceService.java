package com.seeds.NergetBackend.service;

import com.seeds.NergetBackend.dto.MbtiResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChoiceService {

    private final JobService jobService;            // 초기 4장 job 상태/결과 확인
    private final EmbeddingService embeddingService; // (URI 기반 선택 시 사용)
    private final VectorStore vectorStore;           // (ID 기반 선택 시 사용)

    /**
     * 프론트가 이미지 URI를 보냈을 때 쓰는 버전
     */
    public MbtiResultDto processChoicesOrNull(String jobId, List<String> choiceImageUris) {
        var status = jobService.getStatus(jobId);
        if (!"DONE".equals(status.getStatus())) return null;

        float[] initialVec = status.getResultVector();

        // URI → 임베딩 → 평균
        List<float[]> choiceVecs = embeddingService.embedAll(choiceImageUris);
        float[] choiceAgg = average(choiceVecs);

        float[] finalVec = combineAverage(initialVec, choiceAgg);

        String mbti = mockMbti(finalVec);
        String explanation = "시각적 선호 패턴 기반 예비 결과 (URI 기반 모킹)";
        return new MbtiResultDto(mbti, explanation, finalVec);
    }

    /**
     * 서버가 제공한 후보 ID들을 프론트가 제출했을 때 쓰는 버전(AWS 벡터)
     */
    public MbtiResultDto processChoicesFromIdsOrNull(String jobId, List<String> selectedIds) {
        var st = jobService.getStatus(jobId);
        if (!"DONE".equals(st.getStatus())) return null;

        float[] initialVec = st.getResultVector();

        // ID → 벡터 조회 → 평균
        List<float[]> selectedVecs = vectorStore.getVectorsByIds(selectedIds);
        float[] choiceAvg = average(selectedVecs);

        float[] finalVec = combineAverage(initialVec, choiceAvg);

        String mbti = mockMbti(finalVec);
        String exp  = "초기 선호 + 선택 선호 결합 결과(벡터ID 기반)";
        return new MbtiResultDto(mbti, exp, finalVec);
    }

    // ---- 아래는 유틸 메서드들 (없어서 에러 나던 부분) ----

    /** 여러 벡터의 평균 */
    private float[] average(List<float[]> vecs) {
        if (vecs == null || vecs.isEmpty()) return new float[0];
        int dim = vecs.get(0).length;
        float[] avg = new float[dim];
        for (float[] v : vecs) {
            for (int i = 0; i < dim; i++) avg[i] += v[i];
        }
        for (int i = 0; i < dim; i++) avg[i] /= vecs.size();
        return avg;
    }

    /** 두 벡터의 단순 평균 */
    private float[] combineAverage(float[] a, float[] b) {
        int n = Math.max(a.length, b.length);
        float[] out = new float[n];
        for (int i = 0; i < n; i++) {
            float av = i < a.length ? a[i] : 0f;
            float bv = i < b.length ? b[i] : 0f;
            out[i] = (av + bv) / 2f;
        }
        return out;
    }

    /** 간단한 MBTI 모킹 (앞 4차원 부호로 판정) */
    private String mockMbti(float[] v) {
        float e = v.length > 0 ? v[0] : 0;
        float s = v.length > 1 ? v[1] : 0;
        float t = v.length > 2 ? v[2] : 0;
        float j = v.length > 3 ? v[3] : 0;
        return (e >= 0 ? "E" : "I") +
                (s >= 0 ? "S" : "N") +
                (t >= 0 ? "T" : "F") +
                (j >= 0 ? "J" : "P");
    }
}