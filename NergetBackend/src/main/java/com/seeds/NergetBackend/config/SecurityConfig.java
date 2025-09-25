package com.seeds.NergetBackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource; // CorsConfig의 Bean

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth
                        // 실제 API 허용 (인증 붙이기 전까지)
                                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers("/auth/**", "/api/**", "/local-images/**", "/actuator/**").permitAll()
                                .anyRequest().permitAll()

                );
        return http.build();
    }
}