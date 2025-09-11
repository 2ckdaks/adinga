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
            IMAGE="ghcr.io/${OWNER}/${IMAGE_NAME}:${TAG}"

            echo "Owner=$OWNER, Image=${IMAGE}"

            echo "$GH_PAT" | docker login ghcr.io -u "$OWNER" --password-stdin

            docker build -t "${IMAGE}" -f "${DOCKERFILE}" "${DOCKER_CTX}"
            docker push "${IMAGE}"

            # 다음 스테이지에서 쓰도록 파일로 남겨도 됨
            echo "${IMAGE}" > image.txt
          '''
        }
      }
    }

    stage('Deploy to EKS') {
      steps {
        withCredentials([usernamePassword(
          credentialsId: 'aws-jenkins',
          usernameVariable: 'AWS_ACCESS_KEY_ID',
          passwordVariable: 'AWS_SECRET_ACCESS_KEY'
        )]) {
          sh '''
            set -e
            export AWS_DEFAULT_REGION=ap-northeast-2
            TAG="build-${BUILD_NUMBER}"
            IMAGE="ghcr.io/2ckdaks/${IMAGE_NAME}:${TAG}"

            aws eks update-kubeconfig --name adinga-dev --region ap-northeast-2

            cd backend/infra/k8s/base
            kubectl apply -k .

            kubectl -n adinga set image deploy/api-gateway api-gateway="${IMAGE}"
            kubectl -n adinga rollout status deploy/api-gateway
          '''
        }
      }
    }
  }
}
