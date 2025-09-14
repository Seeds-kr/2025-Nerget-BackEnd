// src/main/java/com/seeds/NergetBackend/entity/ImageInteraction.java
package com.seeds.NergetBackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(
        name = "image_interactions",
        uniqueConstraints = { @UniqueConstraint(columnNames = {"memberId", "imageVectorId"}) },
        indexes = {
                @Index(name = "idx_image_interactions_member", columnList = "memberId"),
                @Index(name = "idx_image_interactions_image", columnList = "imageVectorId"),
                @Index(name = "idx_image_interactions_created_at", columnList = "createdAt")
        }
)
public class ImageInteraction {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Member.id */
    @Column(nullable = false)
    private Long memberId;

    /** ImageVector.id (UUID 36자) */
    @Column(nullable = false, length = 36)
    private String imageVectorId;

    /** +1=like, -1=dislike, 0=skip */
    @Column(nullable = false)
    private Integer action;

    /** 가중치(기본 1.0) */
    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (weight == null) weight = 1.0;
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}