package com.seeds.NergetBackend.oauth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory; // ✅ 이거 누락되면 오류
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import org.springframework.beans.factory.annotation.Value; // ✅ @Value 어노테이션용 import
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleOAuthService {

    @Value("${google.client-id}")
    private String clientId;

    public GoogleUser verifyIdToken(String idToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                    .Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken token = verifier.verify(idToken);

            if (token != null) {
                Payload payload = token.getPayload();
                return new GoogleUser(
                        payload.getSubject(),
                        payload.getEmail(),
                        (String) payload.get("name")
                );
            } else {
                throw new RuntimeException("Invalid ID token");
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to verify token", e);
        }
    }
}