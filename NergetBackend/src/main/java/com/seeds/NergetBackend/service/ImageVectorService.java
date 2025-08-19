// src/main/java/com/seeds/NergetBackend/service/ImageVectorService.java
package com.seeds.NergetBackend.service;

import com.seeds.NergetBackend.entity.ImageVector;
import com.seeds.NergetBackend.repository.ImageVectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageVectorService {

    private final ImageVectorRepository repo;

    /** 업로드 직후: PENDING 레코드 생성 (AI 처리 대기) */
    @Transactional
    public ImageVector registerPending(String userId, String s3Key, String metaJson) {
        ImageVector iv = ImageVector.builder()
                .userId(userId)
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
        return s3Keys.stream()
                .map(k -> registerPending(userId, k, metaJson))
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

    // --- 내부 유틸 ---

    private float[] safe4(float[] v) {
        float a = (v != null && v.length > 0) ? v[0] : 0f;
        float b = (v != null && v.length > 1) ? v[1] : 0f;
        float c = (v != null && v.length > 2) ? v[2] : 0f;
        float d = (v != null && v.length > 3) ? v[3] : 0f;
        return new float[]{a, b, c, d};
    }
}