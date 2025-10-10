package com.seeds.NergetBackend.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "comments",
    indexes = {
        @Index(name = "idx_comments_post", columnList = "postId"),
        @Index(name = "idx_comments_member", columnList = "memberId"),
        @Index(name = "idx_comments_created_at", columnList = "createdAt")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 게시글 ID (FK → Post.id) */
    @Column(nullable = false)
    private Long postId;

    /** 작성자 ID (FK → Member.memberId) */
    @Column(nullable = false)
    private Integer memberId;

    /** 댓글 내용 */
    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /** 삭제 여부 (소프트 삭제) */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /** 댓글 삭제 (소프트 삭제) */
    public void delete() {
        this.isDeleted = true;
        this.content = "삭제된 댓글입니다.";
    }
}

