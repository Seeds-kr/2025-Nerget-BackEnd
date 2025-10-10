package com.seeds.NergetBackend.domain.community.service;

import com.seeds.NergetBackend.domain.auth.entity.Member;
import com.seeds.NergetBackend.domain.auth.repository.MemberRepository;
import com.seeds.NergetBackend.domain.community.dto.CommentListResponse;
import com.seeds.NergetBackend.domain.community.dto.CommentRequest;
import com.seeds.NergetBackend.domain.community.dto.CommentResponse;
import com.seeds.NergetBackend.domain.community.entity.Comment;
import com.seeds.NergetBackend.domain.community.entity.Post;
import com.seeds.NergetBackend.domain.community.repository.CommentRepository;
import com.seeds.NergetBackend.domain.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    /** 댓글 작성 */
    @Transactional
    public CommentResponse createComment(Long postId, Integer memberId, CommentRequest request) {
        // 게시글 존재 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 댓글 생성
        Comment comment = Comment.builder()
                .postId(postId)
                .memberId(memberId)
                .content(request.getContent())
                .isDeleted(false)
                .build();
        comment = commentRepository.save(comment);

        // 게시글 댓글 수 증가
        post.incrementCommentCount();
        postRepository.save(post);

        // 작성자 닉네임 조회
        String memberNickname = memberRepository.findById(memberId)
                .map(Member::getNickname)
                .orElse("Unknown");

        return CommentResponse.from(comment, memberNickname);
    }

    /** 댓글 목록 조회 */
    @Transactional(readOnly = true)
    public CommentListResponse getComments(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);

        List<CommentResponse> commentResponses = comments.stream()
                .map(comment -> {
                    String memberNickname = memberRepository.findById(comment.getMemberId())
                            .map(Member::getNickname)
                            .orElse("Unknown");
                    return CommentResponse.from(comment, memberNickname);
                })
                .collect(Collectors.toList());

        return CommentListResponse.builder()
                .comments(commentResponses)
                .totalCount(commentResponses.size())
                .build();
    }

    /** 댓글 수정 */
    @Transactional
    public CommentResponse updateComment(Long commentId, Integer memberId, CommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 작성자 확인
        if (!comment.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        comment.setContent(request.getContent());
        comment = commentRepository.save(comment);

        String memberNickname = memberRepository.findById(memberId)
                .map(Member::getNickname)
                .orElse("Unknown");

        return CommentResponse.from(comment, memberNickname);
    }

    /** 댓글 삭제 */
    @Transactional
    public void deleteComment(Long commentId, Integer memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 작성자 확인
        if (!comment.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        // 소프트 삭제
        comment.delete();
        commentRepository.save(comment);

        // 게시글 댓글 수 감소
        Post post = postRepository.findById(comment.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        post.decrementCommentCount();
        postRepository.save(post);
    }
}

