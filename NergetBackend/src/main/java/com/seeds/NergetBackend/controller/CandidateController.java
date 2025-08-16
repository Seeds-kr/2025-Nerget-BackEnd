package com.seeds.NergetBackend.controller;

import com.seeds.NergetBackend.dto.CandidatesResponse;
import com.seeds.NergetBackend.dto.PendingDto;
import com.seeds.NergetBackend.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flows/{flowId}")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    /**
     * 초기 4장 분석(jobId)이 끝났을 때만 AI 추천/랜덤 후보 12장을 반환
     * 아직이면 202 Accepted 로 펜딩 응답
     */
    @GetMapping("/candidates")
    public ResponseEntity<?> getCandidates(@PathVariable String flowId,
                                           @RequestParam String jobId,
                                           @RequestParam(defaultValue = "12") int count) {
        var res = candidateService.getCandidatesOrNull(jobId, count);
        if (res == null) {
            return ResponseEntity.accepted()
                    .body(new PendingDto("INITIAL_PENDING", "초기 분석이 아직 완료되지 않았습니다."));
        }
        return ResponseEntity.ok(res);
    }
}