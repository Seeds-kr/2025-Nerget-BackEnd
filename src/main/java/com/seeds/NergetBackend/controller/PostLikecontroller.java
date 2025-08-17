// src/main/java/com/seeds/NergetBackend/controller/PostLikeController.java
package com.seeds.NergetBackend.controller;

import com.seeds.NergetBackend.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping("/api/posts/{postId}/like") // 토글
    public ResponseEntity<?> toggleLike(@PathVariable Long postId,
                                        @AuthenticationPrincipal UserDetails user) {
        var r = postLikeService.toggle(postId, user.getUsername());
        return ResponseEntity.ok(new LikeResponse(r.liked(), r.likeCount()));
    }

    record LikeResponse(boolean liked, int likeCount) {}
}
