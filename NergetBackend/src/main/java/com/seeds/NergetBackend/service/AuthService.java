package com.seeds.NergetBackend.service;

import com.seeds.NergetBackend.dto.AuthResponseDto;
import com.seeds.NergetBackend.entity.Member;
import com.seeds.NergetBackend.oauth.GoogleOAuthService;
import com.seeds.NergetBackend.oauth.GoogleUser;
import com.seeds.NergetBackend.repository.MemberRepository;
import com.seeds.NergetBackend.security.JwtTokenProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleOAuthService googleOAuthService;

    public AuthResponseDto handleGoogleLogin(String idToken) {
        GoogleUser googleUser = googleOAuthService.verifyIdToken(idToken);

        var existing = memberRepository.findByEmail(googleUser.getEmail());
        boolean isNew = existing.isEmpty(); // ✅ 새 회원 여부 정확히 판단

        Member member = existing.orElseGet(() -> memberRepository.save(
                Member.builder()
                        .email(googleUser.getEmail())
                        .nickname(googleUser.getName())
                        .createdAt(LocalDateTime.now())
                        .build()
        ));

        String jwt = jwtTokenProvider.createToken(member.getEmail());
        return new AuthResponseDto(jwt, member.getNickname(), isNew);
    }
}