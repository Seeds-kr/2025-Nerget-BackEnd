// src/main/java/com/seeds/NergetBackend/dto/RecommendationsResponse.java
package com.seeds.NergetBackend.domain.home.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RecommendationsResponse {
    private Long memberId;
    private List<RecommendationItemDto> items;
}