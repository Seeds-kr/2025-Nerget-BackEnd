package com.seeds.NergetBackend.service;

import com.seeds.NergetBackend.dto.StyleAnalysisResponseDto;
import com.seeds.NergetBackend.util.AIClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StyleServiceImpl implements StyleService {

    private final AIClient aiClient;
    // 예: private final UserRepository userRepository;
    // 예: private final UserStyleVectorRepository vectorRepository;

    @Override
    public StyleAnalysisResponseDto analyzeUploadedImages(Integer memberId, List<MultipartFile> files) {
        // TODO: 사진 저장, AI 분석 요청
        String vector = aiClient.analyzeUploadedPhotos(files);

        // TODO: 벡터 저장 로직
        return new StyleAnalysisResponseDto(vector, "분석 완료되었습니다.");
    }

    @Override
    public void skipUploadAndMark(Integer memberId) {
        // TODO: DB에 업로드 건너뜀 기록 (ex. user.isSkipped = true)
    }

    @Override
    public String getMbtiForUser(Integer memberId) {
        // TODO: user_style_vectors 테이블에서 mbtiType 가져오기
        return "CUTE-CASUAL"; // mock
    }
}