package com.seeds.NergetBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StyleAnalysisResponseDto {
    private String vectorData;
    private String message;
}