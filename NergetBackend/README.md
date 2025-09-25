# NergetBackend

AI 기반 개인화 이미지 추천 시스템의 백엔드 서비스입니다.

## 🚀 빠른 시작 (Docker)

### 1. 저장소 클론

```bash
git clone <repository-url>
cd NergetBackend
```

### 2. 환경변수 설정 (선택사항)

```bash
# env.example을 참고하여 .env 파일 생성
cp env.example .env
# .env 파일에서 필요한 값들 수정
```

### 3. Docker Compose로 실행

```bash
# 모든 서비스 시작 (MySQL + Spring Boot)
docker-compose up -d

# 로그 확인
docker-compose logs -f app
```

### 4. 서비스 확인

- **API 서버**: http://localhost:8081
- **Docker MySQL**: localhost:3307 (Docker 컨테이너)
- **로컬 MySQL**: localhost:3306 (기존 로컬 DB)
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **API 문서 (JSON)**: http://localhost:8081/api-docs

## 🛠️ 개발 환경 설정

### 로컬에서 실행 (Docker 없이)

1. **MySQL 설치 및 실행**

```bash
# MySQL 8.0 설치 후
mysql -u root -p
CREATE DATABASE nerget;
```

2. **환경변수 설정**

```bash
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=your_password
```

3. **애플리케이션 실행**

```bash
./gradlew bootRun
```

## 📋 API 엔드포인트

> 💡 **Swagger UI에서 모든 API를 테스트할 수 있습니다!**  
> http://localhost:8081/swagger-ui.html

### 인증

- `POST /api/auth/google` - Google OAuth 로그인
- `GET /api/auth/me` - 현재 사용자 정보
- `POST /api/auth/test-login` - 테스트 로그인

### 플로우 관리

- `POST /api/flow/start` - 이미지 업로드 후 AI 처리 시작
- `GET /api/flow/{jobId}/status` - 작업 상태 조회

### 선택 및 추천

- `POST /api/choices` - 이미지 선호도 선택
- `GET /api/candidates` - 후보 이미지 추천
- `GET /api/home/recommendations` - 홈 피드 추천

### 검색

- `GET /api/search/mbti/{code}` - MBTI 기반 이미지 검색

## 📚 API 문서

### Swagger UI 사용법

1. **서버 실행 후 접속**: http://localhost:8081/swagger-ui.html
2. **인증 토큰 설정**:
   - 우상단 "Authorize" 버튼 클릭
   - `Bearer {your-jwt-token}` 형식으로 입력
3. **API 테스트**: 각 엔드포인트의 "Try it out" 버튼으로 직접 테스트 가능

### API 문서 다운로드

- **OpenAPI JSON**: http://localhost:8081/api-docs
- **Postman Collection**: Swagger UI에서 "Export" → "Download" 선택

## 🐳 Docker 명령어

```bash
# 서비스 시작
docker-compose up -d

# 서비스 중지
docker-compose down

# 로그 확인
docker-compose logs -f

# 특정 서비스 재시작
docker-compose restart app

# 볼륨까지 삭제 (데이터 초기화)
docker-compose down -v
```

## 🔧 환경변수

| 변수명                       | 설명                           | 기본값         |
| ---------------------------- | ------------------------------ | -------------- |
| `GOOGLE_CLIENT_ID`           | Google OAuth 클라이언트 ID     | -              |
| `GOOGLE_CLIENT_SECRET`       | Google OAuth 클라이언트 시크릿 | -              |
| `APP_S3_PUBLIC_BASE_URL`     | S3 버킷 공개 URL               | -              |
| `SPRING_DATASOURCE_USERNAME` | DB 사용자명                    | nerget         |
| `SPRING_DATASOURCE_PASSWORD` | DB 비밀번호                    | nergetpassword |

## 📁 프로젝트 구조

```
src/main/java/com/seeds/NergetBackend/
├── controller/     # REST API 컨트롤러
├── service/        # 비즈니스 로직
├── repository/     # 데이터 접근 계층
├── entity/         # JPA 엔티티
├── dto/           # 데이터 전송 객체
├── security/      # 보안 설정
└── config/        # 설정 클래스
```

## 🚨 문제 해결

### MySQL 연결 오류

```bash
# MySQL 컨테이너 상태 확인
docker-compose ps mysql

# MySQL 로그 확인
docker-compose logs mysql
```

### 포트 충돌

```bash
# 8080 포트 사용 중인 프로세스 확인
lsof -i :8080

# docker-compose.yml에서 포트 변경
ports:
  - "8081:8080"  # 8081로 변경
```

## 📝 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.
