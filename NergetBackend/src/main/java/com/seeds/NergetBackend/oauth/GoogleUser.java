package com.seeds.NergetBackend.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoogleUser {
    private String sub;
    private String email;
    private String name;
}