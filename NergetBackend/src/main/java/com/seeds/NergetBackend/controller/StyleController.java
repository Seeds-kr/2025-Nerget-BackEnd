package com.seeds.NergetBackend.controller;

import com.seeds.NergetBackend.dto.StyleAnalysisResponseDto;
import com.seeds.NergetBackend.service.StyleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/style")
public class StyleController {

    private final StyleService styleService;

    // ✅ 사진 업로드 및 AI 분석 요청
    @PostMapping("/analyze")
    public ResponseEntity<StyleAnalysisResponseDto> analyzeStyle(
            @RequestParam("memberId") Integer memberId,
            @RequestParam("files") List<MultipartFile> files
    ) {
        // 내부적으로: 파일저장 -> AI 분석 요청 -> vector 저장
        StyleAnalysisResponseDto response = styleService.analyzeUploadedImages(memberId, files);
        return ResponseEntity.ok(response);
    }

    // ✅ 업로드 건너뛰기
    @PostMapping("/skip-upload")
    public ResponseEntity<String> skipUpload(@RequestParam("memberId") Integer memberId) {
        styleService.skipUploadAndMark(memberId);
        return ResponseEntity.ok("사진 업로드를 건너뛰었습니다.");
    }

    // ⚠️ MBTI 조회는 나중에 /feedback 컨트롤러로 이동 예정
}