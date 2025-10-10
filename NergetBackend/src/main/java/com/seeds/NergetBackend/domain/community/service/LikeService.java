package com.seeds.NergetBackend.domain.community.service;

import com.seeds.NergetBackend.domain.community.entity.Post;
import com.seeds.NergetBackend.domain.community.entity.PostLike;
import com.seeds.NergetBackend.domain.community.repository.PostLikeRepository;
import com.seeds.NergetBackend.domain.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    /** 좋아요 토글 (좋아요/취소) */
    @Transactional
    public boolean toggleLike(Long postId, Integer memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Optional<PostLike> existingLike = postLikeRepository.findByPostIdAndMemberId(postId, memberId);

        if (existingLike.isPresent()) {
            // 좋아요 취소
            postLikeRepository.delete(existingLike.get());
            post.decrementLikeCount();
            postRepository.save(post);
            return false;  // 좋아요 취소됨
        } else {
            // 좋아요 추가
            PostLike postLike = PostLike.builder()
                    .postId(postId)
                    .memberId(memberId)
                    .build();
            postLikeRepository.save(postLike);
            post.incrementLikeCount();
            postRepository.save(post);
            return true;  // 좋아요 추가됨
        }
    }

    /** 좋아요 상태 확인 */
    @Transactional(readOnly = true)
    public boolean isLiked(Long postId, Integer memberId) {
        return postLikeRepository.existsByPostIdAndMemberId(postId, memberId);
    }
}

