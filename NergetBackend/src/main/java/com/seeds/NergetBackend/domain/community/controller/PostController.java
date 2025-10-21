package com.seeds.NergetBackend.domain.community.controller;

import com.seeds.NergetBackend.domain.community.dto.*;
import com.seeds.NergetBackend.domain.community.service.BookmarkService;
import com.seeds.NergetBackend.domain.community.service.LikeService;
import com.seeds.NergetBackend.domain.community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final LikeService likeService;
    private final BookmarkService bookmarkService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestParam(required = false) Integer memberId,
            @RequestBody PostCreateRequest request) {
        
        // TODO: JWT에서 memberId 추출
        if (memberId == null) memberId = 1;
        
        PostResponse response = postService.createPost(memberId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PostListResponse> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer memberId) {
        
        Pageable pageable = PageRequest.of(page, size);
        PostListResponse response = postService.getPosts(pageable, memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(
            @PathVariable Long id,
            @RequestParam(required = false) Integer memberId) {
        
        PostResponse response = postService.getPost(id, memberId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @RequestParam(required = false) Integer memberId,
            @RequestBody PostUpdateRequest request) {
        
        // TODO: JWT에서 memberId 추출
        if (memberId == null) memberId = 1;
        
        PostResponse response = postService.updatePost(id, memberId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @RequestParam(required = false) Integer memberId) {
        
        // TODO: JWT에서 memberId 추출
        if (memberId == null) memberId = 1;
        
        postService.deletePost(id, memberId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Long id,
            @RequestParam(required = false) Integer memberId) {
        
        // TODO: JWT에서 memberId 추출
        if (memberId == null) memberId = 1;
        
        boolean isLiked = likeService.toggleLike(id, memberId);
        return ResponseEntity.ok(Map.of("isLiked", isLiked));
    }

    @PostMapping("/{id}/bookmark")
    public ResponseEntity<Map<String, Object>> toggleBookmark(
            @PathVariable Long id,
            @RequestParam(required = false) Integer memberId) {
        
        // TODO: JWT에서 memberId 추출
        if (memberId == null) memberId = 1;
        
        boolean isBookmarked = bookmarkService.toggleBookmark(id, memberId);
        return ResponseEntity.ok(Map.of("isBookmarked", isBookmarked));
    }
}

