pipeline {
  agent any
  environment {
    GH_USER    = credentials('gh-user')
    GHCR_TOKEN = credentials('ghcr-token')
    IMAGE      = "ghcr.io/${GH_USER}/api-gateway"
    TAG        = "build-${env.BUILD_NUMBER}"
  }
  triggers { githubPush() } // 웹훅 트리거

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }
    stage('Build Docker') {
      steps {
        sh """
          docker build -t ${IMAGE}:${TAG} -f backend/services/api-gateway/Dockerfile backend/services/api-gateway
        """
      }
    }
    stage('Push Docker (GHCR)') {
      steps {
        sh """
          echo ${GHCR_TOKEN} | docker login ghcr.io -u ${GH_USER} --password-stdin
          docker push ${IMAGE}:${TAG}
        """
      }
    }
  }
}