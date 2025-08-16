package com.seeds.NergetBackend.service;

import com.seeds.NergetBackend.dto.MbtiResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChoiceService {

    private final JobService jobService;             // 초기 4장 job 상태/결과 확인
    private final EmbeddingService embeddingService; // 12장 임베딩 + 집계

    /**
     * 스와이프 선택 제출 처리
     * - jobId가 가리키는 초기 4장 분석이 DONE이면: 그 결과 + 12장 벡터 합성 → MBTI 산출
     * - 아직 완료 안 됐으면: null 반환 (컨트롤러가 202 Accepted로 응답)
     */
    public MbtiResultDto processChoicesOrNull(String jobId, List<String> choiceImageUris) {
        var status = jobService.getStatus(jobId); // DONE이면 resultVector 있음

        if (!"DONE".equals(status.getStatus())) {
            return null; // 아직 분석 중 → 컨트롤러에서 202 처리
        }

        // 1) 초기 4장 결과 벡터
        float[] initialVec = status.getResultVector();

        // 2) 스와이프 12장 임베딩 + 집계
        List<float[]> choiceVecs = embeddingService.embedAll(choiceImageUris);
        float[] choiceAgg = embeddingService.aggregate(choiceVecs);

        // 3) 최종 벡터 합성 (단순 평균, 필요 시 가중치 적용 가능)
        float[] finalVec = combine(initialVec, choiceAgg);

        // 4) MBTI 산출 (모킹: 벡터 앞 4개 차원의 부호 기반)
        String mbti = mockMbti(finalVec);
        String explanation = "시각적 선호 패턴 기반 예비 결과 (모킹).";

        return new MbtiResultDto(mbti, explanation, finalVec);
    }

    /** 벡터 합성 (단순 평균) */
    private float[] combine(float[] a, float[] b) {
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