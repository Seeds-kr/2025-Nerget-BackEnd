package com.seeds.NergetBackend.service;

import com.seeds.NergetBackend.dto.SwipeRequestDto;

public interface SwipeService {
    void saveFeedback(SwipeRequestDto dto);
}