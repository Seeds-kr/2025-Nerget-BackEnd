package com.seeds.NergetBackend.domain.community.controller;

import com.seeds.NergetBackend.domain.community.dto.CommentListResponse;
import com.seeds.NergetBackend.domain.community.dto.CommentRequest;
import com.seeds.NergetBackend.domain.community.dto.CommentResponse;
import com.seeds.NergetBackend.domain.community.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long postId,
            @RequestParam(required = false) Integer memberId,
            @RequestBody CommentRequest request) {
        
        // TODO: JWT에서 memberId 추출
        if (memberId == null) memberId = 1;
        
        CommentResponse response = commentService.createComment(postId, memberId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<CommentListResponse> getComments(
            @PathVariable Long postId) {
        
        CommentListResponse response = commentService.getComments(postId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestParam(required = false) Integer memberId,
            @RequestBody CommentRequest request) {
        
        // TODO: JWT에서 memberId 추출
        if (memberId == null) memberId = 1;
        
        CommentResponse response = commentService.updateComment(commentId, memberId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @RequestParam(required = false) Integer memberId) {
        
        // TODO: JWT에서 memberId 추출
        if (memberId == null) memberId = 1;
        
        commentService.deleteComment(commentId, memberId);
        return ResponseEntity.ok().build();
    }
}

