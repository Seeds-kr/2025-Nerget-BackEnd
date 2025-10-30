package com.seeds.NergetBackend.domain.style.controller;

import com.seeds.NergetBackend.domain.style.dto.SwipeRequest;
import com.seeds.NergetBackend.domain.style.dto.SwipeResponse;
import com.seeds.NergetBackend.domain.style.service.StyleService;
import com.seeds.NergetBackend.shared.security.JwtTokenProvider;
import com.seeds.NergetBackend.domain.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/style")
@RequiredArgsConstructor
public class StyleController {

    private final StyleService styleService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    /**
     * 스타일 스와이프 처리 (좋아요/싫어요)
     * 
     * 요청 형식:
     * POST /api/style/swipe
     * Headers: Authorization: Bearer <token>
     * Body: { "itemId": "string|number", "liked": true|false }
     */
    @PostMapping("/swipe")
    public ResponseEntity<?> swipe(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody SwipeRequest request
    ) {
        // 1. Authorization 검증
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("[/api/style/swipe] Missing or invalid Authorization header");
            return ResponseEntity.status(401).body(Map.of(
                    "error", "UNAUTHORIZED",
                    "message", "Authorization header is required"
            ));
        }

        String token = authHeader.substring(7);
        String email;
        try {
            email = jwtTokenProvider.getEmail(token);
        } catch (Exception e) {
            log.error("[/api/style/swipe] Token validation failed", e);
            return ResponseEntity.status(401).body(Map.of(
                    "error", "UNAUTHORIZED",
                    "message", "Invalid or expired token"
            ));
        }

        // 2. 사용자 조회
        Long memberId = memberRepository.findByEmail(email)
                .map(m -> Long.valueOf(m.getMemberId()))
                .orElse(null);

        if (memberId == null) {
            log.error("[/api/style/swipe] User not found: {}", email);
            return ResponseEntity.status(401).body(Map.of(
                    "error", "UNAUTHORIZED",
                    "message", "User not found"
            ));
        }

        // 3. 요청 검증
        if (request.getItemId() == null || request.getItemId().isBlank()) {
            log.warn("[/api/style/swipe] Missing itemId");
            return ResponseEntity.status(400).body(Map.of(
                    "error", "INVALID_REQUEST",
                    "message", "itemId is required"
            ));
        }

        if (request.getLiked() == null) {
            log.warn("[/api/style/swipe] Missing liked");
            return ResponseEntity.status(400).body(Map.of(
                    "error", "INVALID_REQUEST",
                    "message", "liked is required"
            ));
        }

        // 4. 스와이프 처리
        try {
            log.info("[/api/style/swipe] user={} (id={}), itemId={}, liked={}", 
                    email, memberId, request.getItemId(), request.getLiked());
            
            styleService.recordSwipe(memberId, request.getItemId(), request.getLiked());
            
            return ResponseEntity.ok(new SwipeResponse(true));
            
        } catch (IllegalArgumentException e) {
            log.warn("[/api/style/swipe] Invalid itemId: {}", e.getMessage());
            return ResponseEntity.status(400).body(Map.of(
                    "error", "INVALID_REQUEST",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("[/api/style/swipe] Internal error", e);
            return ResponseEntity.status(500).body(Map.of(
                    "error", "INTERNAL_ERROR",
                    "message", "An error occurred processing the swipe"
            ));
        }
    }

    /**
     * 스타일 추천 API
     * 
     * GET /api/style/recommend?page=0&limit=12
     * Headers: Authorization: Bearer <token>
     */
    @GetMapping("/recommend")
    public ResponseEntity<?> recommend(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int limit
    ) {
        // 페이지/리미트 유효성 검증
        if (page < 0) page = 0;
        if (limit < 1) limit = 12;
        if (limit > 50) limit = 50;

        // Authorization 검증
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("[/api/style/recommend] Missing or invalid Authorization header");
            return ResponseEntity.status(401).body(Map.of(
                    "error", "UNAUTHORIZED",
                    "message", "Authorization header is required"
            ));
        }

        String token = authHeader.substring(7);
        String email;
        try {
            email = jwtTokenProvider.getEmail(token);
        } catch (Exception e) {
            log.error("[/api/style/recommend] Token validation failed", e);
            return ResponseEntity.status(401).body(Map.of(
                    "error", "UNAUTHORIZED",
                    "message", "Invalid or expired token"
            ));
        }

        // 사용자 조회
        Long memberId = memberRepository.findByEmail(email)
                .map(m -> Long.valueOf(m.getMemberId()))
                .orElse(1L); // 테스트 편의상 기본값

        log.info("[/api/style/recommend] user={} (id={}), page={}, limit={}", 
                email, memberId, page, limit);

        try {
            return styleService.getRecommendations(memberId, page, limit);
        } catch (Exception e) {
            log.error("[/api/style/recommend] Internal error", e);
            return ResponseEntity.status(500).body(Map.of(
                    "error", "INTERNAL_ERROR",
                    "message", "An error occurred fetching recommendations"
            ));
        }
    }
}

