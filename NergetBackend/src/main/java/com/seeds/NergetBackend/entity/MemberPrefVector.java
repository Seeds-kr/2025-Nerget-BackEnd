// src/main/java/com/seeds/NergetBackend/entity/MemberPrefVector.java
package com.seeds.NergetBackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "member_pref_vectors",
        indexes = {
                @Index(name = "idx_member_pref_vectors_updated_at", columnList = "updatedAt")
        })
public class MemberPrefVector {

    /** Member.id와 1:1 (연관관계 미사용, 단순 FK 보관) */
    @Id
    @Column(nullable = false)
    private Long memberId;

    /** 누적/정규화된 4차원 벡터 */
    @Column(nullable = false) private float v1;
    @Column(nullable = false) private float v2;
    @Column(nullable = false) private float v3;
    @Column(nullable = false) private float v4;

    /** 반영된 상호작용(표본) 수 */
    @Column(nullable = false)
    private int sampleCount;

    /** 벡터 계산에 사용한 모델명(선택: 필요 없으면 비워도 됨) */
    @Column(length = 64)
    private String model; // 예: "nerget-vision-v1"

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /** 편의 메서드 */
    public float[] toArray() { return new float[]{v1, v2, v3, v4}; }

    public void fromArray(float[] v) {
        this.v1 = (v != null && v.length > 0) ? v[0] : 0f;
        this.v2 = (v != null && v.length > 1) ? v[1] : 0f;
        this.v3 = (v != null && v.length > 2) ? v[2] : 0f;
        this.v4 = (v != null && v.length > 3) ? v[3] : 0f;
    }

    /** v += scale * x (증분 갱신용) */
    public void addScaled(float[] x, float scale) {
        if (x == null || x.length < 4) return;
        this.v1 += x[0] * scale;
        this.v2 += x[1] * scale;
        this.v3 += x[2] * scale;
        this.v4 += x[3] * scale;
    }

    /** L2 정규화(선택) */
    public void l2Normalize() {
        double n = Math.sqrt(v1*v1 + v2*v2 + v3*v3 + v4*v4);
        if (n < 1e-12) return;
        v1 /= n; v2 /= n; v3 /= n; v4 /= n;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = (this.createdAt == null) ? now : this.createdAt;
        this.updatedAt = now;
        // 기본값 보정
        if (sampleCount < 0) sampleCount = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}