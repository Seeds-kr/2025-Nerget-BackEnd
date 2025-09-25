// src/main/java/com/seeds/NergetBackend/service/AuthService.java
package com.seeds.NergetBackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seeds.NergetBackend.dto.AuthResponseDto;
import com.seeds.NergetBackend.entity.Member;
import com.seeds.NergetBackend.repository.MemberRepository;
import com.seeds.NergetBackend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper om = new ObjectMapper();

    @Transactional
    public AuthResponseDto googleLogin(String idToken) {
        // 1) (개발용) 토큰 payload에서 email 추출
        String email = extractEmailFromIdTokenPayload(idToken);
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