# Nerget Backend ERD (Entity Relationship Diagram)

## Mermaid ERD

```mermaid
erDiagram
    MEMBERS {
        int memberId PK "AUTO_INCREMENT"
        string email UK "NOT NULL, UNIQUE"
        string password "NULLABLE (소셜로그인)"
        string nickname "VARCHAR(50)"
        datetime createdAt "NOT NULL, AUTO"
    }

    MEMBER_MBTI {
        long memberId PK,FK "NOT NULL"
        string mbti "VARCHAR(4), NOT NULL"
        string source "VARCHAR(32)"
        datetime createdAt "NOT NULL"
        datetime updatedAt "NOT NULL"
    }

    MEMBER_PREF_VECTORS {
        long memberId PK,FK "NOT NULL"
        float v1 "NOT NULL"
        float v2 "NOT NULL"
        float v3 "NOT NULL"
        float v4 "NOT NULL"
        int sampleCount "NOT NULL"
        string model "VARCHAR(64)"
        datetime createdAt "NOT NULL"
        datetime updatedAt "NOT NULL"
    }

    JOBS {
        string id PK "UUID(36), NOT NULL"
        string userId "VARCHAR(64)"
        enum type "ONBOARDING|RECALCULATION, NOT NULL, DEFAULT ONBOARDING"
        enum status "PENDING|RUNNING|DONE|FAILED"
        int progress "NOT NULL, 0-100"
        float v1 "4차원 벡터"
        float v2 "4차원 벡터"
        float v3 "4차원 벡터"
        float v4 "4차원 벡터"
        string error "VARCHAR(512)"
        datetime createdAt "NOT NULL"
        datetime updatedAt "NOT NULL"
    }

    IMAGE_VECTORS {
        string id PK "UUID(36), NOT NULL"
        string userId "VARCHAR(64)"
        string jobId FK "UUID(36), NULLABLE"
        string s3Key "VARCHAR(512), NOT NULL"
        float v1 "NOT NULL"
        float v2 "NOT NULL"
        float v3 "NOT NULL"
        float v4 "NOT NULL"
        text metaJson "TEXT"
        enum status "PENDING|RUNNING|DONE|FAILED"
        datetime createdAt "NOT NULL"
        datetime updatedAt "NOT NULL"
    }

    IMAGE_INTERACTIONS {
        long id PK "AUTO_INCREMENT"
        long memberId FK "NOT NULL"
        string imageVectorId FK "UUID(36), NOT NULL"
        int action "NOT NULL, +1=like, -1=dislike, 0=skip"
        double weight "NOT NULL, DEFAULT 1.0"
        datetime createdAt "NOT NULL"
    }

    %% Relationships
    MEMBERS ||--o| MEMBER_MBTI : "1:1"
    MEMBERS ||--o| MEMBER_PREF_VECTORS : "1:1"
    MEMBERS ||--o{ IMAGE_INTERACTIONS : "1:N"
    IMAGE_VECTORS ||--o{ IMAGE_INTERACTIONS : "1:N"
    JOBS ||--o{ IMAGE_VECTORS : "1:N"
```

## 테이블 설명

### 1. MEMBERS (회원)

- **주요 기능**: 사용자 기본 정보 관리
- **특징**: 소셜 로그인 지원 (password nullable)
- **인덱스**: email (UNIQUE)

### 2. MEMBER_MBTI (회원 MBTI)

- **주요 기능**: 사용자의 MBTI 결과 저장
- **관계**: MEMBERS와 1:1 관계
- **특징**: source 필드로 MBTI 산출 근거 관리

### 3. MEMBER_PREF_VECTORS (회원 선호도 벡터)

- **주요 기능**: 사용자의 누적된 선호도 벡터 저장
- **관계**: MEMBERS와 1:1 관계
- **특징**: 4차원 벡터로 선호도 표현, sampleCount로 학습 데이터 수 추적

### 4. JOBS (작업)

- **주요 기능**: AI 처리 작업 상태 관리
- **특징**: UUID 기반 ID, 4차원 벡터 결과 저장, 타입별 관리
- **상태**: PENDING → RUNNING → DONE/FAILED
- **타입**: ONBOARDING (최초 로그인), RECALCULATION (재계산)
- **제약**: 사용자당 ONBOARDING Job 1개만 허용

### 5. IMAGE_VECTORS (이미지 벡터)

- **주요 기능**: 이미지의 AI 분석 결과(벡터) 저장
- **특징**: S3 키와 4차원 벡터, 메타데이터 JSON 저장
- **인덱스**: userId, status, createdAt

### 6. IMAGE_INTERACTIONS (이미지 상호작용)

- **주요 기능**: 사용자의 이미지에 대한 반응 기록
- **관계**: MEMBERS와 IMAGE_VECTORS를 연결하는 중간 테이블
- **특징**: like/dislike/skip 액션, 가중치 지원
- **제약조건**: (memberId, imageVectorId) UNIQUE

## 주요 특징

1. **벡터 기반 추천 시스템**: 4차원 벡터를 사용한 AI 기반 추천
2. **상태 관리**: Job과 ImageVector에서 처리 상태 추적
3. **유연한 관계**: FK를 통한 느슨한 결합으로 확장성 확보
4. **인덱스 최적화**: 자주 조회되는 컬럼에 인덱스 설정
5. **메타데이터 지원**: JSON 형태의 확장 가능한 메타데이터 저장
