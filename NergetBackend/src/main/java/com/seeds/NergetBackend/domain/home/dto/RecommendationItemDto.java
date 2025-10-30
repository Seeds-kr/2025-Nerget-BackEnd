// src/main/java/com/seeds/NergetBackend/dto/RecommendationItemDto.java
package com.seeds.NergetBackend.domain.home.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RecommendationItemDto {
    private String id;        // ImageVector.id (UUID) - 클라이언트 호환성
    private String imageId;   // ImageVector.id (UUID) - 동일 값
    private String imageUrl;  // S3 퍼블릭 또는 프리사인드 URL
    private float score;      // 추천 점수(디버깅/AB테스트용)
}