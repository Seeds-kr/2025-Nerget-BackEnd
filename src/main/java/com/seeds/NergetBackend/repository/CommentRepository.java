package com.seeds.NergetBackend.repository;

import com.seeds.NergetBackend.entity.Comment;
import com.seeds.NergetBackend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostOrderByCreatedAtAsc(Post post); // 게시글 기준 댓글 목록

    // 내 댓글 첫 페이지
    List<Comment> findByAuthorEmailOrderByIdDesc(String email, org.springframework.data.domain.Pageable pageable);

    // 내 댓글 다음 페이지
    List<Comment> findByAuthorEmailAndIdLessThanOrderByIdDesc(String email, Long cursor, org.springframework.data.domain.Pageable pageable);

}
