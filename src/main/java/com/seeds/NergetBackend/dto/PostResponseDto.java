package com.seeds.NergetBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostResponseDto {
    private Long postId;
    private String title;
    private String content;
    private String imageUrl;
    private String authorNickname;
    private LocalDateTime createdAt;
    private int viewCount;
    private int likeCount;
    private int commentCount;
}
