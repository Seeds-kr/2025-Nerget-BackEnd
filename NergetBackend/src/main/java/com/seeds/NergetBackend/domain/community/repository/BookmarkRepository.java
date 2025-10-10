package com.seeds.NergetBackend.domain.community.repository;

import com.seeds.NergetBackend.domain.community.entity.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    /** 특정 게시글에 특정 회원이 북마크 했는지 확인 */
    Optional<Bookmark> findByPostIdAndMemberId(Long postId, Integer memberId);

    /** 북마크 존재 여부 */
    boolean existsByPostIdAndMemberId(Long postId, Integer memberId);

    /** 북마크 삭제 */
    void deleteByPostIdAndMemberId(Long postId, Integer memberId);

    /** 특정 회원이 북마크한 게시글 ID 조회 (페이징) */
    @Query("SELECT b.postId FROM Bookmark b WHERE b.memberId = :memberId ORDER BY b.createdAt DESC")
    Page<Long> findPostIdsByMemberId(@Param("memberId") Integer memberId, Pageable pageable);

    /** 특정 게시글의 모든 북마크 삭제 */
    void deleteByPostId(Long postId);

    /** 특정 회원의 북마크 개수 */
    long countByMemberId(Integer memberId);
}

