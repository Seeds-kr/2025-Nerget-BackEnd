package com.seeds.NergetBackend.global.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EmbeddingService {

    private static final int DIM = 4; // 4차원 벡터 (실제는 512/768 등)

    @Value("${ai.server.url}")
    private String aiServerUrl;

    @Autowired
    private WebClient aiWebClient;

    /** 
     * 단일 이미지 URI를 벡터로 변환
     * AI 서버 POST /images/analyze 호출
     */
    public float[] embed(String uri) {
        try {
            // S3 URL에서 이미지 다운로드
            File imageFile = downloadImageFromS3(uri);
            
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", new FileSystemResource(imageFile));
            
            Map<String, Object> response = aiWebClient.post()
                    .uri("/images/analyze?conf_threshold=0.8")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            // AI 응답에서 벡터 추출
            float[] vector = new float[DIM];
            vector[0] = ((Number) response.get("v1")).floatValue();
            vector[1] = ((Number) response.get("v2")).floatValue();
            vector[2] = ((Number) response.get("v3")).floatValue();
            vector[3] = ((Number) response.get("v4")).floatValue();
            
            // 임시 파일 삭제
            imageFile.delete();
            
            return vector;
        } catch (Exception e) {
            // AI 서버 연동 실패 시 Mock 벡터 반환
            System.err.println("AI 서버 연동 실패, Mock 벡터 사용: " + e.getMessage());
            float[] v = new float[DIM];
            java.util.Random r = new java.util.Random(uri.hashCode());
            for (int i = 0; i < DIM; i++) {
                v[i] = (float) r.nextGaussian();
            }
            return v;
        }
    }

    /**
     * S3 URL에서 이미지를 다운로드하여 임시 파일로 저장
     */
    private File downloadImageFromS3(String s3Url) throws IOException {
        // S3 URL에서 이미지 다운로드
        java.net.URL url = new java.net.URL(s3Url);
        java.io.InputStream inputStream = url.openStream();
        
        // 임시 파일 생성
        File tempFile = File.createTempFile("image_", ".jpg");
        tempFile.deleteOnExit();
        
        // 파일에 저장
        java.nio.file.Files.copy(inputStream, tempFile.toPath(), 
            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        inputStream.close();
        
        return tempFile;
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