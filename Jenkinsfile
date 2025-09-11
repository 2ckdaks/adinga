pipeline {
  agent any
  environment {
    SERVICE_DIR = 'backend/services/api-gateway'
    IMAGE_NAME  = 'api-gateway'
    TAG         = "build-${env.BUILD_NUMBER}"

    // 방금 설치한 JDK 17 경로로 맞춰주세요
    JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
    PATH      = "${JAVA_HOME}/bin:${PATH}"
  }
  stages {
    stage('Checkout') { steps { checkout scm } }

    stage('Gradle Build (outside Docker)') {
      steps {
        dir("${SERVICE_DIR}") {
          sh '''
            chmod +x gradlew || true
            ./gradlew --no-daemon -x test clean bootJar
          '''
        }
      }
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
            OWNER=$(printf "%s" "$GH_USER" | tr "[:upper:]" "[:lower:]" | tr -d " \t\r\n")
            echo "$GH_PAT" | docker login ghcr.io -u "$OWNER" --password-stdin

            docker build -t ghcr.io/$OWNER/${IMAGE_NAME}:${TAG} \
              -f ${SERVICE_DIR}/Dockerfile.jvm \
              --build-arg JAR=${SERVICE_DIR}/build/libs/*.jar \
              .

            docker push ghcr.io/$OWNER/${IMAGE_NAME}:${TAG}
          '''
        }
      }
    }
  }
}