package com.seeds.NergetBackend.global.common;

import org.springframework.beans.factory.annotation.Value;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.core.io.FileSystemResource;
// import org.springframework.http.MediaType;
// import org.springframework.http.client.MultipartBodyBuilder;
// import org.springframework.web.reactive.function.BodyInserters;
// import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Service;

// import java.io.File;
import java.util.ArrayList;
import java.util.List;
// import java.util.Map;
import java.util.Random;

@Service
public class EmbeddingService {

    private static final int DIM = 4; // 4차원 벡터 (실제는 512/768 등)

    @Value("${ai.server.url}")
    private String aiServerUrl;

    // 실제 AI 서버 연동 시 주석 해제
    // @Autowired
    // private WebClient aiWebClient;

    /** 
     * 단일 이미지 URI를 벡터로 변환
     * 
     * 현재: Mock 랜덤 벡터 생성
     * 실제 연동: AI 서버 POST /images/analyze 호출
     */
    public float[] embed(String uri) {
        // ============================================================
        // 현재: Mock 구현 (테스트용)
        // ============================================================
        float[] v = new float[DIM];
        Random r = new Random(uri.hashCode());
        for (int i = 0; i < DIM; i++) {
            v[i] = (float) r.nextGaussian();
        }
        return v;

        // ============================================================
        // 실제 AI 서버 연동 코드 (주석 해제 후 사용)
        // ============================================================
        // try {
        //     // 이미지 파일이 로컬에 있다면
        //     File imageFile = new File(uri);
        //     
        //     MultipartBodyBuilder builder = new MultipartBodyBuilder();
        //     builder.part("file", new FileSystemResource(imageFile));
        //     
        //     Map<String, Object> response = aiWebClient.post()
        //             .uri("/images/analyze?conf_threshold=0.8")
        //             .contentType(MediaType.MULTIPART_FORM_DATA)
        //             .body(BodyInserters.fromMultipartData(builder.build()))
        //             .retrieve()
        //             .bodyToMono(Map.class)
        //             .block();
        //     
        //     // AI 응답에서 벡터 추출
        //     float[] vector = new float[DIM];
        //     vector[0] = ((Number) response.get("v1")).floatValue();
        //     vector[1] = ((Number) response.get("v2")).floatValue();
        //     vector[2] = ((Number) response.get("v3")).floatValue();
        //     vector[3] = ((Number) response.get("v4")).floatValue();
        //     
        //     return vector;
        // } catch (Exception e) {
        //     throw new RuntimeException("AI 이미지 분석 실패: " + e.getMessage(), e);
        // }
    }

    /** 여러 이미지 임베딩 */
    public List<float[]> embedAll(List<String> uris) {
        List<float[]> out = new ArrayList<>();
        for (String uri : uris) out.add(embed(uri));
        return out;
    }

    /** 간단한 평균 집계 */
    public float[] aggregate(List<float[]> vectors) {
        int dim = 4; // 4차원 고정
        float[] sum = new float[dim];
        if (vectors == null || vectors.isEmpty()) return sum;
            for (float[] v : vectors) {
                for (int i = 0; i < dim; i++) {
                    float val = (v != null && v.length > i) ? v[i] : 0f;
                    sum[i] += val;
                }
            }
            for (int i = 0; i < dim; i++) sum[i] /= vectors.size();
            return sum;
    }
}