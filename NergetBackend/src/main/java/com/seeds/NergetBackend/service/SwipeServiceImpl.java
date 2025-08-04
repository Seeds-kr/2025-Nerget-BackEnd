package com.seeds.NergetBackend.service;

import com.seeds.NergetBackend.dto.SwipeRequestDto;
import com.seeds.NergetBackend.util.AIClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SwipeServiceImpl implements SwipeService {

    private final AIClient aiClient;
    // 예: private final SwipeFeedbackRepository swipeRepository;

    @Override
    public void saveFeedback(SwipeRequestDto dto) {
        // TODO: 피드백 저장 (DB 처리 예정)
        System.out.println("피드백 저장됨: memberId=" + dto.getMemberId()
                + ", styleId=" + dto.getStyleId()
                + ", direction=" + dto.getDirection());

        // 스와이프 완료 후 AI 분석 요청 (mock)
        String newVector = aiClient.analyzeSwipeFeedback(dto.getMemberId());

        // TODO: user_style_vectors 업데이트
        System.out.println("업데이트된 벡터: " + newVector);
    }
}