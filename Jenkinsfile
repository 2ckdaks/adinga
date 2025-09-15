pipeline {
  agent any

  environment {
    // 빌드/배포 대상 서비스들
    SERVICES = "api-gateway location-event-service notification-service"
    AWS_REGION = 'ap-northeast-2'
    EKS_CLUSTER = 'adinga-dev'
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
            set -euo pipefail
            OWNER=$(printf "%s" "$GH_USER" | tr '[:upper:]' '[:lower:]' | tr -d ' \t\r\n')
            TAG="build-${BUILD_NUMBER}"

            echo "$GH_PAT" | docker login ghcr.io -u "$OWNER" --password-stdin

            for SVC in ${SERVICES}; do
              IMAGE="ghcr.io/${OWNER}/${SVC}:${TAG}"
              DF="backend/services/${SVC}/Dockerfile"
              CTX="backend/services/${SVC}"
              echo "==> Build & Push ${IMAGE}"
              docker build -t "${IMAGE}" -f "${DF}" "${CTX}"
              docker push "${IMAGE}"
            done

            # 나중 스테이지에서 참고할 수 있게 기록
            echo "${TAG}" > tag.txt
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
            set -euo pipefail
            export AWS_DEFAULT_REGION="${AWS_REGION}"
            TAG=$(cat tag.txt || echo "build-${BUILD_NUMBER}")
            OWNER_LOWER=$(echo "${GH_USER:-2ckdaks}" | tr '[:upper:]' '[:lower:]' | tr -d ' \t\r\n')

            aws eks update-kubeconfig --name "${EKS_CLUSTER}" --region "${AWS_REGION}"

            # 모든 매니페스트(게이트웨이/각 서비스) 적용
            kubectl -n adinga apply -k backend/infra/k8s/base

            # 최신 이미지로 교체
            for SVC in ${SERVICES}; do
              DEPLOY="${SVC}"
              CONTAINER="app"
              # api-gateway는 컨테이너 이름이 'api-gateway' 라면 처리
              if [ "${SVC}" = "api-gateway" ]; then
                CONTAINER="api-gateway"
              fi
              IMAGE="ghcr.io/${OWNER_LOWER}/${SVC}:${TAG}"
              echo "==> set image deploy/${DEPLOY} ${CONTAINER}=${IMAGE}"
              kubectl -n adinga set image deploy/${DEPLOY} ${CONTAINER}=${IMAGE}
            done

            # 롤아웃 확인
            for DEP in ${SERVICES}; do
              kubectl -n adinga rollout status deploy/${DEP}
            done
          '''
        }
      }
    }

    stage('AWS smoke check') {
      steps {
        withCredentials([usernamePassword(
          credentialsId: 'aws-jenkins',
          usernameVariable: 'AWS_ACCESS_KEY_ID',
          passwordVariable: 'AWS_SECRET_ACCESS_KEY'
        )]) {
          sh '''
            set -e
            export AWS_DEFAULT_REGION="${AWS_REGION}"
            aws sts get-caller-identity
            aws eks describe-cluster --name "${EKS_CLUSTER}" --region "${AWS_REGION}" --query 'cluster.status'
          '''
        }
      }
    }
  }
}
