
package com.seeds.NergetBackend.controller;

import com.seeds.NergetBackend.dto.PostUpdateRequestDto;
import com.seeds.NergetBackend.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage/posts")
public class MyPagePostController {

    private final PostService PostService;

    @PatchMapping("/{postId}")
    public ResponseEntity<?> updateMyPost(@PathVariable Long postId,
                                          @Valid @RequestBody PostUpdateRequestDto req,
                                          @AuthenticationPrincipal UserDetails user) {
        PostService.updateMyPost(postId, user.getUsername(), req); // username = email 가정
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deleteMyPost(@PathVariable Long postId,
                                          @AuthenticationPrincipal UserDetails user) {
        PostService.deleteMyPost(postId, user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
