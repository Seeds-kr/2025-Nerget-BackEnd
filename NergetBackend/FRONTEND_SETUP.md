# 프론트엔드 개발자용 NergetBackend 설정 가이드

## 🚀 빠른 시작

### 방법 1: Docker 이미지 파일 사용 (권장)

1. **Docker 이미지 파일 받기**

   ```bash
   # nerget-backend.tar 파일을 받은 후
   docker load -i nerget-backend.tar
   ```

2. **프로젝트 클론**

   ```bash
   git clone <repository-url>
   cd NergetBackend
   ```

3. **Docker Compose로 실행**

   ```bash
   # 프론트엔드용 설정으로 실행
   docker-compose -f docker-compose.frontend.yml up -d
   ```

4. **서비스 확인**
   - **API 서버**: http://localhost:8080
   - **Swagger UI**: http://localhost:8080/swagger-ui.html
   - **MySQL**: localhost:3306

### 방법 2: 소스에서 빌드

1. **프로젝트 클론**

   ```bash
   git clone <repository-url>
   cd NergetBackend
   ```

2. **Docker Compose로 빌드 및 실행**
   ```bash
   docker-compose -f docker-compose.frontend.yml up -d --build
   ```

## 🔧 환경 설정

### 환경변수 (선택사항)

```bash
# .env 파일 생성
cp env.example .env

# 필요한 값들 수정
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
APP_S3_PUBLIC_BASE_URL=https://your-bucket.s3.ap-northeast-2.amazonaws.com
```

## 📚 API 사용법

### 1. 테스트 로그인

```bash
curl -X POST http://localhost:8080/api/auth/test-login
```

### 2. JWT 토큰으로 API 호출

```bash
# 토큰 발급 후
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     http://localhost:8080/api/auth/me
```

### 3. Swagger UI에서 테스트

- http://localhost:8080/swagger-ui.html 접속
- "Authorize" 버튼으로 JWT 토큰 설정
- 각 API 엔드포인트 테스트

## 🐳 Docker 명령어

```bash
# 서비스 시작
docker-compose -f docker-compose.frontend.yml up -d

# 서비스 중지
docker-compose -f docker-compose.frontend.yml down

# 로그 확인
docker-compose -f docker-compose.frontend.yml logs -f

# 특정 서비스 재시작
docker-compose -f docker-compose.frontend.yml restart app

# 볼륨까지 삭제 (데이터 초기화)
docker-compose -f docker-compose.frontend.yml down -v
```

## 🔍 문제 해결

### MySQL 연결 오류

```bash
# MySQL 컨테이너 상태 확인
docker-compose -f docker-compose.frontend.yml ps mysql

# MySQL 로그 확인
docker-compose -f docker-compose.frontend.yml logs mysql
```

### 포트 충돌

```bash
# 8080 포트 사용 중인 프로세스 확인
lsof -i :8080

# docker-compose.frontend.yml에서 포트 변경
ports:
  - "8081:8080"  # 8081로 변경
```

## 📋 주요 API 엔드포인트

- `POST /api/auth/test-login` - 테스트 로그인
- `GET /api/auth/me` - 현재 사용자 정보
- `POST /api/flow/start` - 이미지 처리 시작
- `GET /api/flow/{jobId}/status` - 작업 상태 조회
- `GET /api/candidates` - 후보 이미지 조회
- `POST /api/choices` - 선호도 선택
- `GET /api/home/recommendations` - 홈 피드 추천
- `GET /api/search/mbti/{code}` - MBTI 기반 검색

## 💡 개발 팁

1. **Swagger UI 활용**: API 문서와 테스트를 한 번에
2. **JWT 토큰 관리**: 테스트 로그인으로 토큰 발급 후 사용
3. **로그 확인**: 문제 발생시 `docker-compose logs -f`로 디버깅
4. **데이터 초기화**: `docker-compose down -v`로 완전 초기화
