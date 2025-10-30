package com.seeds.NergetBackend.domain.style.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwipeRequest {
    private String itemId;    // 문자열 또는 숫자 ID 허용
    private Boolean liked;    // true 또는 false
}

