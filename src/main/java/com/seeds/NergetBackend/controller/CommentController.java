package com.seeds.NergetBackend.controller;

import com.seeds.NergetBackend.dto.CommentRequestDto;
import com.seeds.NergetBackend.dto.CommentResponseDto;
import com.seeds.NergetBackend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // ✅ 댓글 생성
    @PostMapping("/{postId}")
    public ResponseEntity<Void> createComment(
            @PathVariable Long postId,
            @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        commentService.createComment(postId, requestDto, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // ✅ 게시글의 댓글 전체 조회
    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentResponseDto>> getComments(
            @PathVariable Long postId) {

        List<CommentResponseDto> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    // ✅ 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        commentService.updateComment(commentId, requestDto, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // ✅ 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        commentService.deleteComment(commentId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
