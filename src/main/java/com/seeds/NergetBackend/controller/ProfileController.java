// src/main/java/com/seeds/NergetBackend/controller/ProfileController.java
package com.seeds.NergetBackend.controller;

import com.seeds.NergetBackend.dto.ProfileResponseDto;
import com.seeds.NergetBackend.dto.ProfileUpdateRequestDto;
import com.seeds.NergetBackend.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    @Operation(summary = "내 프로필 조회")
    public ResponseEntity<ProfileResponseDto> me(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(profileService.me(user.getUsername()));
    }

    @PutMapping("/me")
    @Operation(summary = "내 프로필 수정(닉네임/소개)")
    public ResponseEntity<ProfileResponseDto> update(@AuthenticationPrincipal UserDetails user,
                                                     @RequestBody @Valid ProfileUpdateRequestDto dto) {
        return ResponseEntity.ok(profileService.update(user.getUsername(), dto));
    }
}
