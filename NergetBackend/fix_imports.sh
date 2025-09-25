#!/bin/bash

# 도메인별 패키지 수정 스크립트

echo "🔧 패키지 구조 리팩토링 중..."

# Auth 도메인
echo "📁 Auth 도메인 수정 중..."
find src/main/java/com/seeds/NergetBackend/domain/auth -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.controller;/package com.seeds.NergetBackend.domain.auth.controller;/g' {} \;
find src/main/java/com/seeds/NergetBackend/domain/auth -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.service;/package com.seeds.NergetBackend.domain.auth.service;/g' {} \;
find src/main/java/com/seeds/NergetBackend/domain/auth -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.entity;/package com.seeds.NergetBackend.domain.auth.entity;/g' {} \;
find src/main/java/com/seeds/NergetBackend/domain/auth -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.repository;/package com.seeds.NergetBackend.domain.auth.repository;/g' {} \;
find src/main/java/com/seeds/NergetBackend/domain/auth -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.dto;/package com.seeds.NergetBackend.domain.auth.dto;/g' {} \;

# Flow 도메인
echo "📁 Flow 도메인 수정 중..."
find src/main/java/com/seeds/NergetBackend/domain/flow -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.controller;/package com.seeds.NergetBackend.domain.flow.controller;/g' {} \;
find src/main/java/com/seeds/NergetBackend/domain/flow -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.service;/package com.seeds.NergetBackend.domain.flow.service;/g' {} \;
find src/main/java/com/seeds/NergetBackend/domain/flow -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.entity;/package com.seeds.NergetBackend.domain.flow.entity;/g' {} \;
find src/main/java/com/seeds/NergetBackend/domain/flow -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.repository;/package com.seeds.NergetBackend.domain.flow.repository;/g' {} \;
find src/main/java/com/seeds/NergetBackend/domain/flow -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.dto;/package com.seeds.NergetBackend.domain.flow.dto;/g' {} \;

# Choice 도메인
echo "📁 Choice 도메인 수정 중..."
find src/main/java/com/seeds/NergetBackend/domain/choice -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.controller;/package com.seeds.NergetBackend.domain.choice.controller;/g' {} \;
find src/main/java/com/seeds/NergetBackend/domain/choice -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.service;/package com.seeds.NergetBackend.domain.choice.service;/g' {} \;
find src/main/java/com/seeds/NergetBackend/domain/choice -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.entity;/package com.seeds.NergetBackend.domain.choice.entity;/g' {} \;
find src/main/java/com/seeds/NergetBackend/domain/choice -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.repository;/package com.seeds.NergetBackend.domain.choice.repository;/g' {} \;
find src/main/java/com/seeds/NergetBackend/domain/choice -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.dto;/package com.seeds.NergetBackend.domain.choice.dto;/g' {} \;

# Candidate 도메인
echo "📁 Candidate 도메인 수정 중..."
find src/main/java/com/seeds/NergetBackend/domain/candidate -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.controller;/package com.seeds.NergetBackend.domain.candidate.controller;/g' {} \;
find src/main/java/com/seeds/NergetBackend/domain/candidate -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.service;/package com.seeds.NergetBackend.domain.candidate.service;/g' {} \;
find src/main/java/com/seeds/NergetBackend/domain/candidate -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.dto;/package com.seeds.NergetBackend.domain.candidate.dto;/g' {} \;

# Search 도메인
echo "📁 Search 도메인 수정 중..."
find src/main/java/com/seeds/NergetBackend/domain/search -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.controller;/package com.seeds.NergetBackend.domain.search.controller;/g' {} \;

# Home 도메인
echo "📁 Home 도메인 수정 중..."
find src/main/java/com/seeds/NergetBackend/domain/home -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.controller;/package com.seeds.NergetBackend.domain.home.controller;/g' {} \;
find src/main/java/com/seeds/NergetBackend/domain/home -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.dto;/package com.seeds.NergetBackend.domain.home.dto;/g' {} \;

# Global 패키지
echo "📁 Global 패키지 수정 중..."
find src/main/java/com/seeds/NergetBackend/global -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.config;/package com.seeds.NergetBackend.global.config;/g' {} \;
find src/main/java/com/seeds/NergetBackend/global -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.security;/package com.seeds.NergetBackend.global.security;/g' {} \;
find src/main/java/com/seeds/NergetBackend/global -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.util;/package com.seeds.NergetBackend.global.util;/g' {} \;
find src/main/java/com/seeds/NergetBackend/global -name "*.java" -exec sed -i '' 's/package com\.seeds\.NergetBackend\.service;/package com.seeds.NergetBackend.global.common;/g' {} \;

echo "✅ 패키지 구조 리팩토링 완료!"

