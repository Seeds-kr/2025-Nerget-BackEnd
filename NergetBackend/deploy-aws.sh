#!/bin/bash

# AWS 배포 스크립트
echo "🚀 NergetBackend AWS 배포 시작..."

# 1. 프로젝트 빌드
echo "📦 프로젝트 빌드 중..."
./gradlew clean build -x test

# 2. JAR 파일 확인
if [ ! -f "build/libs/nerget-backend.jar" ]; then
    echo "❌ JAR 파일이 생성되지 않았습니다."
    exit 1
fi

echo "✅ JAR 파일 생성 완료: build/libs/nerget-backend.jar"

# 3. JAR 파일을 루트로 복사 (Elastic Beanstalk 요구사항)
echo "📋 JAR 파일을 루트로 복사 중..."
cp build/libs/nerget-backend.jar ./

# 4. AWS Elastic Beanstalk 배포 (EB CLI 설치 필요)
if command -v eb &> /dev/null; then
    echo "🌐 Elastic Beanstalk에 배포 중..."
    eb deploy
else
    echo "⚠️  EB CLI가 설치되지 않았습니다."
    echo "다음 명령어로 설치하세요:"
    echo "pip install awsebcli"
    echo ""
    echo "또는 수동으로 AWS 콘솔에서 배포하세요:"
    echo "1. AWS Elastic Beanstalk 콘솔 접속"
    echo "2. 'Create Application' 클릭"
    echo "3. Platform: Java 17"
    echo "4. Upload build/libs/nerget-backend.jar"
fi

echo "🎉 배포 완료!"
