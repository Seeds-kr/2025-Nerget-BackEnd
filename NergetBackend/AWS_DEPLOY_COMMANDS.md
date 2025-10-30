# AWS 배포 명령어

## 현재 상태

- ✅ 코드 구현 완료
- ✅ GitHub 푸시 완료 (8954f88)
- ⏳ AWS 배포 필요

## 배포 방법

### 방법 1: EB CLI 사용 (권장)

```bash
cd /Users/parkjunseok/2025-Nerget-BackEnd/NergetBackend

# EB 초기화 (처음 한 번만)
eb init

# 배포 (기존 환경이 있다면)
eb deploy

# 또는 특정 환경으로 배포
eb deploy <environment-name>
```

### 방법 2: JAR 수동 업로드

```bash
cd /Users/parkjunseok/2025-Nerget-BackEnd/NergetBackend

# JAR 재빌드
./gradlew clean bootJar

# JAR 위치 확인
ls -lh build/libs/nerget-backend.jar

# AWS Console에서 업로드:
# 1. Elastic Beanstalk Console 접속
# 2. 환경 선택
# 3. "Upload and Deploy" 클릭
# 4. 위의 JAR 파일 업로드
```

### 방법 3: EB CLI 명령어 한 줄

```bash
cd /Users/parkjunseok/2025-Nerget-BackEnd/NergetBackend && eb deploy
```

## 배포 후 확인

```bash
# 환경 상태 확인
eb status

# 로그 확인
eb logs

# SSH 접속
eb ssh
```

## 업데이트된 API

새로 추가된 API:

- `POST /api/style/swipe` - 스와이프 처리
- `GET /api/style/recommend` - 추천 조회

## 배포 전 체크리스트

- ✅ 코드 컴파일 성공
- ✅ 로컬 테스트 성공
- ✅ GitHub 푸시 완료
- ⏳ AWS 배포 대기

