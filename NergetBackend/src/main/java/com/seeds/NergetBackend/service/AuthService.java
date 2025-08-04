package com.seeds.NergetBackend.service;

import com.seeds.NergetBackend.dto.AuthResponseDto;
import com.seeds.NergetBackend.entity.Member;
import com.seeds.NergetBackend.oauth.GoogleOAuthService;
import com.seeds.NergetBackend.oauth.GoogleUser;
import com.seeds.NergetBackend.repository.MemberRepository;
import com.seeds.NergetBackend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleOAuthService googleOAuthService;

    public AuthResponseDto handleGoogleLogin(String idToken) {
        GoogleUser googleUser = googleOAuthService.verifyToken(idToken);

        Optional<Member> existingMember = memberRepository.findByEmail(googleUser.getEmail());

        Member member;
        boolean isNew = false;

        if (existingMember.isPresent()) {
            member = existingMember.get();
        } else {
            member = new Member();
            member.setEmail(googleUser.getEmail());
            member.setNickname(googleUser.getName());
            member.setCreatedAt(LocalDateTime.now());
            member = memberRepository.save(member);
            isNew = true;
        }

        String jwt = jwtTokenProvider.createToken(member.getEmail());

        return new AuthResponseDto(jwt, member.getNickname(), isNew);
    }
}