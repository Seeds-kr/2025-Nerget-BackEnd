package com.seeds.NergetBackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MbtiResultDto {
    private String mbti;        // 예: "INTP"
    private String explanation; // 간단 설명(모킹 가능)
    private float[] finalVector;
}