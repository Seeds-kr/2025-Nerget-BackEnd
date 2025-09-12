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

    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html"
    };

    private static final String[] PUBLIC_APIS = {
            "/error", "/actuator/**", "/h2-console/**",
            "/api/auth/**"     // 로그인/토큰 발급 등 공개 API 있으면 여기에
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SWAGGER_WHITELIST).permitAll() // Swagger 항상 허용
                        .requestMatchers(PUBLIC_APIS).permitAll()       // 공개 API
                        .requestMatchers("/api/mypage/**").authenticated() // 마이페이지는 인증 필요
                        .anyRequest().permitAll() // 나머지는 상황에 따라 authenticated로 변경
                )
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable()) // JWT 쓰면 비활성 권장
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())); // H2 콘솔용
        return http.build();
    }
}
