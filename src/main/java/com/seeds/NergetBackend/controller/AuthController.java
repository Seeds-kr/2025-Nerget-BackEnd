package com.seeds.NergetBackend.controller;

import com.seeds.NergetBackend.dto.AuthResponseDto;
import com.seeds.NergetBackend.dto.GoogleLoginDto;
import com.seeds.NergetBackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/google")
    public ResponseEntity<AuthResponseDto> googleLogin(@RequestBody GoogleLoginDto loginDto) {
        AuthResponseDto response = authService.handleGoogleLogin(loginDto.getIdToken());
        return ResponseEntity.ok(response);
    }
}