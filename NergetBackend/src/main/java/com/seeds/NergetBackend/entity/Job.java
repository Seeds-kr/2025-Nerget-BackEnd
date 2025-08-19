// src/main/java/com/seeds/NergetBackend/entity/Job.java
package com.seeds.NergetBackend.entity;

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
@Table(name = "jobs")
public class Job {

    @Id
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @Column(length = 64)
    private String userId;

    /** Job 상태 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;

    /** 진행률 0~100 */
    @Column(nullable = false)
    private int progress;

    /** 초기 분석에서 산출된 4차원 평균 벡터 */
    private float v1;
    private float v2;
    private float v3;
    private float v4;

    /** 에러 메시지 */
    @Column(length = 512)
    private String error;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum Status {
        PENDING, RUNNING, DONE, FAILED
    }

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