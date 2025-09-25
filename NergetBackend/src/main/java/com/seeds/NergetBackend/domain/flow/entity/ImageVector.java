// src/main/java/com/seeds/NergetBackend/entity/ImageVector.java
package com.seeds.NergetBackend.domain.flow.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "image_vectors",
        indexes = {
                @Index(name = "idx_image_vectors_user", columnList = "userId"),
                @Index(name = "idx_image_vectors_status", columnList = "status"),
                @Index(name = "idx_image_vectors_created_at", columnList = "createdAt"),
                @Index(name = "idx_image_vectors_job", columnList = "jobId")
        }
)
public class ImageVector {

    @Id
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    /** 업로더/소유자 식별자(선택) */
    @Column(length = 64)
    private String userId;

    /** 연결된 Job ID (FK) */
    @Column(length = 36)
    private String jobId;

    /** 원본 이미지 식별 (S3 key 또는 URL) */
    @Column(nullable = false, length = 512)
    private String s3Key;

    /** AI가 계산한 4차원 벡터 */
    @Column(nullable = false)
    private float v1;

    @Column(nullable = false)
    private float v2;

    @Column(nullable = false)
    private float v3;

    @Column(nullable = false)
    private float v4;

    /** 부가 메타데이터(JSON) */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String metaJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum Status {
        PENDING,   // 업로드/큐잉됨
        RUNNING,   // AI 처리중
        DONE,      // 벡터 확정
        FAILED     // 처리 실패
    }

    /** 편의 메서드: float[4] ↔ 필드 변환 */
    public float[] toArray() {
        return new float[]{v1, v2, v3, v4};
    }

    public void fromArray(float[] v) {
        float a = (v != null && v.length > 0) ? v[0] : 0f;
        float b = (v != null && v.length > 1) ? v[1] : 0f;
        float c = (v != null && v.length > 2) ? v[2] : 0f;
        float d = (v != null && v.length > 3) ? v[3] : 0f;
        this.v1 = a; this.v2 = b; this.v3 = c; this.v4 = d;
    }

    /** -1~1 범위 강제(옵션) */
    public void clampMinusOneToOne() {
        this.v1 = clamp(v1); this.v2 = clamp(v2); this.v3 = clamp(v3); this.v4 = clamp(v4);
    }
    private float clamp(float x) { return Math.max(-1f, Math.min(1f, x)); }

    @PrePersist
    protected void onCreate() {
        if (this.id == null) this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.status == null) this.status = Status.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}