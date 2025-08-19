// src/main/java/com/seeds/NergetBackend/repository/ImageVectorRepository.java
package com.seeds.NergetBackend.repository;

import com.seeds.NergetBackend.entity.ImageVector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageVectorRepository extends JpaRepository<ImageVector, String> {

    /** 특정 사용자 업로드 이미지 */
    List<ImageVector> findByUserId(String userId);

    /** 상태별 조회 */
    List<ImageVector> findByStatus(ImageVector.Status status);

    /** 여러 ID 한꺼번에 조회 */
    List<ImageVector> findByIdIn(List<String> ids);
}