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
        // ✅ 메서드명 수정
        GoogleUser googleUser = googleOAuthService.verifyIdToken(idToken);

        Member member = memberRepository.findByEmail(googleUser.getEmail())
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .email(googleUser.getEmail())
                            .nickname(googleUser.getName())
                            .createdAt(LocalDateTime.now())
                            .build();
                    return memberRepository.save(newMember);
                });

        boolean isNew = member.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(5));

        String jwt = jwtTokenProvider.createToken(member.getEmail());

        return new AuthResponseDto(jwt, member.getNickname(), isNew);
    }
}