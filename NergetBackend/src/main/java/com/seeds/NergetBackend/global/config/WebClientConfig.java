package com.seeds.NergetBackend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * AI 서버와 통신하기 위한 WebClient 설정
 * 실제 AI 서버 연동 시 주석 해제
 */
@Configuration
public class WebClientConfig {

    @Value("${ai.server.url}")
    private String aiServerUrl;

    @Value("${ai.server.timeout}")
    private int timeout;

    /**
     * AI 서버 통신용 WebClient
     * 
     * 사용법 (주석 해제 후):
     * 1. build.gradle에 추가:
     *    implementation 'org.springframework.boot:spring-boot-starter-webflux'
     * 
     * 2. 이 Bean 주석 해제
     * 
     * 3. EmbeddingService에서 사용:
     *    @Autowired private WebClient aiWebClient;
     */
    // @Bean
    // public WebClient aiWebClient() {
    //     HttpClient httpClient = HttpClient.create()
    //             .responseTimeout(Duration.ofMillis(timeout));
    //
    //     return WebClient.builder()
    //             .baseUrl(aiServerUrl)
    //             .clientConnector(new ReactorClientHttpConnector(httpClient))
    //             .defaultHeader("Content-Type", "application/json")
    //             .build();
    // }
}

