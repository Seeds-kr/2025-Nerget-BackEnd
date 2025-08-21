package com.seeds.NergetBackend.repository;

import com.seeds.NergetBackend.entity.Post;
import com.seeds.NergetBackend.entity.Member;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.Query; // 추가
import org.springframework.data.repository.query.Param; // 추가
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 내가 쓴 글 조회 (Member 엔티티 기반)
    List<Post> findByAuthor(Member author);

    // 제목 검색
    List<Post> findByTitleContaining(String keyword);

    // 내 글 목록
    List<Post> findByAuthorEmailOrderByIdDesc(String email, Pageable pageable);

    // 커서 기반 페이징 (이전 글 불러오기)
    List<Post> findByAuthorEmailAndIdLessThanOrderByIdDesc(String email, Long cursor, Pageable pageable);

    //  수정/삭제 시 소유자 검증
    Optional<Post> findByIdAndAuthorEmail(Long postId, String email);

    @Query("SELECT p FROM Post p WHERE p.author.email = :email AND (:cursor IS NULL OR p.id < :cursor) ORDER BY p.id DESC")
    List<Post> findMyPosts(@Param("email") String email, @Param("cursor") Long cursor, Pageable pageable);
}
