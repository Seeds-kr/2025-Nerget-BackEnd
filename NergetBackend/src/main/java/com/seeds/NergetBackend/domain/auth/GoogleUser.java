package com.seeds.NergetBackend.domain.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoogleUser {
    private String email;
    private String name;
    private String picture; // 추가
}