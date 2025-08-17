package com.seeds.NergetBackend.repository;

import com.seeds.NergetBackend.entity.Post;
import com.seeds.NergetBackend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 🔹 공통: 내가 쓴 글 조회 (Member 엔티티 기반)
    List<Post> findByAuthor(Member author);

    // 🔹 공통: 제목 검색
    List<Post> findByTitleContaining(String keyword);

    // 🔹 마이페이지: 내 글 목록 (페이징, 최신순)
    List<Post> findByAuthorEmailOrderByIdDesc(String email, Pageable pageable);

    // 🔹 마이페이지: 커서 기반 페이징 (이전 글 불러오기)
    List<Post> findByAuthorEmailAndIdLessThanOrderByIdDesc(String email, Long cursor, Pageable pageable);

    // 🔹 마이페이지: 수정/삭제 시 소유자 검증
    Optional<Post> findByIdAndAuthorEmail(Long postId, String email);
}
