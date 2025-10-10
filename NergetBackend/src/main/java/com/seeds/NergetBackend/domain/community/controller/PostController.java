package com.seeds.NergetBackend.domain.community.controller;

import com.seeds.NergetBackend.domain.community.dto.*;
import com.seeds.NergetBackend.domain.community.service.BookmarkService;
import com.seeds.NergetBackend.domain.community.service.LikeService;
import com.seeds.NergetBackend.domain.community.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "게시글", description = "커뮤니티 게시글 관리 API")
public class PostController {

    private final PostService postService;
    private final LikeService likeService;
    private final BookmarkService bookmarkService;

    @Operation(summary = "게시글 작성", description = "이미지 1장과 설명을 포함한 게시글을 작성합니다.")
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @Parameter(description = "현재 사용자 ID (임시, 실제는 JWT에서 추출)", example = "1")
            @RequestParam(required = false) Integer memberId,
            @RequestBody PostCreateRequest request) {
        
        // TODO: JWT에서 memberId 추출
        if (memberId == null) memberId = 1;
        
        PostResponse response = postService.createPost(memberId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글 목록 조회", description = "커뮤니티의 모든 게시글을 최신순으로 조회합니다.")
    @GetMapping
    public ResponseEntity<PostListResponse> getPosts(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "현재 사용자 ID (임시)", example = "1")
            @RequestParam(required = false) Integer memberId) {
        
        Pageable pageable = PageRequest.of(page, size);
        PostListResponse response = postService.getPosts(pageable, memberId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글 상세 조회", description = "특정 게시글의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "현재 사용자 ID (임시)", example = "1")
            @RequestParam(required = false) Integer memberId) {
        
        PostResponse response = postService.getPost(id, memberId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글 수정", description = "내가 작성한 게시글의 내용을 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "현재 사용자 ID (임시)", example = "1")
            @RequestParam(required = false) Integer memberId,
            @RequestBody PostUpdateRequest request) {
        
        // TODO: JWT에서 memberId 추출
        if (memberId == null) memberId = 1;
        
        PostResponse response = postService.updatePost(id, memberId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글 삭제", description = "내가 작성한 게시글을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "현재 사용자 ID (임시)", example = "1")
            @RequestParam(required = false) Integer memberId) {
        
        // TODO: JWT에서 memberId 추출
        if (memberId == null) memberId = 1;
        
        postService.deletePost(id, memberId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 좋아요", description = "게시글에 좋아요를 추가하거나 취소합니다.")
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "현재 사용자 ID (임시)", example = "1")
            @RequestParam(required = false) Integer memberId) {
        
        // TODO: JWT에서 memberId 추출
        if (memberId == null) memberId = 1;
        
        boolean isLiked = likeService.toggleLike(id, memberId);
        return ResponseEntity.ok(Map.of("isLiked", isLiked));
    }

    @Operation(summary = "게시글 북마크", description = "게시글을 북마크에 저장하거나 취소합니다.")
    @PostMapping("/{id}/bookmark")
    public ResponseEntity<Map<String, Object>> toggleBookmark(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "현재 사용자 ID (임시)", example = "1")
            @RequestParam(required = false) Integer memberId) {
        
        // TODO: JWT에서 memberId 추출
        if (memberId == null) memberId = 1;
        
        boolean isBookmarked = bookmarkService.toggleBookmark(id, memberId);
        return ResponseEntity.ok(Map.of("isBookmarked", isBookmarked));
    }
}

