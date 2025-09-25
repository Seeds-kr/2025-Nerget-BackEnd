// dto/LoginResponse.java
package com.seeds.NergetBackend.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;   // JWT (Access)
    private Long   expiresIn;     // 만료(초)
    // 필요하면 refreshToken, user info 등 추가
}