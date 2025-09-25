package com.seeds.NergetBackend.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleLoginDto {
    private String idToken;
}