package com.seeds.NergetBackend.domain.search.controller;

import com.seeds.NergetBackend.domain.home.dto.RecommendationItemDto;
import com.seeds.NergetBackend.domain.flow.service.ImageVectorService;
import com.seeds.NergetBackend.domain.search.dto.MbtiTypeDto;
import com.seeds.NergetBackend.domain.search.dto.MbtiTypesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SearchController {

    private final ImageVectorService imageVectorService;

    @GetMapping("/style/types")
    public ResponseEntity<MbtiTypesResponse> getMbtiTypes() {
        List<MbtiTypeDto> types = new ArrayList<>();
        
        // 16가지 MBTI 타입 정의 (ChoiceService의 styleKeywordOf와 동일)
        // S(Showy/화려), B(Basic/단조), F(Formal/미니멀), C(Comfort/맥시멀)
        // G(Glamorous/트렌디), P(Practical/클래식), E(Experimental/도전), N(Natural/안정)
        
        // S (Showy/화려) 계열
        types.add(MbtiTypeDto.builder().code("SFGE").keyword("모던 시크").build());
        types.add(MbtiTypeDto.builder().code("SFGN").keyword("세련된 모던").build());
        types.add(MbtiTypeDto.builder().code("SFPE").keyword("엘레강스").build());
        types.add(MbtiTypeDto.builder().code("SFPN").keyword("미니멀 클래식").build());
        types.add(MbtiTypeDto.builder().code("SCGE").keyword("아방가르드").build());
        types.add(MbtiTypeDto.builder().code("SCGN").keyword("화려한 캐주얼").build());
        types.add(MbtiTypeDto.builder().code("SCPE").keyword("럭셔리 빈티지").build());
        types.add(MbtiTypeDto.builder().code("SCPN").keyword("클래식 포멀").build());
        
        // B (Basic/단조) 계열
        types.add(MbtiTypeDto.builder().code("BFGE").keyword("미니멀 트렌디").build());
        types.add(MbtiTypeDto.builder().code("BFGN").keyword("심플 모던").build());
        types.add(MbtiTypeDto.builder().code("BFPE").keyword("절제된 클래식").build());
        types.add(MbtiTypeDto.builder().code("BFPN").keyword("심플 베이직").build());
        types.add(MbtiTypeDto.builder().code("BCGE").keyword("보헤미안").build());
        types.add(MbtiTypeDto.builder().code("BCGN").keyword("캐주얼 모던").build());
        types.add(MbtiTypeDto.builder().code("BCPE").keyword("빈티지 믹스").build());
        types.add(MbtiTypeDto.builder().code("BCPN").keyword("편안한 베이직").build());
        
        return ResponseEntity.ok(MbtiTypesResponse.builder().types(types).build());
    }

    @GetMapping("/style/types/{id}")
    public ResponseEntity<List<RecommendationItemDto>> searchByMbti(
            @PathVariable("id") String code,
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
