// src/main/java/com/seeds/NergetBackend/service/AuthService.java
package com.seeds.NergetBackend.domain.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seeds.NergetBackend.domain.auth.GoogleOAuthService;
import com.seeds.NergetBackend.domain.auth.GoogleUser;
import com.seeds.NergetBackend.domain.auth.dto.AuthResponseDto;
import com.seeds.NergetBackend.domain.auth.entity.Member;
import com.seeds.NergetBackend.domain.auth.repository.MemberRepository;
import com.seeds.NergetBackend.shared.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleOAuthService googleOAuthService;
    private final ObjectMapper om = new ObjectMapper();
    
    @Value("${app.auth.development-mode:false}")
    private boolean developmentMode;

    @Transactional
    public AuthResponseDto googleLogin(String idToken) {
        String email;
        
        if (developmentMode) {
            // 개발 모드: 검증 없이 payload만 파싱
            email = extractEmailFromIdTokenPayload(idToken);
            log.warn("DEVELOPMENT MODE: Google ID token verification is disabled!");
        } else {
            // 프로덕션 모드: 실제 Google 검증 수행
            try {
                GoogleUser googleUser = googleOAuthService.verifyIdToken(idToken);
                email = googleUser.getEmail();
                log.info("Google ID token verified for email: {}", email);
            } catch (Exception e) {
                log.error("Google ID token verification failed", e);
                throw new IllegalArgumentException("Invalid Google ID token: " + e.getMessage());
            }
        }
        
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Google ID token has no email");
        }

        // 2) upsert
        Member member = memberRepository.findByEmail(email).orElse(null);
        boolean isNew = false;
        if (member == null) {
            String nickname = email.split("@")[0]; // 간단히
            member = Member.builder()
                    .email(email)
                    .nickname(nickname)
                    .build();
            member = memberRepository.save(member);
            isNew = true;
            log.info("Created new member id={} email={}", member.getMemberId(), email);
        } else {
            log.info("Found existing member id={} email={}", member.getMemberId(), email);
        }

        // 3) 앱용 JWT
        String token = jwtTokenProvider.createToken(member.getEmail());
        return new AuthResponseDto(token, isNew);
    }

    /** 개발용: 서명 검증 없이 payload만 디코드해서 email 꺼냄 */
    private String extractEmailFromIdTokenPayload(String idToken) {
        try {
            String[] parts = idToken.split("\\.");
            if (parts.length < 2) return null;
            byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
            JsonNode node = om.readTree(payloadBytes);

            // 디버깅용 로그(원하면 주석)
            log.debug("[GSI] idToken payload={}", node.toString());

            // 표준 클레임
            if (node.has("email")) return node.get("email").asText();
            // Fallback: sub만 있는 경우 이메일 없음 → null
            return null;
        } catch (Exception e) {
            log.warn("Failed to parse idToken payload", e);
            return null;
        }
    }
}