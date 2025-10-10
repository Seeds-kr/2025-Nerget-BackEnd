package com.seeds.NergetBackend.domain.community.repository;

import com.seeds.NergetBackend.domain.community.entity.PostLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    /** 특정 게시글에 특정 회원이 좋아요 했는지 확인 */
    Optional<PostLike> findByPostIdAndMemberId(Long postId, Integer memberId);

    /** 좋아요 존재 여부 */
    boolean existsByPostIdAndMemberId(Long postId, Integer memberId);

    /** 특정 게시글의 좋아요 삭제 */
    void deleteByPostIdAndMemberId(Long postId, Integer memberId);

    /** 특정 회원이 좋아요한 게시글 조회 (Post 조인) */
    @Query("SELECT pl.postId FROM PostLike pl WHERE pl.memberId = :memberId ORDER BY pl.createdAt DESC")
    Page<Long> findPostIdsByMemberId(@Param("memberId") Integer memberId, Pageable pageable);

    /** 특정 게시글의 모든 좋아요 삭제 */
    void deleteByPostId(Long postId);
}

