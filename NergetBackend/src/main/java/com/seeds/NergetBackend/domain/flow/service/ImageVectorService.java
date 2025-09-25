// src/main/java/com/seeds/NergetBackend/service/ImageVectorService.java
package com.seeds.NergetBackend.domain.flow.service;

import com.seeds.NergetBackend.domain.home.dto.RecommendationItemDto;
import com.seeds.NergetBackend.domain.flow.entity.ImageVector;
import com.seeds.NergetBackend.domain.flow.repository.ImageVectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageVectorService {

    private final ImageVectorRepository repo;
    
    // S3 퍼블릭 베이스 URL (ChoiceService와 동일한 설정)
    @Value("${app.s3.public-base-url:}")
    private String s3PublicBaseUrl;

    /** 업로드 직후: PENDING 레코드 생성 (AI 처리 대기) */
    @Transactional
    public ImageVector registerPending(String userId, String s3Key, String metaJson) {
        return registerPending(userId, s3Key, metaJson, null);
    }

    /** 업로드 직후: PENDING 레코드 생성 (Job 연결) */
    @Transactional
    public ImageVector registerPending(String userId, String s3Key, String metaJson, String jobId) {
        ImageVector iv = ImageVector.builder()
                .userId(userId)
                .jobId(jobId)
                .s3Key(s3Key)
                .metaJson(metaJson)
                .status(ImageVector.Status.PENDING)
                .v1(0f).v2(0f).v3(0f).v4(0f)
                .build();
        return repo.save(iv);
    }

    /** 여러 장 한 번에 PENDING 등록 */
    @Transactional
    public List<ImageVector> registerPendingBatch(String userId, List<String> s3Keys, String metaJson) {
        return registerPendingBatch(userId, s3Keys, metaJson, null);
    }

    /** 여러 장 한 번에 PENDING 등록 (Job 연결) */
    @Transactional
    public List<ImageVector> registerPendingBatch(String userId, List<String> s3Keys, String metaJson, String jobId) {
        return s3Keys.stream()
                .map(k -> registerPending(userId, k, metaJson, jobId))
                .toList();
    }

    /** AI 처리 시작 표시 */
    @Transactional
    public void markRunning(String id) {
        ImageVector iv = repo.findById(id).orElseThrow();
        iv.setStatus(ImageVector.Status.RUNNING);
        repo.save(iv);
    }

    /** AI가 계산한 4D 벡터 저장 + DONE */
    @Transactional
    public void saveVectorDone(String id, float[] v4, String metaJsonAppend) {
        ImageVector iv = repo.findById(id).orElseThrow();
        iv.fromArray(safe4(v4));
        iv.clampMinusOneToOne(); // 필요 시 [-1,1] 강제
        if (metaJsonAppend != null && !metaJsonAppend.isBlank()) {
            String prev = iv.getMetaJson() == null ? "" : iv.getMetaJson();
            iv.setMetaJson(prev + metaJsonAppend);
        }
        iv.setStatus(ImageVector.Status.DONE);
        repo.save(iv);
    }

    /** s3Key로 찾아서 저장 (워커가 key만 아는 경우) */
    @Transactional
    public void saveVectorByS3KeyDone(String s3Key, float[] v4, String metaJsonAppend) {
        ImageVector iv = repo.findAll().stream()
                .filter(x -> s3Key.equals(x.getS3Key()))
                .findFirst()
                .orElseThrow();
        saveVectorDone(iv.getId(), v4, metaJsonAppend);
    }

    /** DONE 벡터들 중 임의 N개 가져오기(후보 풀 대용) */
    @Transactional(readOnly = true)
    public List<ImageVector> sampleDone(int count) {
        List<ImageVector> done = repo.findByStatus(ImageVector.Status.DONE);
        java.util.Collections.shuffle(done);
        return done.stream().limit(count).toList();
    }

    /** MBTI 코드로 이미지 검색 */
    @Transactional(readOnly = true)
    public List<RecommendationItemDto> findByMbtiCode(String code, int limit) {
        // DONE 상태인 모든 이미지 가져오기
        List<ImageVector> doneImages = repo.findByStatus(ImageVector.Status.DONE);
        
        // MBTI 코드와 일치하는 이미지 필터링
        List<ImageVector> matchingImages = doneImages.stream()
                .filter(image -> {
                    String imageMbti = calculateMbtiFromVector(image);
                    return code.equals(imageMbti);
                })
                .limit(limit)
                .collect(Collectors.toList());
        
        // RecommendationItemDto로 변환
        return matchingImages.stream()
                .map(image -> RecommendationItemDto.builder()
                        .imageId(image.getId())
                        .imageUrl(toPublicUrl(image.getS3Key()))
                        .score(0f) // 요구사항에 따라 score=0
                        .build())
                .collect(Collectors.toList());
    }
    
    /** ChoiceService.toMbti와 동일한 로직으로 MBTI 코드 계산 */
    private String calculateMbtiFromVector(ImageVector image) {
        if (image == null) return null;
        
        float v1 = image.getV1();
        float v2 = image.getV2();
        float v3 = image.getV3();
        float v4 = image.getV4();
        
        String axis1 = (v1 >= 0 ? "S" : "B");
        String axis2 = (v2 >= 0 ? "F" : "C");
        String axis3 = (v3 >= 0 ? "G" : "P");
        String axis4 = (v4 >= 0 ? "E" : "N");
        
        return axis1 + axis2 + axis3 + axis4;
    }
    
    /** 퍼블릭 베이스 URL + s3Key로 최종 URL 조립 (ChoiceService와 동일한 로직) */
    private String toPublicUrl(String s3Key) {
        if (s3Key == null) return null;
        String base = (s3PublicBaseUrl == null) ? "" : s3PublicBaseUrl.trim();
        if (base.isEmpty()) return s3Key; // 임시: 프론트에서 처리하게
        // base 끝/키 앞의 슬래시 중복 방지
        if (base.endsWith("/")) base = base.substring(0, base.length()-1);
        String key = s3Key.startsWith("/") ? s3Key.substring(1) : s3Key;
        return base + "/" + key;
    }

    // --- 내부 유틸 ---

    private float[] safe4(float[] v) {
        float a = (v != null && v.length > 0) ? v[0] : 0f;
        float b = (v != null && v.length > 1) ? v[1] : 0f;
        float c = (v != null && v.length > 2) ? v[2] : 0f;
        float d = (v != null && v.length > 3) ? v[3] : 0f;
        return new float[]{a, b, c, d};
    }
}