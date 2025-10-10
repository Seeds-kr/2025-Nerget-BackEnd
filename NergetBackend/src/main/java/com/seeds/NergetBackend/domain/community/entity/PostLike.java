package com.seeds.NergetBackend.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "post_likes",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_post_likes_post_member", columnNames = {"postId", "memberId"})
    },
    indexes = {
        @Index(name = "idx_post_likes_post", columnList = "postId"),
        @Index(name = "idx_post_likes_member", columnList = "memberId")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 게시글 ID (FK → Post.id) */
    @Column(nullable = false)
    private Long postId;

    /** 사용자 ID (FK → Member.memberId) */
    @Column(nullable = false)
    private Integer memberId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

