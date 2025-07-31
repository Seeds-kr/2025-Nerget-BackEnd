package com.seeds.NergetBackend.controller;

import com.seeds.NergetBackend.dto.SwipeRequestDto;
import com.seeds.NergetBackend.service.SwipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/swipe")
public class SwipeController {

    private final SwipeService swipeService;

    // 스와이프 피드백 저장
    @PostMapping
    public ResponseEntity<String> saveSwipeFeedback(@RequestBody SwipeRequestDto dto) {
        swipeService.saveFeedback(dto);
        return ResponseEntity.ok("피드백 저장 완료");
    }
}