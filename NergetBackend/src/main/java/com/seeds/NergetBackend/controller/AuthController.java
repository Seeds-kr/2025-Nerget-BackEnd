package com.seeds.NergetBackend.controller;

import com.seeds.NergetBackend.dto.AuthResponseDto;
import com.seeds.NergetBackend.entity.Member;
import com.seeds.NergetBackend.repository.MemberRepository;
import com.seeds.NergetBackend.security.JwtTokenProvider;
import com.seeds.NergetBackend.service.AuthService;
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
    public ResponseEntity<AuthResponseDto> googleLogin(@RequestBody Map<String, String> request) {
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

    @PostMapping("/test-login")
    public ResponseEntity<AuthResponseDto> testLogin() {
        // 이미 있는 유저라 가정
        Member member = memberRepository.findByEmail("testuser@gmail.com")
                .orElseThrow(() -> new RuntimeException("테스트 유저 없음"));

        String token = jwtTokenProvider.createToken(member.getEmail());

        AuthResponseDto response = new AuthResponseDto(token, false); // isNew = false
        return ResponseEntity.ok(response);
    }
}