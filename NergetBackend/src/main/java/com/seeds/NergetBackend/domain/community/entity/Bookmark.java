package com.seeds.NergetBackend.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "bookmarks",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_bookmarks_post_member", columnNames = {"postId", "memberId"})
    },
    indexes = {
        @Index(name = "idx_bookmarks_member", columnList = "memberId"),
        @Index(name = "idx_bookmarks_post", columnList = "postId"),
        @Index(name = "idx_bookmarks_created_at", columnList = "createdAt")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {

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

