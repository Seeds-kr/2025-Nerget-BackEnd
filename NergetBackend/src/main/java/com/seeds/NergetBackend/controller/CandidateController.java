// src/main/java/com/seeds/NergetBackend/controller/CandidateController.java
package com.seeds.NergetBackend.controller;

import com.seeds.NergetBackend.dto.CandidatesResponse;
import com.seeds.NergetBackend.service.CandidateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/candidates")
@Tag(name = "후보 이미지", description = "AI 분석 후 사용자에게 제공할 후보 이미지 API")
public class CandidateController {

    private final CandidateService candidateService;

    @Operation(summary = "후보 이미지 목록 조회", description = "AI 처리 완료 후 사용자에게 보여줄 후보 이미지들을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "후보 이미지 조회 성공"),
            @ApiResponse(responseCode = "409", description = "AI 처리가 아직 완료되지 않음")
    })
    @GetMapping
    public ResponseEntity<?> getCandidates(
            @Parameter(description = "작업 ID", required = true, example = "job-12345")
            @RequestParam String jobId,
            @Parameter(description = "반환할 이미지 개수", example = "12")
            @RequestParam(defaultValue = "12") int count
    ) {
        CandidatesResponse resp = candidateService.getCandidatesOrNull(jobId, count);
        if (resp == null) {
            return ResponseEntity.status(409).body("Job is not DONE yet");
        }
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "이미지 파일 다운로드", description = "특정 이미지 ID의 실제 이미지 파일을 다운로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이미지 파일 다운로드 성공",
                    content = @Content(mediaType = "image/jpeg")),
            @ApiResponse(responseCode = "404", description = "이미지를 찾을 수 없음")
    })
    @GetMapping(value = "/{imageId}/download", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> downloadImage(
            @Parameter(description = "이미지 ID", required = true, example = "img-123")
            @PathVariable String imageId
    ) {
        // TODO: S3에서 이미지 다운로드 로직 구현
        // 현재는 예시 응답
        return ResponseEntity.notFound().build();
    }
}