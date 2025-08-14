package com.seeds.NergetBackend.service;

import com.seeds.NergetBackend.dto.CommentRequestDto;
import com.seeds.NergetBackend.dto.CommentResponseDto;
import com.seeds.NergetBackend.entity.Comment;
import com.seeds.NergetBackend.entity.Member;
import com.seeds.NergetBackend.entity.Post;
import com.seeds.NergetBackend.repository.CommentRepository;
import com.seeds.NergetBackend.repository.MemberRepository;
import com.seeds.NergetBackend.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
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

    @Transactional
    public void createComment(Long postId, CommentRequestDto dto, String userEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        Member author = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setPost(post);
        comment.setAuthor(author);

        commentRepository.save(comment);

        post.setCommentCount(post.getCommentCount() + 1);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        return commentRepository.findAllByPostOrderByCreatedAtAsc(post)
                .stream()
                .map(comment -> new CommentResponseDto(
                        comment.getId(),
                        comment.getContent(),
                        comment.getAuthor().getNickname(),
                        comment.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateComment(Long commentId, CommentRequestDto dto, String userEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        if (!comment.getAuthor().getEmail().equals(userEmail)) {
            throw new IllegalArgumentException("본인의 댓글만 수정할 수 있습니다.");
        }

        comment.setContent(dto.getContent());
    }

    @Transactional
    public void deleteComment(Long commentId, String userEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

        if (!comment.getAuthor().getEmail().equals(userEmail)) {
            throw new IllegalArgumentException("본인의 댓글만 삭제할 수 있습니다.");
        }

        // 댓글 수 감소
        Post post = comment.getPost();
        post.setCommentCount(Math.max(0, post.getCommentCount() - 1));

        commentRepository.delete(comment);
    }
}
