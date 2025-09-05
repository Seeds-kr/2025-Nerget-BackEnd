package com.seeds.NergetBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ProfileResponseDto {
    private Long memberId;
    private String email;
    private String nickname;
    private String bio;
    private String avatarUrl;
}