package com.seeds.NergetBackend.domain.mypage.service;

import com.seeds.NergetBackend.domain.auth.entity.Member;
import com.seeds.NergetBackend.domain.auth.repository.MemberRepository;
import com.seeds.NergetBackend.domain.community.dto.CommentResponse;
import com.seeds.NergetBackend.domain.community.dto.PostResponse;
import com.seeds.NergetBackend.domain.community.entity.Comment;
import com.seeds.NergetBackend.domain.community.entity.Post;
import com.seeds.NergetBackend.domain.community.entity.PostImage;
import com.seeds.NergetBackend.domain.community.repository.*;
import com.seeds.NergetBackend.domain.mypage.dto.MyCommentsResponse;
import com.seeds.NergetBackend.domain.mypage.dto.MyLikesResponse;
import com.seeds.NergetBackend.domain.mypage.dto.MyPostsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;

    /** 내가 작성한 게시물 조회 */
    @Transactional(readOnly = true)
    public MyPostsResponse getMyPosts(Integer memberId, Pageable pageable) {
        Page<Post> page = postRepository.findByMemberIdAndStatusOrderByCreatedAtDesc(
                memberId, Post.Status.ACTIVE, pageable);

        List<PostResponse> posts = page.getContent().stream()
                .map(post -> convertToPostResponse(post, memberId))
                .collect(Collectors.toList());

        return MyPostsResponse.builder()
                .memberId(memberId)
                .posts(posts)
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .hasNext(page.hasNext())
                .build();
    }

    /** 내가 작성한 댓글 조회 */
    @Transactional(readOnly = true)
    public MyCommentsResponse getMyComments(Integer memberId, Pageable pageable) {
        Page<Comment> page = commentRepository.findByMemberIdAndIsDeletedOrderByCreatedAtDesc(
                memberId, false, pageable);

        String memberNickname = memberRepository.findById(memberId)
                .map(Member::getNickname)
                .orElse("Unknown");

        List<CommentResponse> comments = page.getContent().stream()
                .map(comment -> CommentResponse.from(comment, memberNickname))
                .collect(Collectors.toList());

        return MyCommentsResponse.builder()
                .memberId(memberId)
                .comments(comments)
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .hasNext(page.hasNext())
                .build();
    }

    /** 내가 좋아요한 게시물 조회 */
    @Transactional(readOnly = true)
    public MyLikesResponse getMyLikes(Integer memberId, Pageable pageable) {
        // 좋아요한 게시글 ID 목록
        Page<Long> postIdsPage = postLikeRepository.findPostIdsByMemberId(memberId, pageable);

        // 게시글 상세 정보 조회
        List<PostResponse> likedPosts = postIdsPage.getContent().stream()
                .map(postId -> postRepository.findById(postId).orElse(null))
                .filter(post -> post != null && post.getStatus() == Post.Status.ACTIVE)
                .map(post -> convertToPostResponse(post, memberId))
                .collect(Collectors.toList());

        return MyLikesResponse.builder()
                .memberId(memberId)
                .likedPosts(likedPosts)
                .currentPage(postIdsPage.getNumber())
                .totalPages(postIdsPage.getTotalPages())
                .totalElements(postIdsPage.getTotalElements())
                .hasNext(postIdsPage.hasNext())
                .build();
    }

    /** Post → PostResponse 변환 */
    private PostResponse convertToPostResponse(Post post, Integer currentMemberId) {
        // 이미지 정보
        PostImage postImage = postImageRepository.findFirstByPostIdOrderByDisplayOrderAsc(post.getId())
                .orElse(null);

        // 작성자 닉네임
        String memberNickname = memberRepository.findById(post.getMemberId())
                .map(Member::getNickname)
                .orElse("Unknown");

        // 좋아요/북마크 여부
        Boolean isLiked = postLikeRepository.existsByPostIdAndMemberId(post.getId(), currentMemberId);
        Boolean isBookmarked = bookmarkRepository.existsByPostIdAndMemberId(post.getId(), currentMemberId);

        return PostResponse.from(
                post,
                postImage != null ? postImage.getImageUrl() : null,
                postImage != null ? postImage.getImageVectorId() : null,
                memberNickname,
                isLiked,
                isBookmarked
        );
    }
}

