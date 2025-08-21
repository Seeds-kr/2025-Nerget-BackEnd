package com.seeds.NergetBackend.repository;

import com.seeds.NergetBackend.entity.PostLike;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query; // 추가
import org.springframework.data.repository.query.Param; // 추가
import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 내가 좋아요한 글 목록 (커서 기반: like row id 사용)
    List<PostLike> findByMemberEmailAndIdLessThanOrderByIdDesc(String email, Long cursor, Pageable pageable);
    List<PostLike> findByMemberEmailOrderByIdDesc(String email, Pageable pageable);

    // 좋아요 여부/카운트(선택)
    boolean existsByPostIdAndMemberMemberId(Long postId, Integer memberId);
    long countByPostId(Long postId);
    @Query("SELECT pl FROM PostLike pl WHERE pl.member.email = :email AND (:cursor IS NULL OR pl.id < :cursor) ORDER BY pl.id DESC")
    List<PostLike> findMyLikes(@Param("email") String email, @Param("cursor") Long cursor, Pageable pageable);
}
