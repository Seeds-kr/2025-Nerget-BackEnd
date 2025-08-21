package com.seeds.NergetBackend.service;

import com.seeds.NergetBackend.dto.*;
import com.seeds.NergetBackend.entity.Comment;
import com.seeds.NergetBackend.entity.Post;
import com.seeds.NergetBackend.entity.PostLike;
import com.seeds.NergetBackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;

    public PageResponse<PostSummaryDto> myPosts(String email, Long cursor, int limit) {
        List<Post> postList = postRepository.findMyPosts(email, cursor, PageRequest.of(0, limit));
        Long next = postList.size() == limit ? postList.get(postList.size() - 1).getId() : null;

        List<PostSummaryDto> items = postList.stream().map(p ->
                PostSummaryDto.builder()
                        .id(p.getId())
                        .title(p.getTitle())
                        .imageUrl(p.getImageUrl())
                        .createdAt(p.getCreatedAt())
                        .likeCount(p.getLikeCount())
                        .commentCount(p.getCommentCount())
                        .build()
        ).toList();

        return new PageResponse<>(items, next);
    }

    public PageResponse<CommentSummaryDto> myComments(String email, Long cursor, int limit) {
        List<Comment> commentList = commentRepository.findMyComments(email, cursor, PageRequest.of(0, limit));
        Long next = commentList.size() == limit ? commentList.get(commentList.size() - 1).getId() : null;

        List<CommentSummaryDto> items = commentList.stream().map(c ->
                CommentSummaryDto.builder()
                        .id(c.getId())
                        .postId(c.getPost().getId())
                        .postTitle(c.getPost().getTitle())
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .build()
        ).toList();

        return new PageResponse<>(items, next);
    }

    public PageResponse<LikedPostSummaryDto> myLikes(String email, Long cursor, int limit) {
        List<PostLike> likeList = postLikeRepository.findMyLikes(email, cursor, PageRequest.of(0, limit));
        Long next = likeList.size() == limit ? likeList.get(likeList.size() - 1).getId() : null;

        List<LikedPostSummaryDto> items = likeList.stream().map(pl -> {
            Post p = pl.getPost();
            return LikedPostSummaryDto.builder()
                    .likeRowId(pl.getId())
                    .postId(p.getId())
                    .title(p.getTitle())
                    .imageUrl(p.getImageUrl())
                    .postCreatedAt(p.getCreatedAt())
                    .likeCount(p.getLikeCount())
                    .commentCount(p.getCommentCount())
                    .build();
        }).toList();

        return new PageResponse<>(items, next);
    }
}