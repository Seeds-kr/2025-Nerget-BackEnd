package com.seeds.NergetBackend.dto;

import lombok.Data;

@Data
public class SwipeRequestDto {
    private Integer memberId;       // 어떤 사용자인지
    private Integer styleId;        // 어떤 스타일 이미지에 대해
    private String direction;       // "LIKE" or "DISLIKE"
}