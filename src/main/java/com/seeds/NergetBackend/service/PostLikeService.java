// src/main/java/com/seeds/NergetBackend/service/PostLikeService.java
package com.seeds.NergetBackend.service;

import com.seeds.NergetBackend.entity.Member;
import com.seeds.NergetBackend.entity.Post;
import com.seeds.NergetBackend.entity.PostLike;
import com.seeds.NergetBackend.repository.MemberRepository;
import com.seeds.NergetBackend.repository.PostLikeRepository;
import com.seeds.NergetBackend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository postLikeRepository;

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
    }

    @Transactional
    public LikeResult toggle(Long postId, String email) {
        Member member = getMemberByEmail(email);
        Post post = getPost(postId);

        boolean exists = postLikeRepository.existsByPostIdAndMemberMemberId(postId, member.getMemberId());
        boolean liked;

        if (exists) {
            // 취소
            PostLike like = (PostLike) postLikeRepository
                    .findByPostIdAndMemberMemberId(postId, member.getMemberId())
                    .orElseThrow(); // 존재한다고 확인했으므로 안전
            postLikeRepository.delete(like);
            liked = false;
        } else {
            // 추가
            PostLike like = new PostLike();
            like.setPost(post);
            like.setMember(member);
            postLikeRepository.save(like);
            liked = true;
        }

        // likeCount 컬럼을 쓰는 구조라면 동기화(정확성 우선)
        long count = postLikeRepository.countByPostId(postId);
        post.setLikeCount((int) count); // JPA dirty checking

        return new LikeResult(liked, (int) count);
    }

    public record LikeResult(boolean liked, int likeCount) {}
}
