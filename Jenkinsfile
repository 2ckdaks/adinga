pipeline {
  agent any

  environment {
    IMAGE_NAME   = 'api-gateway'
    DOCKERFILE   = 'backend/services/api-gateway/Dockerfile'
    DOCKER_CTX   = 'backend/services/api-gateway'
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Build & Push (GHCR)') {
      steps {
        withCredentials([usernamePassword(
          credentialsId: 'gh-user',
          usernameVariable: 'GH_USER',
          passwordVariable: 'GH_PAT'
        )]) {
          sh '''
            set -euo pipefail

            # GH 사용자명 정리: 소문자/개행 제거
            OWNER=$(printf "%s" "$GH_USER" | tr '[:upper:]' '[:lower:]' | tr -d ' \t\r\n')

            # 태그 생성 (빌드번호 사용)
            TAG="build-${BUILD_NUMBER}"

            echo "Owner=${OWNER}, Image=${IMAGE_NAME}, Tag=${TAG}"

            # GHCR 로그인
            echo "$GH_PAT" | docker login ghcr.io -u "$OWNER" --password-stdin

            # 빌드 & 푸시
            docker build -t ghcr.io/$OWNER/${IMAGE_NAME}:$TAG \
              -f "$DOCKERFILE" "$DOCKER_CTX"

            docker push ghcr.io/$OWNER/${IMAGE_NAME}:$TAG
          '''
        }
      }
    }
  }
}