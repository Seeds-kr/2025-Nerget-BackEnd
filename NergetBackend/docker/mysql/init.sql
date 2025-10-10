-- NergetBackend 데이터베이스 초기화 스크립트

USE nerget;

-- 테스트 유저 생성
INSERT INTO members (member_id, email, nickname, password, created_at) 
VALUES (1, 'testuser@gmail.com', '테스트유저', NULL, NOW())
ON DUPLICATE KEY UPDATE email=email;

-- 샘플 이미지 벡터 20개 (후보용)
-- Placeholder 이미지 사용: https://via.placeholder.com/400x600
INSERT INTO image_vectors (id, user_id, s3key, v1, v2, v3, v4, status, created_at, updated_at)
VALUES 
  (UUID(), NULL, 'https://via.placeholder.com/400x600/FF6B6B/FFFFFF?text=Style+1', 0.8, 0.6, 0.9, 0.4, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://via.placeholder.com/400x600/4ECDC4/FFFFFF?text=Style+2', -0.5, 0.3, -0.7, 0.2, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://via.placeholder.com/400x600/45B7D1/FFFFFF?text=Style+3', 0.3, -0.4, 0.6, -0.3, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://via.placeholder.com/400x600/FFA07A/FFFFFF?text=Style+4', -0.7, 0.8, 0.2, 0.5, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://via.placeholder.com/400x600/98D8C8/FFFFFF?text=Style+5', 0.9, -0.2, -0.5, -0.6, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://via.placeholder.com/400x600/F7B731/FFFFFF?text=Style+6', -0.3, -0.6, 0.8, 0.7, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://via.placeholder.com/400x600/5F27CD/FFFFFF?text=Style+7', 0.6, 0.5, -0.4, -0.2, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://via.placeholder.com/400x600/00D2D3/FFFFFF?text=Style+8', -0.8, -0.7, 0.3, 0.8, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://via.placeholder.com/400x600/FF6348/FFFFFF?text=Style+9', 0.4, 0.9, -0.6, 0.3, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://via.placeholder.com/400x600/1B9CFC/FFFFFF?text=Style+10', -0.6, -0.3, 0.7, -0.5, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://via.placeholder.com/400x600/FDA7DF/FFFFFF?text=Style+11', 0.7, 0.4, 0.5, 0.6, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://via.placeholder.com/400x600/ED4C67/FFFFFF?text=Style+12', -0.4, 0.7, -0.8, -0.4, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://via.placeholder.com/400x600/12CBC4/FFFFFF?text=Style+13', 0.5, -0.5, 0.4, 0.2, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://via.placeholder.com/400x600/A3CB38/FFFFFF?text=Style+14', -0.9, 0.6, -0.3, 0.7, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://via.placeholder.com/400x600/D980FA/FFFFFF?text=Style+15', 0.2, -0.8, 0.9, -0.7, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://via.placeholder.com/400x600/FFC312/FFFFFF?text=Style+16', -0.2, 0.5, -0.6, 0.9, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://via.placeholder.com/400x600/C4E538/FFFFFF?text=Style+17', 0.8, -0.7, -0.2, -0.8, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://via.placeholder.com/400x600/006266/FFFFFF?text=Style+18', -0.5, -0.9, 0.5, 0.4, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://via.placeholder.com/400x600/EE5A6F/FFFFFF?text=Style+19', 0.6, 0.2, -0.9, 0.5, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://via.placeholder.com/400x600/009432/FFFFFF?text=Style+20', -0.7, -0.4, 0.6, -0.9, 'DONE', NOW(), NOW())
ON DUPLICATE KEY UPDATE s3key=s3key;
