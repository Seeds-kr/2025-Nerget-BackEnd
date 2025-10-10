package com.seeds.NergetBackend.domain.mypage.dto;

import com.seeds.NergetBackend.domain.community.dto.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyCommentsResponse {

    private Integer memberId;
    private List<CommentResponse> comments;
    private Integer currentPage;
    private Integer totalPages;
    private Long totalElements;
    private Boolean hasNext;
}

