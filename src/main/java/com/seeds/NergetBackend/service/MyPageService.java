// src/main/java/com/seeds/NergetBackend/service/MyPageService.java
package com.seeds.NergetBackend.service;

import com.seeds.NergetBackend.dto.*;
import com.seeds.NergetBackend.entity.Comment;
import com.seeds.NergetBackend.entity.Post;
import com.seeds.NergetBackend.entity.PostLike;
import com.seeds.NergetBackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;

    public PageResponse<PostSummaryDto> myPosts(String email, Long cursor, int limit) {
        var list = postRepository.findMyPosts(email, cursor, PageRequest.of(0, limit));
        Long next = list.size() == limit ? list.get(list.size()-1).getId() /* getter는 getId()로 나올 것 */ : null;

        var items = list.stream().map(p ->
                PostSummaryDto.builder()
                        .id(p.getId())                 // Lombok이 getId()를 만들어주므로 OK
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
        var list = commentRepository.findMyComments(email, cursor, PageRequest.of(0, limit));
        Long next = list.size() == limit ? list.get(list.size()-1).getId() : null;

        var items = list.stream().map(c ->
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
        var list = postLikeRepository.findMyLikes(email, cursor, PageRequest.of(0, limit));
        Long next = list.size() == limit ? list.get(list.size()-1).getId() : null;

        var items = list.stream().map(pl -> {
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
