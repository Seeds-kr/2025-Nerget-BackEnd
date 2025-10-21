package com.seeds.NergetBackend.shared.ai;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Component
public class AIClient {

    // 업로드한 사진 분석 → 벡터값 반환 (Mock)
    public String analyzeUploadedPhotos(List<MultipartFile> files) {
        // 실제로는 AI 서버에 요청 보내야 함
        return "VECTOR-" + UUID.randomUUID().toString().substring(0, 8);
    }

    // 스와이프 피드백 분석 → 벡터값 재계산 (Mock)
    public String analyzeSwipeFeedback(Integer memberId) {
        // 실제로는 스와이프 데이터 기반 분석 요청해야 함
        return "VECTOR-UPDATED-" + UUID.randomUUID().toString().substring(0, 6);
    }
}