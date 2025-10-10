package com.seeds.NergetBackend.domain.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MbtiTypeDto {

    private String code;        // 예: "SFGE"
    private String keyword;     // 예: "미니멀 모던"
    private String description; // 설명 (선택)
}

