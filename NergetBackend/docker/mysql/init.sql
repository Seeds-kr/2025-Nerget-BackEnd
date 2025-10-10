-- NergetBackend 데이터베이스 초기화 스크립트

USE nerget;

-- 테스트 유저 생성
INSERT INTO members (member_id, email, nickname, password, created_at) 
VALUES (1, 'testuser@gmail.com', '테스트유저', NULL, NOW())
ON DUPLICATE KEY UPDATE email=email;

-- 샘플 이미지 벡터 40개 (후보용)
-- Picsum Photos 사용 (다양한 실제 이미지)
INSERT INTO image_vectors (id, user_id, s3key, v1, v2, v3, v4, status, created_at, updated_at)
VALUES 
  (UUID(), NULL, 'https://picsum.photos/400/600?random=1', 0.8, 0.6, 0.9, 0.4, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=2', -0.5, 0.3, -0.7, 0.2, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=3', 0.3, -0.4, 0.6, -0.3, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=4', -0.7, 0.8, 0.2, 0.5, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=5', 0.9, -0.2, -0.5, -0.6, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=6', -0.3, -0.6, 0.8, 0.7, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=7', 0.6, 0.5, -0.4, -0.2, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=8', -0.8, -0.7, 0.3, 0.8, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=9', 0.4, 0.9, -0.6, 0.3, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=10', -0.6, -0.3, 0.7, -0.5, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=11', 0.7, 0.4, 0.5, 0.6, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=12', -0.4, 0.7, -0.8, -0.4, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=13', 0.5, -0.5, 0.4, 0.2, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=14', -0.9, 0.6, -0.3, 0.7, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=15', 0.2, -0.8, 0.9, -0.7, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=16', -0.2, 0.5, -0.6, 0.9, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=17', 0.8, -0.7, -0.2, -0.8, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=18', -0.5, -0.9, 0.5, 0.4, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=19', 0.6, 0.2, -0.9, 0.5, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=20', -0.7, -0.4, 0.6, -0.9, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=21', 0.3, 0.8, 0.7, 0.5, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=22', -0.6, 0.4, -0.5, 0.3, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=23', 0.5, -0.3, 0.8, -0.4, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=24', -0.4, 0.9, 0.1, 0.6, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=25', 0.7, -0.5, -0.3, -0.5, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=26', -0.8, -0.4, 0.6, 0.8, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=27', 0.4, 0.7, -0.7, -0.1, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=28', -0.9, -0.8, 0.4, 0.9, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=29', 0.6, 0.3, -0.8, 0.4, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=30', -0.3, -0.5, 0.9, -0.6, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=31', 0.9, 0.5, 0.6, 0.7, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=32', -0.7, 0.6, -0.4, -0.3, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=33', 0.2, -0.6, 0.5, 0.1, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=34', -0.1, 0.8, -0.9, 0.8, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=35', 0.8, -0.4, -0.6, -0.7, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=36', -0.4, -0.7, 0.7, 0.5, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=37', 0.5, 0.6, -0.5, -0.6, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=38', -0.6, -0.2, 0.8, 0.6, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=39', 0.7, 0.1, -0.7, 0.2, 'DONE', NOW(), NOW()),
  (UUID(), NULL, 'https://picsum.photos/400/600?random=40', -0.5, -0.8, 0.4, -0.8, 'DONE', NOW(), NOW())
ON DUPLICATE KEY UPDATE s3key=s3key;

-- 사용자 선호 벡터 추가 (홈 추천용)
INSERT INTO member_pref_vectors (member_id, v1, v2, v3, v4, sample_count, model, created_at, updated_at)
VALUES (1, 0.5, 0.6, 0.4, 0.3, 8, 'nerget-vision-v1', NOW(), NOW())
ON DUPLICATE KEY UPDATE v1=0.5, v2=0.6, v3=0.4, v4=0.3, sample_count=8;

-- 더미 게시글 추가
INSERT INTO posts (member_id, content, like_count, comment_count, bookmark_count, view_count, status, created_at, updated_at)
VALUES 
  (1, '오늘 코디 어때요? 🎨', 15, 3, 5, 120, 'ACTIVE', NOW(), NOW()),
  (1, '새로 산 옷 자랑 ✨', 28, 7, 12, 245, 'ACTIVE', NOW(), NOW()),
  (1, '이번 시즌 트렌드 스타일', 42, 11, 18, 380, 'ACTIVE', NOW(), NOW()),
  (1, '미니멀한 느낌으로', 9, 2, 4, 95, 'ACTIVE', NOW(), NOW()),
  (1, '빈티지 룩 도전해봤어요', 33, 8, 14, 290, 'ACTIVE', NOW(), NOW())
ON DUPLICATE KEY UPDATE content=content;

-- 게시글 이미지 연결 (방금 생성된 게시글 5개와 이미지 벡터 연결)
SET @post1 = (SELECT id FROM posts WHERE content = '오늘 코디 어때요? 🎨' LIMIT 1);
SET @post2 = (SELECT id FROM posts WHERE content = '새로 산 옷 자랑 ✨' LIMIT 1);
SET @post3 = (SELECT id FROM posts WHERE content = '이번 시즌 트렌드 스타일' LIMIT 1);
SET @post4 = (SELECT id FROM posts WHERE content = '미니멀한 느낌으로' LIMIT 1);
SET @post5 = (SELECT id FROM posts WHERE content = '빈티지 룩 도전해봤어요' LIMIT 1);

SET @img1 = (SELECT id FROM image_vectors WHERE s3key LIKE '%random=1%' LIMIT 1);
SET @img2 = (SELECT id FROM image_vectors WHERE s3key LIKE '%random=2%' LIMIT 1);
SET @img3 = (SELECT id FROM image_vectors WHERE s3key LIKE '%random=3%' LIMIT 1);
SET @img4 = (SELECT id FROM image_vectors WHERE s3key LIKE '%random=5%' LIMIT 1);
SET @img5 = (SELECT id FROM image_vectors WHERE s3key LIKE '%random=8%' LIMIT 1);

INSERT INTO post_images (post_id, image_vector_id, s3key, image_url, display_order, created_at)
SELECT @post1, @img1, 'https://picsum.photos/400/600?random=1', 'https://picsum.photos/400/600?random=1', 0, NOW()
WHERE @post1 IS NOT NULL AND @img1 IS NOT NULL
ON DUPLICATE KEY UPDATE s3key=s3key;

INSERT INTO post_images (post_id, image_vector_id, s3key, image_url, display_order, created_at)
SELECT @post2, @img2, 'https://picsum.photos/400/600?random=2', 'https://picsum.photos/400/600?random=2', 0, NOW()
WHERE @post2 IS NOT NULL AND @img2 IS NOT NULL
ON DUPLICATE KEY UPDATE s3key=s3key;

INSERT INTO post_images (post_id, image_vector_id, s3key, image_url, display_order, created_at)
SELECT @post3, @img3, 'https://picsum.photos/400/600?random=3', 'https://picsum.photos/400/600?random=3', 0, NOW()
WHERE @post3 IS NOT NULL AND @img3 IS NOT NULL
ON DUPLICATE KEY UPDATE s3key=s3key;

INSERT INTO post_images (post_id, image_vector_id, s3key, image_url, display_order, created_at)
SELECT @post4, @img4, 'https://picsum.photos/400/600?random=5', 'https://picsum.photos/400/600?random=5', 0, NOW()
WHERE @post4 IS NOT NULL AND @img4 IS NOT NULL
ON DUPLICATE KEY UPDATE s3key=s3key;

INSERT INTO post_images (post_id, image_vector_id, s3key, image_url, display_order, created_at)
SELECT @post5, @img5, 'https://picsum.photos/400/600?random=8', 'https://picsum.photos/400/600?random=8', 0, NOW()
WHERE @post5 IS NOT NULL AND @img5 IS NOT NULL
ON DUPLICATE KEY UPDATE s3key=s3key;
