-- NergetBackend DB 마이그레이션 스크립트
-- Job-ImageVector 연결 및 온보딩 Job 1:1 강제

USE nerget;

-- 1. jobs 테이블에 type 컬럼 추가
ALTER TABLE jobs 
  ADD COLUMN type VARCHAR(16) NOT NULL DEFAULT 'ONBOARDING';

-- 2. 사용자당 동일 type은 1개만 허용하는 unique index 생성
CREATE UNIQUE INDEX uk_jobs_user_type ON jobs(userId, type);

-- 3. image_vectors 테이블에 jobId FK 추가
ALTER TABLE image_vectors 
  ADD COLUMN jobId CHAR(36) NULL,
  ADD CONSTRAINT fk_image_vectors_job 
    FOREIGN KEY (jobId) REFERENCES jobs(id) 
    ON DELETE SET NULL;

-- 4. jobId에 대한 인덱스 생성
CREATE INDEX idx_image_vectors_job ON image_vectors(jobId);

-- 5. 기존 데이터가 있다면 type을 'ONBOARDING'으로 설정 (이미 DEFAULT로 설정됨)
-- UPDATE jobs SET type = 'ONBOARDING' WHERE type IS NULL;

-- 마이그레이션 완료 확인
SELECT 'Migration completed successfully' as status;



