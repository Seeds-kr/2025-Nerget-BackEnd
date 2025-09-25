// src/main/java/com/seeds/NergetBackend/dto/AuthResponseDto.java
package com.seeds.NergetBackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {

    @JsonProperty("accessToken")   // 프론트가 우선적으로 읽는 키
    private String accessToken;

    @JsonProperty("isNewUser")     // 프론트가 인식하는 불린 키
    private boolean isNewUser;
}