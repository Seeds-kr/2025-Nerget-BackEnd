package com.seeds.NergetBackend.domain.community.dto;

import com.seeds.NergetBackend.domain.community.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private Long postId;
    private Integer memberId;
    private String memberNickname;  // 작성자 닉네임 (조인 필요)
    private String content;
    private String imageUrl;
    private String imageVectorId;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer bookmarkCount;
    private Boolean isLiked;        // 현재 사용자가 좋아요 했는지
    private Boolean isBookmarked;   // 현재 사용자가 북마크 했는지
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** Entity → DTO 변환 (기본) */
    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .postId(post.getId())
                .memberId(post.getMemberId())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .bookmarkCount(post.getBookmarkCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    /** Entity → DTO 변환 (상세 정보 포함) */
    public static PostResponse from(Post post, String imageUrl, String imageVectorId, 
                                     String memberNickname, Boolean isLiked, Boolean isBookmarked) {
        return PostResponse.builder()
                .postId(post.getId())
                .memberId(post.getMemberId())
                .memberNickname(memberNickname)
                .content(post.getContent())
                .imageUrl(imageUrl)
                .imageVectorId(imageVectorId)
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .bookmarkCount(post.getBookmarkCount())
                .isLiked(isLiked)
                .isBookmarked(isBookmarked)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}

