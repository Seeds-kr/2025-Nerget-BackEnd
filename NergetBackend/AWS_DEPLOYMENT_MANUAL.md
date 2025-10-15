# AWS 배포 수동 가이드

## 🚨 AWS 자격 증명 설정 필요

현재 제공된 정보는 AWS 콘솔 로그인 정보입니다. Elastic Beanstalk 배포를 위해서는 **Access Key ID**와 **Secret Access Key**가 필요합니다.

### 1. AWS Access Key 생성

1. **AWS 콘솔 로그인**

   - 이메일: `hanium_126@etechcloud.co.kr`
   - 비밀번호: `haniumAWS!//`

2. **IAM 서비스로 이동**

   - AWS 콘솔에서 "IAM" 검색
   - 좌측 메뉴에서 "사용자" 클릭
   - 본인 사용자명 클릭

3. **보안 자격 증명 탭**

   - "보안 자격 증명" 탭 클릭
   - "액세스 키 만들기" 버튼 클릭
   - "애플리케이션 외부에서 실행되는 코드" 선택
   - "다음" 클릭

4. **Access Key 생성**
   - Access Key ID와 Secret Access Key를 안전하게 저장
   - ⚠️ **Secret Access Key는 한 번만 표시되므로 반드시 저장하세요!**

### 2. AWS CLI 설정

```bash
aws configure
# AWS Access Key ID: [생성한 Access Key ID 입력]
# AWS Secret Access Key: [생성한 Secret Access Key 입력]
# Default region name: ap-northeast-2
# Default output format: json
```

### 3. Elastic Beanstalk 배포

#### 방법 A: EB CLI 사용 (권장)

```bash
# 1. EB 초기화
eb init

# 2. 애플리케이션 생성 및 배포
eb create nerget-backend-prod

# 3. 환경변수 설정
eb setenv SPRING_DATASOURCE_URL="jdbc:mysql://your-rds-endpoint:3306/nerget"
eb setenv SPRING_DATASOURCE_USERNAME="nerget"
eb setenv SPRING_DATASOURCE_PASSWORD="your-password"
eb setenv AI_SERVER_URL="http://your-ai-server:8000"
```

#### 방법 B: AWS 콘솔 수동 배포

1. **Elastic Beanstalk 콘솔 접속**

   - AWS 콘솔에서 "Elastic Beanstalk" 검색

2. **애플리케이션 생성**

   - "애플리케이션 생성" 클릭
   - 애플리케이션 이름: `nerget-backend`
   - 플랫폼: `Java 17`
   - 플랫폼 브랜치: `Java 17 running on 64bit Amazon Linux 2023`
   - 플랫폼 버전: 최신 버전

3. **애플리케이션 코드 업로드**

   - "로컬 파일에서 업로드" 선택
   - `build/libs/nerget-backend.jar` 파일 업로드

4. **환경변수 설정**
   - 환경 → 구성 → 소프트웨어 → 환경 속성
   - 다음 환경변수 추가:
     ```
     SPRING_DATASOURCE_URL=jdbc:mysql://your-rds-endpoint:3306/nerget
     SPRING_DATASOURCE_USERNAME=nerget
     SPRING_DATASOURCE_PASSWORD=your-password
     AI_SERVER_URL=http://your-ai-server:8000
     GOOGLE_CLIENT_ID=1076507336995-uo4b83nsjjc24rcot71546lntebifp5k.apps.googleusercontent.com
     GOOGLE_CLIENT_SECRET=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
     ```

### 4. RDS 데이터베이스 설정

1. **RDS 인스턴스 생성**

   - AWS 콘솔에서 "RDS" 검색
   - "데이터베이스 생성" 클릭
   - 엔진 유형: `MySQL`
   - 템플릿: `프리 티어`
   - DB 인스턴스 식별자: `nerget-db`
   - 마스터 사용자명: `nerget`
   - 마스터 비밀번호: 안전한 비밀번호 설정

2. **보안 그룹 설정**
   - VPC 보안 그룹에서 3306 포트 허용
   - Elastic Beanstalk 보안 그룹에서 접근 허용

### 5. 배포 확인

배포 완료 후 다음 URL로 접속하여 확인:

- **애플리케이션 URL**: `https://your-app-name.elasticbeanstalk.com`
- **테스트 로그인**: `https://your-app-name.elasticbeanstalk.com/api/auth/test-login`
- **Swagger UI**: `https://your-app-name.elasticbeanstalk.com/swagger-ui.html`

### 6. 문제 해결

#### 일반적인 문제들:

1. **데이터베이스 연결 실패**

   - RDS 보안 그룹에서 3306 포트 허용 확인
   - 데이터베이스 엔드포인트 URL 확인

2. **AI 서버 연결 실패**

   - AI 서버가 실행 중인지 확인
   - 방화벽 설정 확인

3. **Google OAuth 오류**
   - Google Cloud Console에서 리디렉션 URI 추가
   - `https://your-app-name.elasticbeanstalk.com/api/auth/google/callback`

### 7. 다음 단계

1. **도메인 연결** (선택사항)

   - Route 53에서 도메인 구매 및 연결

2. **SSL 인증서**

   - AWS Certificate Manager에서 SSL 인증서 발급

3. **모니터링 설정**
   - CloudWatch로 애플리케이션 모니터링

## 📞 지원

배포 과정에서 문제가 발생하면:

1. AWS 콘솔의 CloudWatch 로그 확인
2. Elastic Beanstalk 환경 상태 확인
3. 보안 그룹 및 네트워크 설정 점검
