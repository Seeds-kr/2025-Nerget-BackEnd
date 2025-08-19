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

    // AuthController.java 에 임시로 추가
    @GetMapping("/test-token")
    public ResponseEntity<AuthResponseDto> getTestToken() {
        // 테스트하고 싶은 사용자 ID를 지정 (DB에 실제 존재하는 사용자여야 합니다)
        Long testMemberId = 1L;

        // JwtTokenProvider를 사용하여 토큰 생성
        String testToken = jwtTokenProvider.createToken(testMemberId, "ROLE_USER"); // createToken은 직접 구현하신 메소드 이름 사용

        AuthResponseDto response = new AuthResponseDto(testToken, "테스트유저");
        return ResponseEntity.ok(response);
    }
}