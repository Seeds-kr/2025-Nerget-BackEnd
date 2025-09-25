// src/main/java/com/seeds/NergetBackend/dto/MbtiResultDto.java
package com.seeds.NergetBackend.domain.choice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MbtiResultDto {

    /** 최종 MBTI 문자열 (예: "SFGN") */
    private String mbti;

    /** 부가 설명 (간단 텍스트) */
    private String explanation;

    /** 최종 벡터값 (4차원) */
    private float[] finalVector;
}