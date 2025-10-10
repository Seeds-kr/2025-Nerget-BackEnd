package com.seeds.NergetBackend.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {

    /** 게시글 내용/설명 */
    private String content;

    /** 이미지 S3 키 (프론트에서 S3 업로드 후 전달) */
    private String s3Key;

    /** 이미지 URL (공개 URL) */
    private String imageUrl;

    /** ImageVector ID (AI 분석 완료된 경우, 선택) */
    private String imageVectorId;
}

