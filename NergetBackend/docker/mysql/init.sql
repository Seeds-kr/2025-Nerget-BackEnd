-- NergetBackend 데이터베이스 초기화 스크립트
-- 이 스크립트는 MySQL 컨테이너가 처음 시작될 때 실행됩니다

USE nerget;

-- 기본 테이블들은 JPA의 ddl-auto=update에 의해 자동 생성됩니다
-- 필요시 여기에 초기 데이터나 추가 설정을 추가할 수 있습니다

-- 예시: 기본 관리자 계정이나 초기 데이터가 필요한 경우
-- INSERT INTO member (email, nickname, created_at) VALUES ('admin@nerget.com', 'Admin', NOW());


