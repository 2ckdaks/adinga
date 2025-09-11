pipeline {
  agent any
  environment {
    IMAGE_NAME = 'api-gateway'
    DOCKERFILE = 'backend/services/api-gateway/Dockerfile'
    DOCKER_CTX = 'backend/services/api-gateway'
  }
  stages {
    stage('Build & Push (GHCR)') {
      steps {
        withCredentials([usernamePassword(
          credentialsId: 'gh-user',
          usernameVariable: 'GH_USER',
          passwordVariable: 'GH_PAT'
        )]) {
          sh """
            /bin/bash -lc '
              set -euo pipefail

              OWNER=\$(printf "%s" "\$GH_USER" | tr "[:upper:]" "[:lower:]" | tr -d " \\t\\r\\n")
              TAG="build-${BUILD_NUMBER}"

              echo "Owner=\${OWNER}, Image=\${IMAGE_NAME}, Tag=\${TAG}"
              echo "\$GH_PAT" | docker login ghcr.io -u "\$OWNER" --password-stdin

              docker build -t ghcr.io/\${OWNER}/\${IMAGE_NAME}:\${TAG} \\
                -f "\${DOCKERFILE}" "\${DOCKER_CTX}"

              docker push ghcr.io/\${OWNER}/\${IMAGE_NAME}:\${TAG}
            '
          """
        }
      }
    }
  }
}
