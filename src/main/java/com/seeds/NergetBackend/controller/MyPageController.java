// src/main/java/com/seeds/NergetBackend/controller/MyPageController.java
package com.seeds.NergetBackend.controller;

import com.seeds.NergetBackend.dto.*;
import com.seeds.NergetBackend.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/posts")
    public ResponseEntity<PageResponseDto<PostSummaryDto>> myPosts(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") int limit,
            @AuthenticationPrincipal UserDetails user) {

        return ResponseEntity.ok(
                myPageService.myPosts(user.getUsername(), cursor, limit)
        );
    }

    @GetMapping("/comments")
    public ResponseEntity<PageResponseDto<CommentSummaryDto>> myComments(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") int limit,
            @AuthenticationPrincipal UserDetails user) {

        return ResponseEntity.ok(
                myPageService.myComments(user.getUsername(), cursor, limit)
        );
    }

    @GetMapping("/likes")
    public ResponseEntity<PageResponseDto<LikedPostSummaryDto>> myLikes(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") int limit,
            @AuthenticationPrincipal UserDetails user) {

        return ResponseEntity.ok(
                myPageService.myLikes(user.getUsername(), cursor, limit)
        );
    }
}
