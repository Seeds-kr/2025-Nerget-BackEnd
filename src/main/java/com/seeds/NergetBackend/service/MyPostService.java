// src/main/java/com/seeds/NergetBackend/service/MyPostService.java
package com.seeds.NergetBackend.service;

import com.seeds.NergetBackend.dto.PostUpdateRequestDto;
import com.seeds.NergetBackend.entity.Post;
import com.seeds.NergetBackend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPostService {

    private final PostRepository postRepository;

    private Post loadOwnedPost(Long postId, String email) {
        return postRepository.findByIdAndAuthorEmail(postId, email)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없거나 권한이 없습니다."));
    }

    @Transactional
    public void updateMyPost(Long postId, String email, PostUpdateRequestDto req) {
        Post post = loadOwnedPost(postId, email);
        // 필요한 필드만 업데이트 (null/빈값 검증은 Controller/DTO @Valid로)
        if (req.getTitle() != null)   post.setTitle(req.getTitle());
        if (req.getContent() != null) post.setContent(req.getContent());
        if (req.getImageUrl() != null) post.setImageUrl(req.getImageUrl());
        // likeCount/commentCount 등은 비즈니스 로직에서 관리하므로 여기선 건드리지 않음
        // JPA Dirty Checking 으로 자동 반영
    }

    @Transactional
    public void deleteMyPost(Long postId, String email) {
        Post post = loadOwnedPost(postId, email);
        postRepository.delete(post);        // 하드 삭제
        // 소프트 삭제 원하면: post.setDeletedAt(now) 추가 + 조회쿼리에서 필터
    }
}
