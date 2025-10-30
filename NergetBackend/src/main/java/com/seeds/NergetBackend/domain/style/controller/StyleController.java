package com.seeds.NergetBackend.domain.style.controller;

import com.seeds.NergetBackend.domain.style.dto.SwipeRequest;
import com.seeds.NergetBackend.domain.style.dto.SwipeResponse;
import com.seeds.NergetBackend.domain.style.service.StyleService;
import com.seeds.NergetBackend.shared.security.JwtTokenProvider;
import com.seeds.NergetBackend.domain.auth.repository.MemberRepository;
import com.seeds.NergetBackend.domain.choice.entity.ImageInteraction;
import com.seeds.NergetBackend.domain.choice.repository.ImageInteractionRepository;
import com.seeds.NergetBackend.domain.flow.entity.ImageVector;
import com.seeds.NergetBackend.domain.flow.repository.ImageVectorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/style")
@RequiredArgsConstructor
public class StyleController {

    private final StyleService styleService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final ImageInteractionRepository imageInteractionRepository;
    private final ImageVectorRepository imageVectorRepository;

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

        // 사용자 조회 (Optional - 토큰이 있으면 사용자별 추천, 없으면 전체 추천)
        Long memberId = null;
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String email = jwtTokenProvider.getEmail(token);
                memberId = memberRepository.findByEmail(email)
                        .map(m -> Long.valueOf(m.getMemberId()))
                        .orElse(1L);
                log.info("[/api/style/recommend] user={} (id={}), page={}, limit={}", 
                        email, memberId, page, limit);
            } catch (Exception e) {
                log.warn("[/api/style/recommend] Token validation failed, proceeding without auth: {}", e.getMessage());
                // 토큰이 잘못되었어도 계속 진행 (전체 추천)
            }
        } else {
            log.info("[/api/style/recommend] No auth header, returning general recommendations. page={}, limit={}", 
                    page, limit);
        }

        // memberId가 null이면 1L 사용 (전체 추천)
        if (memberId == null) {
            memberId = 1L;
        }

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

    /**
     * 사용자 프로필 이미지 조회
     * GET /api/users/profile/image
     * Headers: Authorization: Bearer <token>
     */
    @GetMapping("/users/profile/image")
    public ResponseEntity<?> getProfileImage(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        try {
            // 사용자 조회
            Long memberId = getMemberIdFromAuth(authHeader);
            if (memberId == null) {
                return ResponseEntity.status(401).body(Map.of(
                        "error", "UNAUTHORIZED",
                        "message", "Authorization required"
                ));
            }

            // 사용자의 좋아요한 이미지 중 가장 최근 것 가져오기
            Optional<ImageInteraction> recentLike = imageInteractionRepository.findAll()
                    .stream()
                    .filter(i -> i.getMemberId().equals(memberId) && i.getAction() == 1) // 좋아요만
                    .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                    .findFirst();

            if (recentLike.isPresent()) {
                String imageVectorId = recentLike.get().getImageVectorId();
                Optional<ImageVector> iv = imageVectorRepository.findById(imageVectorId);
                
                if (iv.isPresent()) {
                    return ResponseEntity.ok(Map.of(
                            "imageUrl", iv.get().getS3Key(),
                            "imageId", iv.get().getId()
                    ));
                }
            }

            // 프로필 이미지 없으면 빈 응답
            return ResponseEntity.status(204).build();
            
        } catch (Exception e) {
            log.error("[/api/users/profile/image] Error", e);
            return ResponseEntity.status(500).body(Map.of(
                    "error", "INTERNAL_ERROR",
                    "message", "Failed to get profile image"
            ));
        }
    }

    /**
     * 사용자 스타일 이미지 목록 조회
     * GET /api/users/images
     * Headers: Authorization: Bearer <token>
     */
    @GetMapping("/users/images")
    public ResponseEntity<?> getUserImages(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        try {
            // 사용자 조회
            Long memberId = getMemberIdFromAuth(authHeader);
            if (memberId == null) {
                return ResponseEntity.status(401).body(Map.of(
                        "error", "UNAUTHORIZED",
                        "message", "Authorization required"
                ));
            }

            // 사용자의 좋아요한 이미지들
            List<String> likedImageIds = imageInteractionRepository.findAll()
                    .stream()
                    .filter(i -> i.getMemberId().equals(memberId) && i.getAction() == 1)
                    .map(ImageInteraction::getImageVectorId)
                    .collect(Collectors.toList());

            // ImageVector 조회
            List<ImageVector> imageVectors = imageVectorRepository.findByIdIn(likedImageIds);

            // DTO 변환
            List<Map<String, Object>> images = imageVectors.stream()
                    .map(iv -> {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", iv.getId());
                        item.put("imageUrl", iv.getS3Key());
                        item.put("createdAt", iv.getCreatedAt());
                        return item;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "images", images,
                    "count", images.size()
            ));
            
        } catch (Exception e) {
            log.error("[/api/users/images] Error", e);
            return ResponseEntity.status(500).body(Map.of(
                    "error", "INTERNAL_ERROR",
                    "message", "Failed to get user images"
            ));
        }
    }

    /**
     * Authorization 헤더에서 memberId 추출
     */
    private Long getMemberIdFromAuth(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        try {
            String token = authHeader.substring(7);
            String email = jwtTokenProvider.getEmail(token);
            return memberRepository.findByEmail(email)
                    .map(m -> Long.valueOf(m.getMemberId()))
                    .orElse(null);
        } catch (Exception e) {
            log.warn("Failed to get memberId from token", e);
            return null;
        }
    }
}


