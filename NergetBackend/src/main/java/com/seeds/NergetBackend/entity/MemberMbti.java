// src/main/java/com/seeds/NergetBackend/entity/MemberMbti.java
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
@Table(name = "member_mbti")
public class MemberMbti {

    /** Member.id와 1:1 (FK, 연관관계 없이 Long으로 관리) */
    @Id
    @Column(nullable = false)
    private Long memberId;

    /** MBTI 결과 (예: "INTJ") */
    @Column(length = 4, nullable = false)
    private String mbti;

    /** 산출 근거 (self_reported, inferred, mixed 등) */
    @Column(length = 32)
    private String source;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}