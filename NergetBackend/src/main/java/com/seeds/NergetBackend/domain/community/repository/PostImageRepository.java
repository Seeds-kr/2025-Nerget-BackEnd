package com.seeds.NergetBackend.domain.community.repository;

import com.seeds.NergetBackend.domain.community.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    /** 특정 게시글의 이미지 조회 */
    List<PostImage> findByPostIdOrderByDisplayOrderAsc(Long postId);

    /** 특정 게시글의 첫 번째 이미지 조회 */
    Optional<PostImage> findFirstByPostIdOrderByDisplayOrderAsc(Long postId);

    /** 특정 게시글의 이미지 삭제 */
    void deleteByPostId(Long postId);

    /** ImageVector ID로 조회 (추천 시스템 연동) */
    Optional<PostImage> findByImageVectorId(String imageVectorId);
}

