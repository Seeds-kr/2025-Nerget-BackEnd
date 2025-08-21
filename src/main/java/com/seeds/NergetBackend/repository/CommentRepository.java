package com.seeds.NergetBackend.repository;

import com.seeds.NergetBackend.entity.Comment;
import com.seeds.NergetBackend.entity.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable; // 추가
import org.springframework.data.jpa.repository.Query; // 추가
import org.springframework.data.repository.query.Param; // 추가
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostOrderByCreatedAtAsc(Post post); // 게시글 기준 댓글 목록


    List<Comment> findByAuthorEmailOrderByIdDesc(String email, org.springframework.data.domain.Pageable pageable);


    List<Comment> findByAuthorEmailAndIdLessThanOrderByIdDesc(String email, Long cursor, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.author.email = :email AND (:cursor IS NULL OR c.id < :cursor) ORDER BY c.id DESC")
    List<Comment> findMyComments(@Param("email") String email, @Param("cursor") Long cursor, Pageable pageable);
}
