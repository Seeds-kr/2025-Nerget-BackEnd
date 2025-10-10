package com.seeds.NergetBackend.domain.community.repository;

import com.seeds.NergetBackend.domain.community.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /** 활성 게시글만 조회 (삭제/신고 제외) */
    Page<Post> findByStatusOrderByCreatedAtDesc(Post.Status status, Pageable pageable);

    /** 특정 회원의 활성 게시글 조회 */
    Page<Post> findByMemberIdAndStatusOrderByCreatedAtDesc(Integer memberId, Post.Status status, Pageable pageable);

    /** 게시글 상세 조회 (활성 상태만) */
    Optional<Post> findByIdAndStatus(Long id, Post.Status status);

    /** 조회수 증가 (벌크 업데이트) */
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") Long postId);

    /** 특정 회원의 게시글 개수 */
    long countByMemberIdAndStatus(Integer memberId, Post.Status status);

    /** 인기 게시글 조회 (좋아요 순) */
    @Query("SELECT p FROM Post p WHERE p.status = :status ORDER BY p.likeCount DESC, p.createdAt DESC")
    Page<Post> findPopularPosts(@Param("status") Post.Status status, Pageable pageable);
}

