package com.seeds.NergetBackend.domain.style.service;

import com.seeds.NergetBackend.domain.choice.entity.ImageInteraction;
import com.seeds.NergetBackend.domain.choice.entity.MemberPrefVector;
import com.seeds.NergetBackend.domain.choice.repository.ImageInteractionRepository;
import com.seeds.NergetBackend.domain.choice.repository.MemberPrefVectorRepository;
import com.seeds.NergetBackend.domain.flow.entity.ImageVector;
import com.seeds.NergetBackend.domain.flow.repository.ImageVectorRepository;
import com.seeds.NergetBackend.domain.home.dto.RecommendationItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StyleService {

    private final ImageInteractionRepository imageInteractionRepository;
    private final ImageVectorRepository imageVectorRepository;
    private final MemberPrefVectorRepository memberPrefVectorRepository;

    @Value("${app.s3.public-base-url:}")
    private String s3PublicBaseUrl;

    /**
     * 스와이프 기록 처리
     * - itemId는 문자열 또는 숫자 ID 허용
     * - 존재하지 않는 아이템도 graceful하게 처리
     */
    @Transactional
    public void recordSwipe(Long memberId, String itemId, Boolean liked) {
        log.info("Record swipe: memberId={}, itemId={}, liked={}", memberId, itemId, liked);

        // ImageVector 존재 여부 확인
        Optional<ImageVector> imageVector = imageVectorRepository.findById(itemId);
        
        if (!imageVector.isPresent()) {
            log.warn("ImageVector not found for itemId: {}. Skipping storage but not throwing error.", itemId);
            // 미등록 아이템도 graceful하게 처리 (기록하지 않음 또는 특별 처리)
            // 실제 비즈니스 정책에 따라 변경 가능
            return;
        }

        ImageVector iv = imageVector.get();
        String imageVectorId = iv.getId();

        // 기존 interaction 확인 (upsert 방식)
        ImageInteraction existing = imageInteractionRepository
                .findByMemberIdAndImageVectorId(memberId, imageVectorId)
                .orElse(null);

        if (existing != null) {
            // 기존 기록 업데이트
            existing.setAction(liked ? 1 : -1);
            existing.setWeight(1.0);
            existing.setCreatedAt(LocalDateTime.now());
            imageInteractionRepository.save(existing);
            log.info("Updated existing interaction for memberId={}, imageVectorId={}", 
                    memberId, imageVectorId);
        } else {
            // 새 기록 생성
            ImageInteraction interaction = ImageInteraction.builder()
                    .memberId(memberId)
                    .imageVectorId(imageVectorId)
                    .action(liked ? 1 : -1)
                    .weight(1.0)
                    .createdAt(LocalDateTime.now())
                    .build();
            imageInteractionRepository.save(interaction);
            log.info("Created new interaction for memberId={}, imageVectorId={}", 
                    memberId, imageVectorId);
        }

        // 프리퍼런스 벡터 업데이트 (선택적)
        updateMemberPrefVector(memberId);
    }

    /**
     * 사용자 취향 벡터 업데이트
     */
    private void updateMemberPrefVector(Long memberId) {
        // 모든 스와이프 기록 조회
        List<ImageInteraction> interactions = imageInteractionRepository
                .findAll()
                .stream()
                .filter(i -> i.getMemberId().equals(memberId))
                .collect(Collectors.toList());

        if (interactions.isEmpty()) {
            log.debug("No interactions found for memberId={}, skipping pref update", memberId);
            return;
        }

        // 좋아요/싫어요 벡터 분리
        List<float[]> likeVectors = new ArrayList<>();
        List<float[]> dislikeVectors = new ArrayList<>();

        for (ImageInteraction interaction : interactions) {
            // ImageVector를 DB에서 직접 조회
            Optional<ImageVector> iv = imageVectorRepository.findById(interaction.getImageVectorId());
            if (!iv.isPresent()) continue;

            float[] v = iv.get().toArray();
            
            if (interaction.getAction() == 1) {
                // 좋아요
                likeVectors.add(v);
            } else if (interaction.getAction() == -1) {
                // 싫어요
                dislikeVectors.add(v);
            }
        }

        // 평균 계산
        float[] meanLike = average(likeVectors);
        float[] meanDislike = average(dislikeVectors);

        // 프리퍼런스 벡터 계산 (좋아요 - 싫어요)
        float[] prefVector = new float[4];
        for (int i = 0; i < 4; i++) {
            prefVector[i] = meanLike[i] - meanDislike[i] * 0.5f; // 싫어요는 더 약하게 반영
        }

        // L2 정규화
        float norm = (float) Math.sqrt(
                prefVector[0] * prefVector[0] +
                prefVector[1] * prefVector[1] +
                prefVector[2] * prefVector[2] +
                prefVector[3] * prefVector[3]
        );
        
        if (norm > 0.001f) {
            for (int i = 0; i < 4; i++) {
                prefVector[i] /= norm;
            }
        }

        // DB에 저장 또는 업데이트
        MemberPrefVector memberPref = memberPrefVectorRepository.findById(memberId)
                .orElse(MemberPrefVector.builder()
                        .memberId(memberId)
                        .build());

        memberPref.setV1(prefVector[0]);
        memberPref.setV2(prefVector[1]);
        memberPref.setV3(prefVector[2]);
        memberPref.setV4(prefVector[3]);

        memberPrefVectorRepository.save(memberPref);
        log.info("Updated preference vector for memberId={}", memberId);
    }

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

    /**
     * 추천 결과 반환
     */
    public ResponseEntity<?> getRecommendations(Long memberId, int page, int limit) {
        try {
            // 사용자 취향 벡터 조회
            MemberPrefVector pref = memberPrefVectorRepository.findById(memberId)
                    .orElse(null);

            List<RecommendationItemDto> items;
            int totalPages = 0;

            if (pref != null) {
                // 벡터 기반 추천
                List<ImageVector> topImages;
                try {
                    topImages = imageVectorRepository.findTopByPrefVector(
                            pref.getV1(), pref.getV2(), pref.getV3(), pref.getV4(), 
                            limit * (page + 1) // 페이지네이션 처리
                    );
                } catch (Exception e) {
                    log.warn("Native query failed, using fallback", e);
                    List<ImageVector> pool = imageVectorRepository.findAll();
                    float p1 = pref.getV1(), p2 = pref.getV2(), p3 = pref.getV3(), p4 = pref.getV4();
                    pool.sort((a, b) -> Float.compare(
                            dotProduct(b, p1, p2, p3, p4),
                            dotProduct(a, p1, p2, p3, p4)
                    ));
                    int toIdx = Math.min(limit * (page + 1), pool.size());
                    topImages = pool.subList(0, toIdx);
                }

                // 현재 페이지 아이템만 추출
                int fromIdx = page * limit;
                int toIdx = Math.min(fromIdx + limit, topImages.size());
                List<ImageVector> pageImages = topImages.subList(fromIdx, toIdx);

                // 전체 페이지 수 계산
                long totalItems = imageVectorRepository.count();
                totalPages = (int) Math.ceil((double) totalItems / limit);

                // DTO 변환
                float p1 = pref.getV1(), p2 = pref.getV2(), p3 = pref.getV3(), p4 = pref.getV4();
                items = pageImages.stream()
                        .map(iv -> RecommendationItemDto.builder()
                                .id(iv.getId())
                                .imageId(iv.getId())
                                .imageUrl(toPublicUrl(iv.getS3Key()))
                                .score(dotProduct(iv, p1, p2, p3, p4))
                                .build())
                        .collect(Collectors.toList());
            } else {
                // 프리퍼런스가 없으면 최신 이미지 반환
                List<ImageVector> allImages = imageVectorRepository.findAll();
                allImages.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

                int fromIdx = page * limit;
                int toIdx = Math.min(fromIdx + limit, allImages.size());
                List<ImageVector> pageImages = allImages.subList(fromIdx, toIdx);

                totalPages = (int) Math.ceil((double) allImages.size() / limit);

                items = pageImages.stream()
                        .map(iv -> RecommendationItemDto.builder()
                                .id(iv.getId())
                                .imageId(iv.getId())
                                .imageUrl(toPublicUrl(iv.getS3Key()))
                                .score(0.0f)
                                .build())
                        .collect(Collectors.toList());
            }

            // 응답 구성
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("items", items);
            responseBody.put("recommendations", items); // 호환성을 위해
            responseBody.put("page", page);
            responseBody.put("limit", limit);
            responseBody.put("totalPages", totalPages);

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            log.error("Error getting recommendations", e);
            throw e;
        }
    }

    private float dotProduct(ImageVector iv, float p1, float p2, float p3, float p4) {
        return iv.getV1() * p1 + iv.getV2() * p2 + iv.getV3() * p3 + iv.getV4() * p4;
    }

    private String toPublicUrl(String s3Key) {
        if (s3Key == null) return null;
        
        // 이미 완전한 URL이면 그대로 반환
        if (s3Key.startsWith("http://") || s3Key.startsWith("https://")) {
            return s3Key;
        }
        
        String base = (s3PublicBaseUrl == null) ? "" : s3PublicBaseUrl.trim();
        if (base.isEmpty()) return s3Key;
        
        if (base.endsWith("/")) base = base.substring(0, base.length() - 1);
        String key = s3Key.startsWith("/") ? s3Key.substring(1) : s3Key;
        return base + "/" + key;
    }
}

