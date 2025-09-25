// src/main/java/com/seeds/NergetBackend/controller/HomeController.java
package com.seeds.NergetBackend.controller;

import com.seeds.NergetBackend.dto.RecommendationItemDto;
import com.seeds.NergetBackend.dto.RecommendationsResponse;
import com.seeds.NergetBackend.service.ChoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final ChoiceService choiceService;

    /**
     * 홈 피드 추천
     * - 실제에선 인증에서 memberId를 꺼내 쓰면 되고,
     * - 테스트 중이면 쿼리스트링/헤더로 memberId 받는 것도 OK
     */
    @GetMapping("/recommendations")
    public ResponseEntity<RecommendationsResponse> recommendations(
            @RequestParam(name = "memberId", required = false) Long memberIdParam,
            @RequestParam(name = "limit", required = false, defaultValue = "12") int limit,
            Principal principal
    ) {
        Long memberId = resolveMemberId(memberIdParam, principal);

        // ChoiceService 호출 (메서드! 괄호 필수)
        List<ChoiceService.RecommendationItem> recs =
                choiceService.recommendHomeFeed(memberId, limit);

        // 내부 static DTO → 외부 응답 DTO로 매핑
        List<RecommendationItemDto> items = recs.stream()
                .map(r -> RecommendationItemDto.builder()
                        .imageId(r.imageId)
                        .imageUrl(r.imageUrl)
                        .score(r.score)
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                RecommendationsResponse.builder()
                        .memberId(memberId)
                        .items(items)
                        .build()
        );
    }

    private Long resolveMemberId(Long memberIdParam, Principal principal) {
        if (memberIdParam != null) return memberIdParam;
        // 실제 환경: principal에서 사용자 식별 → Member 조회 후 id 반환
        // 지금은 테스트 편의상 1L 고정
        return 1L;
    }
}