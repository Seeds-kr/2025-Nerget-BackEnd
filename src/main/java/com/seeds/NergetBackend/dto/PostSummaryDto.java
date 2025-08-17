package com.seeds.NergetBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @Builder @AllArgsConstructor
public class PostSummaryDto {
    private final Long id;
    private final String title;
    private final String imageUrl;
    private final LocalDateTime createdAt;
    private final int likeCount;
    private final int commentCount;
}
