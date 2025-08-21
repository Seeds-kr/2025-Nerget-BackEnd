package com.seeds.NergetBackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF: REST API 테스트 편의상 비활성화
                .csrf(csrf -> csrf.disable())

                // CORS 기본값
                .cors(Customizer.withDefaults())

                // 인가 규칙: 전부 허용 (개발/테스트용)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error", "/actuator/**", "/h2-console/**").permitAll()
                        .anyRequest().permitAll()
                )

                // 폼 로그인/세션 로그인 사용 안 함 (원하면 지워도 됨)
                .formLogin(form -> form.disable())
                .httpBasic(Customizer.withDefaults())

                // H2 콘솔 사용 시 frame 옵션
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
        ;

        return http.build();
    }
}
