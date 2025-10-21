package com.seeds.NergetBackend.domain.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.seeds.NergetBackend.domain.auth.GoogleUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleOAuthService {

    @Value("${google.client-id}")
    private String clientId;

    public GoogleUser verifyIdToken(String idToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                    .Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken != null) {
                GoogleIdToken.Payload payload = googleIdToken.getPayload();

                return new GoogleUser(
                        payload.getEmail(),
                        (String) payload.get("name"),
                        (String) payload.get("picture")
                );
            } else {
                throw new RuntimeException("Invalid ID token.");
            }
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Google ID Token verification failed", e);
        }
    }
}