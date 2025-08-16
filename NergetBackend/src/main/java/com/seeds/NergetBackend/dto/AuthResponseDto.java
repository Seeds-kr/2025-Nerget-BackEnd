package com.seeds.NergetBackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponseDto {
    private String token;
    private String nickname;

    @JsonProperty("isNew")   // ✅ JSON 키를 isNew로 고정
    private boolean isNew;
}