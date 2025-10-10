package com.seeds.NergetBackend.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "post_images",
    indexes = {
        @Index(name = "idx_post_images_post", columnList = "postId"),
        @Index(name = "idx_post_images_vector", columnList = "imageVectorId")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 게시글 ID (FK → Post.id) */
    @Column(nullable = false)
    private Long postId;

    /** 
     * ImageVector 연결 (AI 벡터 분석 연동)
     * 게시글 이미지도 추천 시스템에 활용
     */
    @Column(length = 36)
    private String imageVectorId;

    /** S3 키 */
    @Column(nullable = false, length = 512)
    private String s3Key;

    /** 이미지 공개 URL */
    @Column(nullable = false, length = 512)
    private String imageUrl;

    /** 이미지 순서 (현재는 1장만이지만 향후 확장 대비) */
    @Column(nullable = false)
    @Builder.Default
    private Integer displayOrder = 1;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

