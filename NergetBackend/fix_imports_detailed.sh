#!/bin/bash

echo "🔧 Import 문 수정 중..."

# 모든 Java 파일에서 import 문 수정
find src/main/java/com/seeds/NergetBackend -name "*.java" -exec sed -i '' \
    -e 's/import com\.seeds\.NergetBackend\.controller\./import com.seeds.NergetBackend.domain.auth.controller./g' \
    -e 's/import com\.seeds\.NergetBackend\.service\./import com.seeds.NergetBackend.domain.auth.service./g' \
    -e 's/import com\.seeds\.NergetBackend\.entity\./import com.seeds.NergetBackend.domain.auth.entity./g' \
    -e 's/import com\.seeds\.NergetBackend\.repository\./import com.seeds.NergetBackend.domain.auth.repository./g' \
    -e 's/import com\.seeds\.NergetBackend\.dto\./import com.seeds.NergetBackend.domain.auth.dto./g' \
    -e 's/import com\.seeds\.NergetBackend\.config\./import com.seeds.NergetBackend.global.config./g' \
    -e 's/import com\.seeds\.NergetBackend\.security\./import com.seeds.NergetBackend.global.security./g' \
    -e 's/import com\.seeds\.NergetBackend\.util\./import com.seeds.NergetBackend.global.util./g' \
    -e 's/import com\.seeds\.NergetBackend\.oauth\./import com.seeds.NergetBackend.domain.auth./g' \
    {} \;

echo "✅ Import 문 수정 완료!"

