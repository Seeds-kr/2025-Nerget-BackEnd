package com.seeds.NergetBackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey = Keys.hmacShaKeyFor("mySecretKey123456789012345678901234567890".getBytes());
    private final long validityInMilliseconds = 1000L * 60 * 60; // 1시간

    // 토큰 생성
    public String createToken(String email) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(email) // 👈 email을 subject에 넣음
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey)
                .compact();
    }

    // 토큰에서 이메일 꺼내기
    public String getEmail(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject(); // 👈 subject에 저장된 email 반환
    }
}