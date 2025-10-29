// src/main/java/com/seeds/NergetBackend/controller/CandidateController.java
package com.seeds.NergetBackend.domain.candidate.controller;

import com.seeds.NergetBackend.domain.candidate.dto.CandidatesResponse;
import com.seeds.NergetBackend.domain.candidate.service.CandidateService;
import com.seeds.NergetBackend.domain.flow.entity.ImageVector;
import com.seeds.NergetBackend.domain.flow.repository.ImageVectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/candidates")
public class CandidateController {

    private final CandidateService candidateService;
    private final ImageVectorRepository imageVectorRepository;
    
    // S3 퍼블릭 베이스 URL
    @Value("${app.s3.public-base-url:}")
    private String s3PublicBaseUrl;

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
        try {
            // 이미지 ID로 ImageVector 조회
            Optional<ImageVector> imageVectorOpt = imageVectorRepository.findById(imageId);
            if (imageVectorOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            ImageVector imageVector = imageVectorOpt.get();
            String s3Key = imageVector.getS3Key();
            
            // S3 URL 구성
            String imageUrl = toPublicUrl(s3Key);
            if (imageUrl == null) {
                return ResponseEntity.notFound().build();
            }
            
            // S3에서 이미지 다운로드
            URL url = new URL(imageUrl);
            byte[] imageBytes = url.openStream().readAllBytes();
            
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageBytes);
                    
        } catch (IOException e) {
            // 로그 남기고 404 반환
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // 기타 오류도 404 반환
            return ResponseEntity.notFound().build();
        }
    }
    
    /** 퍼블릭 베이스 URL + s3Key로 최종 URL 조립 */
    private String toPublicUrl(String s3Key) {
        if (s3Key == null) return null;
        
        // 이미 완전한 URL이면 그대로 반환
        if (s3Key.startsWith("http://") || s3Key.startsWith("https://")) {
            return s3Key;
        }
        
        String base = (s3PublicBaseUrl == null) ? "" : s3PublicBaseUrl.trim();
        if (base.isEmpty()) return s3Key;
        // base 끝/키 앞의 슬래시 중복 방지
        if (base.endsWith("/")) base = base.substring(0, base.length()-1);
        String key = s3Key.startsWith("/") ? s3Key.substring(1) : s3Key;
        return base + "/" + key;
    }
}