package com.seeds.NergetBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @Builder @AllArgsConstructor
public class LikedPostSummaryDto {
    private final Long likeRowId;     // 좋아요 테이블의 PK (커서용)
    private final Long postId;
    private final String title;
    private final String imageUrl;
    private final LocalDateTime postCreatedAt;
    private final int likeCount;
    private final int commentCount;
}