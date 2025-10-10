# AI 서버 연동 가이드

## 📋 개요

이 문서는 NergetBackend와 AI 서버를 실제로 연동하는 방법을 설명합니다.  
현재는 **Mock 데이터**로 작동하며, AI 서버 연동 시 주석을 해제하여 실제 AI 기능을 사용할 수 있습니다.

---

## 🔧 AI 서버 API 스펙

### 1️⃣ 이미지 분석 API
- **Endpoint**: `POST /images/analyze`
- **Content-Type**: `multipart/form-data`
- **Parameters**:
  - `file`: 이미지 파일 (multipart)
  - `conf_threshold`: 신뢰도 임계값 (query, default: 0.8)
- **Response**:
  ```json
  {
    "v1": 0.234,
    "v2": -0.456,
    "v3": 0.789,
    "v4": 0.123
  }
  ```

### 2️⃣ 추천 API
- **Endpoint**: `POST /reco/by-user-vector`
- **Content-Type**: `application/json`
- **Request Body**:
  ```json
  {
    "vector": [0.1, 0.2, 0.3, 0.4],
    "top_k": 12
  }
  ```
- **Response**:
  ```json
  [
    {
      "image_id": "abc123",
      "url": "https://...",
      "score": 0.95
    },
    ...
  ]
  ```

---

## 🚀 연동 절차

### Step 1: 의존성 추가

`build.gradle`에 WebFlux 추가:

```gradle
dependencies {
    // 기존 의존성...
    
    // AI 서버 연동용 WebClient
    implementation 'org.springframework.boot:spring-boot-starter-webflux'
}
```

### Step 2: 환경 변수 설정

AI 서버 URL 설정 (`.env` 또는 환경 변수):

```bash
export AI_SERVER_URL=http://ai-server:8000
```

또는 `application.properties`에서 직접 수정:

```properties
ai.server.url=http://ai-server:8000
ai.server.timeout=30000
```

### Step 3: WebClient Bean 활성화

`src/main/java/com/seeds/NergetBackend/global/config/WebClientConfig.java` 파일에서 주석 해제:

```java
@Bean
public WebClient aiWebClient() {
    HttpClient httpClient = HttpClient.create()
            .responseTimeout(Duration.ofMillis(timeout));

    return WebClient.builder()
            .baseUrl(aiServerUrl)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .defaultHeader("Content-Type", "application/json")
            .build();
}
```

### Step 4: EmbeddingService 코드 전환

`src/main/java/com/seeds/NergetBackend/global/common/EmbeddingService.java`:

**1. import 주석 해제:**
```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import java.io.File;
import java.util.Map;
```

**2. WebClient 주입 주석 해제:**
```java
@Autowired
private WebClient aiWebClient;
```

**3. `embed()` 메서드 변경:**
- 현재 Mock 코드 제거 (또는 주석 처리)
- 주석 처리된 AI 호출 코드 활성화

### Step 5: ChoiceService 추천 로직 전환

`src/main/java/com/seeds/NergetBackend/domain/choice/service/ChoiceService.java`:

**1. WebClient 주입 추가:**
```java
@Autowired
private WebClient aiWebClient;
```

**2. `recommendHomeFeed()` 메서드 변경:**
- 현재 DB 벡터 계산 코드 제거 (또는 주석 처리)
- 주석 처리된 AI 추천 API 호출 코드 활성화

---

## 📂 주요 파일 위치

| 파일 | 역할 | 수정 사항 |
|------|------|-----------|
| `application.properties` | AI 서버 URL 설정 | `ai.server.url` 확인 |
| `WebClientConfig.java` | WebClient Bean 설정 | `@Bean` 주석 해제 |
| `EmbeddingService.java` | 이미지 벡터 분석 | AI 호출 코드 활성화 |
| `ChoiceService.java` | 홈 피드 추천 | AI 추천 API 활성화 |

---

## ✅ 체크리스트

- [ ] `build.gradle`에 `spring-boot-starter-webflux` 추가
- [ ] AI 서버 URL 환경 변수 설정 (`AI_SERVER_URL`)
- [ ] `WebClientConfig.java`의 `@Bean` 주석 해제
- [ ] `EmbeddingService.java`의 import 및 WebClient 주석 해제
- [ ] `EmbeddingService.embed()` 메서드 AI 코드 활성화
- [ ] `ChoiceService.java`에 WebClient 주입 추가
- [ ] `ChoiceService.recommendHomeFeed()` AI 코드 활성화
- [ ] 빌드 및 테스트: `./gradlew clean build`
- [ ] AI 서버 연결 확인

---

## 🐛 트러블슈팅

### 1. Connection Refused
```
원인: AI 서버가 실행되지 않았거나 URL이 잘못됨
해결: AI 서버 실행 확인 및 URL 검증
```

### 2. Timeout Error
```
원인: AI 서버 응답 지연
해결: application.properties의 ai.server.timeout 값 증가
```

### 3. 벡터 포맷 불일치
```
원인: AI 서버 응답 JSON 구조 변경
해결: EmbeddingService의 파싱 로직 확인
```

---

## 📝 현재 상태 (Mock)

- ✅ **이미지 분석**: 랜덤 가우시안 벡터 생성
- ✅ **추천 로직**: DB 벡터 유사도 계산
- ✅ **플로우**: 완벽하게 작동 (테스트용)

## 🎯 연동 후 상태 (Real AI)

- 🚀 **이미지 분석**: AI 서버 실제 분석
- 🚀 **추천 로직**: AI 서버 추천 엔진
- 🚀 **플로우**: 실제 AI 기반 작동

---

**작성일**: 2025-10-10  
**버전**: 1.0

