// src/main/java/com/seeds/NergetBackend/dto/AuthResponseDto.java
package com.seeds.NergetBackend.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "인증 응답 DTO")
public class AuthResponseDto {

    @JsonProperty("accessToken")   // 프론트가 우선적으로 읽는 키
    @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @JsonProperty("isNewUser")     // 프론트가 인식하는 불린 키
    @Schema(description = "신규 사용자 여부", example = "false")
    private boolean isNewUser;
}