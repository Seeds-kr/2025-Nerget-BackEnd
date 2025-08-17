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
}
