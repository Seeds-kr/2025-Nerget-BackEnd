package com.seeds.NergetBackend.domain.auth.controller;

import com.seeds.NergetBackend.domain.auth.dto.AuthResponseDto;
import com.seeds.NergetBackend.domain.auth.entity.Member;
import com.seeds.NergetBackend.domain.auth.repository.MemberRepository;
import com.seeds.NergetBackend.shared.security.JwtTokenProvider;
import com.seeds.NergetBackend.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/google")
    public ResponseEntity<AuthResponseDto> googleLogin(
            @RequestBody Map<String, String> request) {
        String idToken = request.get("idToken"); // 프론트에서 보내는 키 이름이 idToken
        AuthResponseDto response = authService.googleLogin(idToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "Missing Authorization header"));
        }
        String token = authHeader.substring(7);
        String email = jwtTokenProvider.getEmail(token);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        return ResponseEntity.ok(Map.of(
                "email", member.getEmail(),
                "nickname", member.getNickname(),
                "isNewUser", false,
                "accessToken", token
        ));
    }

}