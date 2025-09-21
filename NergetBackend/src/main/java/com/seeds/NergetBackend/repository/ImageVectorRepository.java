// src/main/java/com/seeds/NergetBackend/repository/ImageVectorRepository.java
package com.seeds.NergetBackend.repository;

import com.seeds.NergetBackend.entity.ImageVector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /** 특정 Job에 연결된 ImageVector 조회 */
    List<ImageVector> findByJobId(String jobId);

    @Query(value = """
        SELECT *
        FROM image_vectors
        WHERE status = 'DONE'
        ORDER BY (v1 * :p1 + v2 * :p2 + v3 * :p3 + v4 * :p4) DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<ImageVector> findTopByPrefVector(
            @Param("p1") float p1, @Param("p2") float p2,
            @Param("p3") float p3, @Param("p4") float p4,
            @Param("limit") int limit
    );
}