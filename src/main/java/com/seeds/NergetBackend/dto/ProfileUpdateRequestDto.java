package com.seeds.NergetBackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;


@Getter
public class ProfileUpdateRequestDto {
    @NotBlank
    @Size(min = 2, max = 20)
    private String nickname;


    @Size(max = 200)
    private String bio; // 자기소개
}