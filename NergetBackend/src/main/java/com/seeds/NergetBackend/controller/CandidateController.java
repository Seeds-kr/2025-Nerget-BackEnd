// src/main/java/com/seeds/NergetBackend/controller/CandidateController.java
package com.seeds.NergetBackend.controller;

import com.seeds.NergetBackend.dto.CandidatesResponse;
import com.seeds.NergetBackend.service.CandidateService;
import lombok.RequiredArgsConstructor;
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
}