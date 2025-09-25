pipeline {
  agent any

  environment {
    SERVICES   = "api-gateway location-event-service notification-service todo-service trigger-engine-service"
    AWS_REGION = 'ap-northeast-2'
    EKS_CLUSTER= 'adinga-dev'
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Docker Build & Push (GHCR)') {
      steps {
        withCredentials([
          usernamePassword(credentialsId: 'gh-user',    usernameVariable: 'GH_USER', passwordVariable: 'GH_PAT'),
          usernamePassword(credentialsId: 'dockerhub',  usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS') // ⬅️ 추가
        ]) {
          sh '''
            set -eu

            # 1) Docker Hub 로그인 (베이스 이미지 pull 시 레이트리밋/401 방지)
            echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin

            # 2) GHCR 로그인 (이미지 push)
            OWNER=$(printf "%s" "$GH_USER" | tr '[:upper:]' '[:lower:]' | tr -d ' \t\r\n')
            TAG="build-${BUILD_NUMBER}"
            echo "$GH_PAT" | docker login ghcr.io -u "$OWNER" --password-stdin

            for SVC in ${SERVICES}; do
              IMAGE="ghcr.io/${OWNER}/${SVC}:${TAG}"
              DF="backend/services/${SVC}/Dockerfile"
              CTX="backend/services/${SVC}"

              echo "==> Build & Push ${IMAGE}"
              # 일시적 401/네트워크 이슈 완화: 베이스 이미지 미리 당겨두기
              # docker pull eclipse-temurin:17-jre || true
              # docker pull gradle:8.8-jdk17    || true

              docker build -t "${IMAGE}" -f "${DF}" "${CTX}"
              docker push "${IMAGE}"
              docker rmi "${IMAGE}" || true
              docker builder prune -af || true
            done

            echo "${TAG}" > tag.txt
          '''
        }
      }
    }

    stage('Deploy to EKS') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'aws-jenkins',
          usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')]) {
          sh '''
            set -eu
            export AWS_DEFAULT_REGION='ap-northeast-2'
            TAG=$(cat tag.txt || echo "build-${BUILD_NUMBER}")
            OWNER_LOWER=$(echo "${GH_USER:-2ckdaks}" | tr '[:upper:]' '[:lower:]' | tr -d ' \t\r\n')

            aws eks update-kubeconfig --name 'adinga-dev' --region 'ap-northeast-2'

            kubectl -n adinga apply -k backend/infra/k8s/base

            for SVC in ${SERVICES}; do
              DEPLOY="${SVC}"
              CONTAINER="app"
              [ "${SVC}" = "api-gateway" ] && CONTAINER="api-gateway"
              IMAGE="ghcr.io/${OWNER_LOWER}/${SVC}:${TAG}"
              kubectl -n adinga set image deploy/${DEPLOY} ${CONTAINER}=${IMAGE}
            done

            for DEP in ${SERVICES}; do
              kubectl -n adinga rollout status deploy/${DEP}
            done
          '''
        }
      }
    }

    stage('AWS smoke check') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'aws-jenkins',
          usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')]) {
          sh '''
            set -eu
            export AWS_DEFAULT_REGION='ap-northeast-2'
            aws sts get-caller-identity
            aws eks describe-cluster --name 'adinga-dev' --region 'ap-northeast-2' --query 'cluster.status'
          '''
        }
      }
    }
  }
}