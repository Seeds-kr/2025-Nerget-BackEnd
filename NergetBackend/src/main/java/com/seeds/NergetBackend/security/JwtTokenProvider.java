package com.seeds.NergetBackend.security;

import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    public String createToken(String email) {
        return "mock-jwt-token-for-" + email;
    }
}