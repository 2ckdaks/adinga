pipeline {
  agent any

  environment {
    IMAGE_NAME = 'api-gateway'
    DOCKERFILE = 'backend/services/api-gateway/Dockerfile'
    DOCKER_CTX = 'backend/services/api-gateway'
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Docker Build & Push (GHCR)') {
      steps {
        withCredentials([usernamePassword(
          credentialsId: 'gh-user',
          usernameVariable: 'GH_USER',
          passwordVariable: 'GH_PAT'
        )]) {
          sh '''
            set -e
            OWNER=$(printf "%s" "$GH_USER" | tr '[:upper:]' '[:lower:]' | tr -d ' \t\r\n')
            TAG="build-${BUILD_NUMBER}"

            echo "Owner=$OWNER, Image=${IMAGE_NAME}, Tag=$TAG"

            # GHCR 로그인
            echo "$GH_PAT" | docker login ghcr.io -u "$OWNER" --password-stdin

            # Dockerfile(멀티스테이지)로 바로 빌드
            docker build -t ghcr.io/$OWNER/api-gateway:$TAG \
              -f backend/services/api-gateway/Dockerfile \
              backend/services/api-gateway

            docker push ghcr.io/$OWNER/${IMAGE_NAME}:$TAG
          '''
        }
      }
    }
  }
}
