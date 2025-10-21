package com.seeds.NergetBackend.domain.mypage.controller;

import com.seeds.NergetBackend.domain.mypage.dto.MyCommentsResponse;
import com.seeds.NergetBackend.domain.mypage.dto.MyLikesResponse;
import com.seeds.NergetBackend.domain.mypage.dto.MyPostsResponse;
import com.seeds.NergetBackend.domain.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/posts")
    public ResponseEntity<MyPostsResponse> getMyPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer memberId) {
        
        // TODO: JWT에서 memberId 추출
        if (memberId == null) memberId = 1;
        
        Pageable pageable = PageRequest.of(page, size);
        MyPostsResponse response = myPageService.getMyPosts(memberId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/comments")
    public ResponseEntity<MyCommentsResponse> getMyComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer memberId) {
        
        // TODO: JWT에서 memberId 추출
        if (memberId == null) memberId = 1;
        
        Pageable pageable = PageRequest.of(page, size);
        MyCommentsResponse response = myPageService.getMyComments(memberId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/likes")
    public ResponseEntity<MyLikesResponse> getMyLikes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer memberId) {
        
        // TODO: JWT에서 memberId 추출
        if (memberId == null) memberId = 1;
        
        Pageable pageable = PageRequest.of(page, size);
        MyLikesResponse response = myPageService.getMyLikes(memberId, pageable);
        return ResponseEntity.ok(response);
    }
}

