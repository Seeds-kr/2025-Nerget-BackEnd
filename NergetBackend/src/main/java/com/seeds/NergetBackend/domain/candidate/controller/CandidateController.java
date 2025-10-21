// src/main/java/com/seeds/NergetBackend/controller/CandidateController.java
package com.seeds.NergetBackend.domain.candidate.controller;

import com.seeds.NergetBackend.domain.candidate.dto.CandidatesResponse;
import com.seeds.NergetBackend.domain.candidate.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/candidates")
public class CandidateController {

    private final CandidateService candidateService;

    @GetMapping
    public ResponseEntity<?> getCandidates(
            @RequestParam String jobId,
            @RequestParam(defaultValue = "12") int count
    ) {
        CandidatesResponse resp = candidateService.getCandidatesOrNull(jobId, count);
        if (resp == null) {
            return ResponseEntity.status(409).body("Job is not DONE yet");
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping(value = "/{imageId}/download", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> downloadImage(
            @PathVariable String imageId
    ) {
        // TODO: S3에서 이미지 다운로드 로직 구현
        // 현재는 예시 응답
        return ResponseEntity.notFound().build();
    }
}