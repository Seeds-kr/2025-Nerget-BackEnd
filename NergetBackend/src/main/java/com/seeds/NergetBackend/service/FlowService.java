package com.seeds.NergetBackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlowService {

    private final StorageService storageService;
    private final JobService jobService;
    private final EmbeddingService embeddingService;

    /** 플로우 식별자 발급 (로그인 직후) */
    public String startFlow() {
        return UUID.randomUUID().toString();
    }

    /**
     * 4장 업로드 → 스토리지 저장 → 비동기 분석 잡 시작
     * 즉시 jobId 반환 (프론트는 퍼센트 표시에 사용)
     */
    public String handleInitialUpload(String flowId, List<MultipartFile> files) {
        if (files == null || files.isEmpty() || files.size() > 4) {
            throw new IllegalArgumentException("파일은 1~4장이어야 합니다.");
        }
        // 1) 파일 저장 (현재는 모킹 URI)
        List<String> uris = storageService.saveInitial(flowId, files);

        // 2) 잡 생성 및 비동기 진행 시작
        return jobService.startInitialAnalysis(flowId, uris, embeddingService);
    }
}