// src/main/java/com/seeds/NergetBackend/controller/ChoiceController.java
package com.seeds.NergetBackend.controller;

import com.seeds.NergetBackend.dto.MbtiResultDto;
import com.seeds.NergetBackend.service.ChoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/choices")
public class ChoiceController {

    private final ChoiceService choiceService;

    @PostMapping
    public ResponseEntity<?> submit(@RequestBody ChoiceRequest req) {
        // like / dislike ID 분리
        List<String> likeIds = new ArrayList<>();
        List<String> dislikeIds = new ArrayList<>();
        if (req.selected != null) {
            for (ChoiceItem it : req.selected) {
                if (it == null || it.id == null) continue;
                if (it.like) likeIds.add(it.id);
                else dislikeIds.add(it.id);
            }
        }

        MbtiResultDto result = choiceService.processChoicesFromIdsOrNull(
                req.jobId, likeIds, dislikeIds
        );
        if (result == null) {
            return ResponseEntity.status(409).body("Job is not DONE yet");
        }
        return ResponseEntity.ok(result);
    }

    // --- 요청 DTO (간단 내장형; 별도 파일로 분리해도 됩니다) ---

    public static class ChoiceRequest {
        public String jobId;
        public List<ChoiceItem> selected;
    }

    public static class ChoiceItem {
        public String id;
        public boolean like;
    }
}