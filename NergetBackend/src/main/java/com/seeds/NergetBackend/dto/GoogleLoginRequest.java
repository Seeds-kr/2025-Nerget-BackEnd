// dto/GoogleLoginRequest.java
package com.seeds.NergetBackend.dto;

import lombok.Getter;
@Getter
public class GoogleLoginRequest {
    private String idToken; // 프론트에서 받은 Google Credential (ID Token)
}