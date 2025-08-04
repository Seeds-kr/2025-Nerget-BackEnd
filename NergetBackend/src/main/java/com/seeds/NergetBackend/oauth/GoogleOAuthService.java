package com.seeds.NergetBackend.oauth;

import org.springframework.stereotype.Service;

@Service
public class GoogleOAuthService {
    public GoogleUser verifyToken(String idToken) {
        GoogleUser user = new GoogleUser();
        user.setEmail("test@example.com");
        user.setName("테스트 유저");
        return user;
    }
}