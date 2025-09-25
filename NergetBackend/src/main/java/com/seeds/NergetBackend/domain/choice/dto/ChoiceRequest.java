package com.seeds.NergetBackend.domain.choice.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class ChoiceRequest {
    private String jobId;             // 초기 4장 분석 잡 아이디
    private List<String> imageIds;    // 스와이프에서 고른 12장(URI나 ID)
}