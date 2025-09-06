package com.seeds.NergetBackend.service;


import com.seeds.NergetBackend.dto.ProfileResponseDto;
import com.seeds.NergetBackend.dto.ProfileUpdateRequestDto;
import com.seeds.NergetBackend.entity.Member;
import com.seeds.NergetBackend.exception.DuplicateNicknameException;
import com.seeds.NergetBackend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {


    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public ProfileResponseDto me(String email) {
        Member m = findByEmailOrThrow(email);
        return toDto(m);
    }

    @Transactional
    public ProfileResponseDto update(String email, ProfileUpdateRequestDto dto) {
        Member m = findByEmailOrThrow(email);

        // 1) 입력 전처리
        String nickname = safeTrim(dto.getNickname());
        String bio = safeTrimNullable(dto.getBio());

        if (nickname.isEmpty()) {
            throw new IllegalArgumentException("닉네임은 공백만으로 구성될 수 없습니다.");
        }

        // (선택) 정규식/예약어 검증
        // if (!nickname.matches("^[a-zA-Z0-9가-힣_.]{2,20}$")) { ... }

        // 2) No-op 최적화: 값이 동일하면 바로 반환
        boolean sameNickname = nickname.equals(m.getNickname());
        boolean sameBio = (bio == null ? m.getBio() == null : bio.equals(m.getBio()));
        if (sameNickname && sameBio) {
            return toDto(m);
        }

        // 3) 닉네임 중복 체크 (변경되는 경우만)
        if (!sameNickname && memberRepository.existsByNicknameAndIdNot(nickname, m.getId())) {
            throw new DuplicateNicknameException(nickname);
        }

        // 4) 업데이트
        m.setNickname(nickname);
        m.setBio(bio);

        return toDto(m);
    }

    @Transactional
    public ProfileResponseDto updateAvatar(String email, String avatarUrl) {
        Member m = findByEmailOrThrow(email);
        // (선택) avatarUrl 전처리/검증 (길이, 형식)
        m.setAvatarUrl(avatarUrl);
        return toDto(m);
    }

    // --- helpers ---

    private Member findByEmailOrThrow(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        // (선택) MemberNotFoundException 로 교체 가능
    }

    private ProfileResponseDto toDto(Member m) {
        return new ProfileResponseDto(
                m.getId(),
                m.getEmail(),
                m.getNickname(),
                m.getBio(),
                m.getAvatarUrl()
        );
    }

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    private String safeTrimNullable(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t; // 빈문자는 null로 정규화
    }
}
