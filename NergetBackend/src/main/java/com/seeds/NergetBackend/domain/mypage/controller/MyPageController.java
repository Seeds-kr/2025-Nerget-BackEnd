package com.seeds.NergetBackend.domain.mypage.controller;

import com.seeds.NergetBackend.domain.mypage.dto.MyCommentsResponse;
import com.seeds.NergetBackend.domain.mypage.dto.MyLikesResponse;
import com.seeds.NergetBackend.domain.mypage.dto.MyPostsResponse;
import com.seeds.NergetBackend.domain.mypage.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@Tag(name = "마이페이지", description = "사용자 활동 조회 API")
public class MyPageController {

    private final MyPageService myPageService;

    @Operation(summary = "내 게시물 조회", description = "내가 작성한 게시물 목록을 조회합니다.")
    @GetMapping("/posts")
    public ResponseEntity<MyPostsResponse> getMyPosts(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "현재 사용자 ID (임시, 실제는 JWT에서 추출)", example = "1")
            @RequestParam(required = false) Integer memberId) {
        
        // TODO: JWT에서 memberId 추출
        if (memberId == null) memberId = 1;
        
        Pageable pageable = PageRequest.of(page, size);
        MyPostsResponse response = myPageService.getMyPosts(memberId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 댓글 조회", description = "내가 작성한 댓글 목록을 조회합니다.")
    @GetMapping("/comments")
    public ResponseEntity<MyCommentsResponse> getMyComments(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "현재 사용자 ID (임시, 실제는 JWT에서 추출)", example = "1")
            @RequestParam(required = false) Integer memberId) {
        
        // TODO: JWT에서 memberId 추출
        if (memberId == null) memberId = 1;
        
        Pageable pageable = PageRequest.of(page, size);
        MyCommentsResponse response = myPageService.getMyComments(memberId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내가 좋아요한 게시물", description = "내가 좋아요한 게시물 목록을 조회합니다.")
    @GetMapping("/likes")
    public ResponseEntity<MyLikesResponse> getMyLikes(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "현재 사용자 ID (임시, 실제는 JWT에서 추출)", example = "1")
            @RequestParam(required = false) Integer memberId) {
        
        // TODO: JWT에서 memberId 추출
        if (memberId == null) memberId = 1;
        
        Pageable pageable = PageRequest.of(page, size);
        MyLikesResponse response = myPageService.getMyLikes(memberId, pageable);
        return ResponseEntity.ok(response);
    }
}

