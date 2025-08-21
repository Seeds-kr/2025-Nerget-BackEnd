package com.seeds.NergetBackend.service;

import com.seeds.NergetBackend.dto.PostRequestDto;
import com.seeds.NergetBackend.dto.PostResponseDto;
import com.seeds.NergetBackend.dto.PostUpdateRequestDto;
import com.seeds.NergetBackend.entity.Member;
import com.seeds.NergetBackend.entity.Post;
import com.seeds.NergetBackend.repository.MemberRepository;
import com.seeds.NergetBackend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    // 게시글 작성
    @Transactional
    public void createPost(PostRequestDto dto, MultipartFile imageFile, String userEmail) {
        Member author = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원이 존재하지 않습니다: " + userEmail));

        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setAuthor(author);

        // 이미지 업로드 후 URL 저장
        post.setImageUrl("이미지 URL 로직");

        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);

        postRepository.save(post);
    }

    // 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<PostResponseDto> getAllPosts() {
        List<Post> postList = postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        return postList.stream()
                .map(post -> new PostResponseDto(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getImageUrl(),
                        post.getAuthor().getNickname(),
                        post.getCreatedAt(),
                        post.getViewCount(),
                        post.getLikeCount(),
                        post.getCommentCount()
                ))
                .toList();
    }

    // 단건 조회 + 조회수 증가
    @Transactional
    public PostResponseDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다: " + id));

        post.setViewCount(post.getViewCount() + 1); // dirty checking으로 자동 반영

        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getImageUrl(),
                post.getAuthor().getNickname(),
                post.getCreatedAt(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount()
        );
    }

    // 게시글 수정 (작성자만)
    @Transactional
    public void updatePost(Long postId, PostUpdateRequestDto dto, String userEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다: " + postId));

        if (!post.getAuthor().getEmail().equals(userEmail)) {
            throw new SecurityException("게시글 작성자만 수정할 수 있습니다.");
        }

        if (dto.getTitle() != null)   post.setTitle(dto.getTitle());
        if (dto.getContent() != null) post.setContent(dto.getContent());
        if (dto.getImageUrl() != null) post.setImageUrl(dto.getImageUrl());
    }

    // 게시글 삭제 (작성자만)
    @Transactional
    public void deletePost(Long postId, String userEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다: " + postId));

        if (!post.getAuthor().getEmail().equals(userEmail)) {
            throw new SecurityException("게시글 작성자만 삭제할 수 있습니다.");
        }

        postRepository.delete(post);
    }
}
