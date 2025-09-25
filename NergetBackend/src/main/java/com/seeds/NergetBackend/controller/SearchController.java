package com.seeds.NergetBackend.controller;

import com.seeds.NergetBackend.dto.RecommendationItemDto;
import com.seeds.NergetBackend.service.ImageVectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final ImageVectorService imageVectorService;

    @GetMapping("/mbti/{code}")
    public ResponseEntity<List<RecommendationItemDto>> searchByMbti(
            @PathVariable String code,
            @RequestParam(defaultValue = "24") int limit) {
        
        // MBTI 코드 정규화 (대문자)
        String normalizedCode = code != null ? code.toUpperCase().trim() : "";
        
        // 유효성 검사
        if (normalizedCode.length() != 4) {
            return ResponseEntity.badRequest().build();
        }
        
        // MBTI 코드 패턴 검증 (S/B, F/C, G/P, E/N)
        if (!isValidMbtiCode(normalizedCode)) {
            return ResponseEntity.badRequest().build();
        }
        
        List<RecommendationItemDto> results = imageVectorService.findByMbtiCode(normalizedCode, limit);
        return ResponseEntity.ok(results);
    }
    
    private boolean isValidMbtiCode(String code) {
        if (code == null || code.length() != 4) return false;
        
        char[] chars = code.toCharArray();
        return (chars[0] == 'S' || chars[0] == 'B') &&
               (chars[1] == 'F' || chars[1] == 'C') &&
               (chars[2] == 'G' || chars[2] == 'P') &&
               (chars[3] == 'E' || chars[3] == 'N');
    }
}
