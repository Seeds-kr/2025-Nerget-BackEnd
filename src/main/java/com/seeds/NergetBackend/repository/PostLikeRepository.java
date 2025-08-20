package com.seeds.NergetBackend.repository;

import com.seeds.NergetBackend.entity.PostLike;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 내가 좋아요한 글 목록
    List<PostLike> findByMemberEmailAndIdLessThanOrderByIdDesc(String email, Long cursor, Pageable pageable);
    List<PostLike> findByMemberEmailOrderByIdDesc(String email, Pageable pageable);

    // 좋아요 여부/카운트
    boolean existsByPostIdAndMemberMemberId(Long postId, Integer memberId);
    long countByPostId(Long postId);
}
