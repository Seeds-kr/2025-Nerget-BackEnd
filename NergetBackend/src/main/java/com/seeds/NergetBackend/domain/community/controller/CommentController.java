package com.seeds.NergetBackend.domain.community.controller;

import com.seeds.NergetBackend.domain.community.dto.CommentListResponse;
import com.seeds.NergetBackend.domain.community.dto.CommentRequest;
import com.seeds.NergetBackend.domain.community.dto.CommentResponse;
import com.seeds.NergetBackend.domain.community.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "댓글", description = "게시글 댓글 관리 API")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다.")
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long postId,
            @Parameter(description = "현재 사용자 ID (임시)", example = "1")
            @RequestParam(required = false) Integer memberId,
            @RequestBody CommentRequest request) {
        
        // TODO: JWT에서 memberId 추출
        if (memberId == null) memberId = 1;
        
        CommentResponse response = commentService.createComment(postId, memberId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "댓글 목록 조회", description = "게시글의 모든 댓글을 조회합니다.")
    @GetMapping("/{postId}/comments")
    public ResponseEntity<CommentListResponse> getComments(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long postId) {
        
        CommentListResponse response = commentService.getComments(postId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "댓글 수정", description = "내가 작성한 댓글을 수정합니다.")
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @Parameter(description = "댓글 ID", example = "1")
            @PathVariable Long commentId,
            @Parameter(description = "현재 사용자 ID (임시)", example = "1")
            @RequestParam(required = false) Integer memberId,
            @RequestBody CommentRequest request) {
        
        // TODO: JWT에서 memberId 추출
        if (memberId == null) memberId = 1;
        
        CommentResponse response = commentService.updateComment(commentId, memberId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "댓글 삭제", description = "내가 작성한 댓글을 삭제합니다.")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "댓글 ID", example = "1")
            @PathVariable Long commentId,
            @Parameter(description = "현재 사용자 ID (임시)", example = "1")
            @RequestParam(required = false) Integer memberId) {
        
        // TODO: JWT에서 memberId 추출
        if (memberId == null) memberId = 1;
        
        commentService.deleteComment(commentId, memberId);
        return ResponseEntity.ok().build();
    }
}

