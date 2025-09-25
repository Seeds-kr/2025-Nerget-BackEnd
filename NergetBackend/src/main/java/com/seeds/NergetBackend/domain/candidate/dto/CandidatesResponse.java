package com.seeds.NergetBackend.domain.candidate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CandidatesResponse {
    private List<CandidateImageDto> images; // 보통 12개
}