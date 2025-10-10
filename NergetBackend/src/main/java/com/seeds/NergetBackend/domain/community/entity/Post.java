package com.seeds.NergetBackend.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "posts",
    indexes = {
        @Index(name = "idx_posts_member", columnList = "memberId"),
        @Index(name = "idx_posts_created_at", columnList = "createdAt"),
        @Index(name = "idx_posts_status", columnList = "status")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 작성자 (FK → Member.memberId) */
    @Column(nullable = false)
    private Integer memberId;

    /** 게시글 내용/설명 */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    /** 조회수 */
    @Column(nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    /** 좋아요 수 (비정규화 - 성능) */
    @Column(nullable = false)
    @Builder.Default
    private Integer likeCount = 0;

    /** 댓글 수 (비정규화 - 성능) */
    @Column(nullable = false)
    @Builder.Default
    private Integer commentCount = 0;

    /** 북마크 수 (비정규화 - 성능) */
    @Column(nullable = false)
    @Builder.Default
    private Integer bookmarkCount = 0;

    /** 게시글 상태 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    @Builder.Default
    private Status status = Status.ACTIVE;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum Status {
        ACTIVE,     // 활성
        DELETED,    // 삭제됨
        REPORTED    // 신고됨 (향후 확장)
    }

    /** 조회수 증가 */
    public void incrementViewCount() {
        this.viewCount++;
    }

    /** 좋아요 수 증가 */
    public void incrementLikeCount() {
        this.likeCount++;
    }

    /** 좋아요 수 감소 */
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    /** 댓글 수 증가 */
    public void incrementCommentCount() {
        this.commentCount++;
    }

    /** 댓글 수 감소 */
    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    /** 북마크 수 증가 */
    public void incrementBookmarkCount() {
        this.bookmarkCount++;
    }

    /** 북마크 수 감소 */
    public void decrementBookmarkCount() {
        if (this.bookmarkCount > 0) {
            this.bookmarkCount--;
        }
    }
}

