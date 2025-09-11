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

          echo "Owner=\${OWNER}, Image=${IMAGE_NAME}, Tag=\${TAG}"

          # GHCR 로그인
          echo "\$GH_PAT" | docker login ghcr.io -u "\$OWNER" --password-stdin

          # 빌드 & 푸시
          docker build -t ghcr.io/\${OWNER}/${IMAGE_NAME}:\${TAG} \
            -f "${DOCKERFILE}" "${DOCKER_CTX}"

          docker push ghcr.io/\${OWNER}/${IMAGE_NAME}:\${TAG}
        '
      """
    }
  }
}