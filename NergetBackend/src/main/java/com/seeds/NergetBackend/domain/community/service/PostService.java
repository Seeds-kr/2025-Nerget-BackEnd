package com.seeds.NergetBackend.domain.community.service;

import com.seeds.NergetBackend.domain.auth.entity.Member;
import com.seeds.NergetBackend.domain.auth.repository.MemberRepository;
import com.seeds.NergetBackend.domain.community.dto.*;
import com.seeds.NergetBackend.domain.community.entity.Post;
import com.seeds.NergetBackend.domain.community.entity.PostImage;
import com.seeds.NergetBackend.domain.community.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final PostLikeRepository postLikeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;

    /** 게시글 작성 */
    @Transactional
    public PostResponse createPost(Integer memberId, PostCreateRequest request) {
        // 게시글 생성
        Post post = Post.builder()
                .memberId(memberId)
                .content(request.getContent())
                .status(Post.Status.ACTIVE)
                .build();
        post = postRepository.save(post);

        // 이미지 등록
        PostImage postImage = PostImage.builder()
                .postId(post.getId())
                .s3Key(request.getS3Key())
                .imageUrl(request.getImageUrl())
                .imageVectorId(request.getImageVectorId())
                .displayOrder(1)
                .build();
        postImageRepository.save(postImage);

        // 회원 정보 조회
        String memberNickname = memberRepository.findById(memberId)
                .map(Member::getNickname)
                .orElse("Unknown");

        return PostResponse.from(post, request.getImageUrl(), request.getImageVectorId(), 
                                 memberNickname, false, false);
    }

    /** 게시글 목록 조회 (전체) */
    @Transactional(readOnly = true)
    public PostListResponse getPosts(Pageable pageable, Integer currentMemberId) {
        Page<Post> page = postRepository.findByStatusOrderByCreatedAtDesc(Post.Status.ACTIVE, pageable);
        
        List<PostResponse> posts = page.getContent().stream()
                .map(post -> convertToResponse(post, currentMemberId))
                .collect(Collectors.toList());

        return PostListResponse.builder()
                .posts(posts)
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .hasNext(page.hasNext())
                .build();
    }

    /** 게시글 상세 조회 */
    @Transactional
    public PostResponse getPost(Long postId, Integer currentMemberId) {
        Post post = postRepository.findByIdAndStatus(postId, Post.Status.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 조회수 증가
        postRepository.incrementViewCount(postId);

        return convertToResponse(post, currentMemberId);
    }

    /** 게시글 수정 */
    @Transactional
    public PostResponse updatePost(Long postId, Integer memberId, PostUpdateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 작성자 확인
        if (!post.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        post.setContent(request.getContent());
        post = postRepository.save(post);

        return convertToResponse(post, memberId);
    }

    /** 게시글 삭제 */
    @Transactional
    public void deletePost(Long postId, Integer memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 작성자 확인
        if (!post.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        // 소프트 삭제
        post.setStatus(Post.Status.DELETED);
        postRepository.save(post);
    }

    /** Entity → DTO 변환 (상세 정보 포함) */
    private PostResponse convertToResponse(Post post, Integer currentMemberId) {
        // 이미지 정보
        PostImage postImage = postImageRepository.findFirstByPostIdOrderByDisplayOrderAsc(post.getId())
                .orElse(null);

        // 작성자 닉네임
        String memberNickname = memberRepository.findById(post.getMemberId())
                .map(Member::getNickname)
                .orElse("Unknown");

        // 좋아요/북마크 여부
        Boolean isLiked = currentMemberId != null && 
                         postLikeRepository.existsByPostIdAndMemberId(post.getId(), currentMemberId);
        Boolean isBookmarked = currentMemberId != null && 
                              bookmarkRepository.existsByPostIdAndMemberId(post.getId(), currentMemberId);

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

