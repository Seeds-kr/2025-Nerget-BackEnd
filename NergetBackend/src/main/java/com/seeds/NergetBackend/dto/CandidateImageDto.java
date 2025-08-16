package com.seeds.NergetBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CandidateImageDto {
    private String id;   // 후보 이미지 식별자
    private String url;  // 표시용 URL
}
