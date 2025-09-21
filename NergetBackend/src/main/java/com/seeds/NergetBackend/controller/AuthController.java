package com.seeds.NergetBackend.controller;

import com.seeds.NergetBackend.dto.AuthResponseDto;
import com.seeds.NergetBackend.entity.Member;
import com.seeds.NergetBackend.repository.MemberRepository;
import com.seeds.NergetBackend.security.JwtTokenProvider;
import com.seeds.NergetBackend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "사용자 인증 관련 API")
public class AuthController {

    private final AuthService authService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "Google OAuth 로그인", description = "Google ID 토큰을 사용하여 사용자를 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                        "isNewUser": false
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/google")
    public ResponseEntity<AuthResponseDto> googleLogin(
            @Parameter(description = "Google ID 토큰", required = true,
                    example = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjE2NzAyNzQ4...")
            @RequestBody Map<String, String> request) {
        String idToken = request.get("idToken"); // 프론트에서 보내는 키 이름이 idToken
        AuthResponseDto response = authService.googleLogin(idToken);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "현재 사용자 정보 조회", description = "JWT 토큰을 통해 현재 로그인한 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "email": "user@example.com",
                                        "nickname": "사용자닉네임",
                                        "isNewUser": false,
                                        "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "인증 토큰이 없거나 유효하지 않음")
    })
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(
            @Parameter(description = "Bearer JWT 토큰", required = true)
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

    @Operation(summary = "테스트 로그인", description = "개발/테스트용 로그인입니다. 실제 Google OAuth 없이 테스트 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 로그인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                        "isNewUser": false
                                    }
                                    """))),
            @ApiResponse(responseCode = "500", description = "테스트 유저가 존재하지 않음")
    })
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