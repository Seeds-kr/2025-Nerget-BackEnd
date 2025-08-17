package com.seeds.NergetBackend.repository;

import com.seeds.NergetBackend.entity.Post;
import com.seeds.NergetBackend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 내가 쓴 글 조회용 (마이페이지)
    List<Post> findByAuthor(Member author);

    // 제목 포함 검색 등 커스텀 쿼리도 가능
    List<Post> findByTitleContaining(String keyword);

    // 내 글 첫 페이지 (커서 없음)
    List<Post> findByAuthorEmailOrderByIdDesc(String email, org.springframework.data.domain.Pageable pageable);

    // 내 글 다음 페이지 (커서 있음: Post의 PK가 'Id'임에 주의)
    List<Post> findByAuthorEmailAndIdLessThanOrderByIdDesc(String email, Long cursor, org.springframework.data.domain.Pageable pageable);

    // 내 글 수정/삭제 소유자 검증
    java.util.Optional<Post> findByIdAndAuthorEmail(Long postId, String email);

}
