package com.seeds.NergetBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PendingDto {
    private String status;   // "INITIAL_PENDING"
    private String message;  // "초기 분석이 아직 완료되지 않았습니다."
}