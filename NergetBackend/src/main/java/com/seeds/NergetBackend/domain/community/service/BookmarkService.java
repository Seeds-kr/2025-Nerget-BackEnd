package com.seeds.NergetBackend.domain.community.service;

import com.seeds.NergetBackend.domain.community.entity.Bookmark;
import com.seeds.NergetBackend.domain.community.entity.Post;
import com.seeds.NergetBackend.domain.community.repository.BookmarkRepository;
import com.seeds.NergetBackend.domain.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;

    /** 북마크 토글 (저장/취소) */
    @Transactional
    public boolean toggleBookmark(Long postId, Integer memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Optional<Bookmark> existingBookmark = bookmarkRepository.findByPostIdAndMemberId(postId, memberId);

        if (existingBookmark.isPresent()) {
            // 북마크 취소
            bookmarkRepository.delete(existingBookmark.get());
            post.decrementBookmarkCount();
            postRepository.save(post);
            return false;  // 북마크 취소됨
        } else {
            // 북마크 추가
            Bookmark bookmark = Bookmark.builder()
                    .postId(postId)
                    .memberId(memberId)
                    .build();
            bookmarkRepository.save(bookmark);
            post.incrementBookmarkCount();
            postRepository.save(post);
            return true;  // 북마크 추가됨
        }
    }

    /** 북마크 상태 확인 */
    @Transactional(readOnly = true)
    public boolean isBookmarked(Long postId, Integer memberId) {
        return bookmarkRepository.existsByPostIdAndMemberId(postId, memberId);
    }
}

