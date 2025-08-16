package com.seeds.NergetBackend.controller;

import com.seeds.NergetBackend.dto.ChoiceRequest;
import com.seeds.NergetBackend.dto.MbtiResultDto;
import com.seeds.NergetBackend.dto.PendingDto;
import com.seeds.NergetBackend.service.ChoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flows/{flowId}")
@RequiredArgsConstructor
public class ChoiceController {

    private final ChoiceService choiceService;

    @PostMapping("/choices")
    public ResponseEntity<?> submitChoices(@PathVariable String flowId,
                                           @RequestBody ChoiceRequest req) {
        MbtiResultDto result = choiceService.processChoicesOrNull(req.getJobId(), req.getImageIds());

        if (result == null) {
            // 초기 4장 분석이 아직 끝나지 않은 경우: 퍼센트 없이 202로만 알림
            return ResponseEntity.accepted()
                    .body(new PendingDto("INITIAL_PENDING", "초기 분석이 아직 완료되지 않았습니다."));
        }
        // 완료된 경우: 최종 결과 반환
        return ResponseEntity.ok(result);
    }
}