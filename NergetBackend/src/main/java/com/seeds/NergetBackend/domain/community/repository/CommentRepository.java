package com.seeds.NergetBackend.domain.community.repository;

import com.seeds.NergetBackend.domain.community.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /** 특정 게시글의 댓글 조회 (최신순) */
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    /** 특정 게시글의 댓글 조회 (페이징) */
    Page<Comment> findByPostIdOrderByCreatedAtAsc(Long postId, Pageable pageable);

    /** 특정 회원의 댓글 조회 (페이징) */
    Page<Comment> findByMemberIdAndIsDeletedOrderByCreatedAtDesc(Integer memberId, Boolean isDeleted, Pageable pageable);

    /** 특정 게시글의 활성 댓글 개수 */
    long countByPostIdAndIsDeleted(Long postId, Boolean isDeleted);

    /** 특정 게시글의 댓글 삭제 */
    void deleteByPostId(Long postId);
}

