package com.seeds.NergetBackend.controller;

import com.seeds.NergetBackend.dto.PostRequestDto;
import com.seeds.NergetBackend.dto.PostResponseDto;
import com.seeds.NergetBackend.dto.PostUpdateRequestDto;
import com.seeds.NergetBackend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 업로드 (사진 1장 + 내용)
    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestPart("post") PostRequestDto postDto,
            @RequestPart("image") MultipartFile imageFile,
            @AuthenticationPrincipal UserDetails userDetails) {

        postService.createPost(postDto, imageFile, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // 게시글 목록 조회
    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAllPosts() {
        List<PostResponseDto> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // 게시글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long id) {
        PostResponseDto post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    //게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @RequestBody PostUpdateRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        postService.updatePost(id, dto, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        postService.deletePost(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

}
