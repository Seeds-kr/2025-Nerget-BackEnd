package com.seeds.NergetBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @Builder @AllArgsConstructor
public class CommentSummaryDto {
    private final Long id;
    private final Long postId;
    private final String postTitle;
    private final String content;
    private final LocalDateTime createdAt;
}