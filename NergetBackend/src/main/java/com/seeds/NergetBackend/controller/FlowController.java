package com.seeds.NergetBackend.controller;

import com.seeds.NergetBackend.dto.FlowStartResponse;
import com.seeds.NergetBackend.dto.JobStartResponse;
import com.seeds.NergetBackend.service.FlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/flows")
@RequiredArgsConstructor
public class FlowController {

    private final FlowService flowService;

    // 1) 플로우 시작: 로그인 직후 호출 → flowId 발급
    @PostMapping
    public ResponseEntity<FlowStartResponse> startFlow() {
        String flowId = flowService.startFlow();
        return ResponseEntity.ok(new FlowStartResponse(flowId));
    }

    // 2) 초기 이미지 업로드: 1~4장 업로드 → 분석 잡 시작, jobId 반환
    @PostMapping(
            value = "/{flowId}/initial-images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<JobStartResponse> uploadInitialImages(
            @PathVariable String flowId,
            @RequestPart("files") List<MultipartFile> files
    ) {
        String jobId = flowService.handleInitialUpload(flowId, files);
        return ResponseEntity.ok(new JobStartResponse(jobId));
    }
}