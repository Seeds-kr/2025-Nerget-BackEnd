package com.seeds.NergetBackend.repository;

import com.seeds.NergetBackend.entity.PostLike;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    List<PostLike> findByMemberEmailAndIdLessThanOrderByIdDesc(String email, Long cursor, Pageable pageable);
    List<PostLike> findByMemberEmailOrderByIdDesc(String email, Pageable pageable);

    boolean existsByPostIdAndMemberId(Long postId, Long memberId);
    Optional<PostLike> findByPostIdAndMemberId(Long postId, Long memberId);

    long countByPostId(Long postId);

    @Query("SELECT pl FROM PostLike pl WHERE pl.member.email = :email AND (:cursor IS NULL OR pl.id < :cursor) ORDER BY pl.id DESC")
    List<PostLike> findMyLikes(@Param("email") String email, @Param("cursor") Long cursor, Pageable pageable);
}
