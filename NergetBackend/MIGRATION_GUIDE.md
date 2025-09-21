# NergetBackend 마이그레이션 가이드

## Job-ImageVector 연결 및 온보딩 Job 1:1 강제

### 📋 변경 사항 요약

1. **Job 엔티티**: `type` 필드 추가 (ONBOARDING, RECALCULATION)
2. **ImageVector 엔티티**: `jobId` FK 추가
3. **DB 제약**: 사용자당 ONBOARDING Job 1개만 허용
4. **서비스 로직**: Job-ImageVector 관계 처리

### 🗄️ 데이터베이스 마이그레이션

#### 방법 1: 자동 마이그레이션 (권장)
현재 `ddl-auto=update` 설정으로 인해 애플리케이션 시작 시 자동으로 스키마가 업데이트됩니다.

#### 방법 2: 수동 마이그레이션
기존 데이터가 있는 경우 수동으로 마이그레이션을 실행할 수 있습니다:

```bash
# MySQL에 접속
mysql -u root -p nerget

# 마이그레이션 스크립트 실행
source /path/to/NergetBackend/docker/mysql/migration.sql
```

### 🔧 주요 변경사항

#### 1. Job 엔티티 업데이트
- `type` 필드 추가 (기본값: ONBOARDING)
- `userId + type` 유니크 제약 추가

#### 2. ImageVector 엔티티 업데이트
- `jobId` FK 필드 추가
- Job과의 관계 설정

#### 3. 서비스 로직 업데이트
- `JobService`: 온보딩 Job 1:1 제약 검증
- `ImageVectorService`: Job 연결 처리
- `FlowController`: 예외 처리 추가

### 🚀 배포 시 주의사항

1. **기존 데이터**: 기존 Job들은 자동으로 `type='ONBOARDING'`으로 설정됩니다.
2. **유니크 제약**: 기존에 동일 사용자의 중복 Job이 있다면 마이그레이션 실패 가능성이 있습니다.
3. **롤백**: 필요시 마이그레이션 스크립트의 역방향 작업을 수행해야 합니다.

### 📝 API 변경사항

#### FlowController
- `POST /api/flow/start`: 온보딩 Job 중복 생성 시 400 에러 반환

#### 새로운 제약사항
- 사용자당 ONBOARDING Job은 1개만 생성 가능
- ImageVector는 특정 Job에 연결되어 관리됨

### 🔍 검증 방법

1. **DB 스키마 확인**:
```sql
DESCRIBE jobs;
DESCRIBE image_vectors;
SHOW INDEX FROM jobs;
SHOW INDEX FROM image_vectors;
```

2. **제약사항 테스트**:
```sql
-- 동일 사용자의 ONBOARDING Job 중복 생성 시도
INSERT INTO jobs (id, userId, type, status, progress, createdAt, updatedAt) 
VALUES ('test-1', 'user1', 'ONBOARDING', 'PENDING', 0, NOW(), NOW());
INSERT INTO jobs (id, userId, type, status, progress, createdAt, updatedAt) 
VALUES ('test-2', 'user1', 'ONBOARDING', 'PENDING', 0, NOW(), NOW());
-- 두 번째 INSERT는 실패해야 함
```

3. **API 테스트**:
```bash
# 동일 사용자로 두 번 온보딩 시도
curl -X POST http://localhost:8080/api/flow/start \
  -H "Content-Type: application/json" \
  -d '{"userId": "test-user", "s3Keys": ["key1", "key2"]}'
# 두 번째 요청은 400 에러 반환
```


