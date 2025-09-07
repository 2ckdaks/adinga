pipeline {
  agent any

  environment {
    REGISTRY   = 'ghcr.io'
    OWNER      = '2ckdaks'
    K8S_NS     = 'adinga'

    IMAGE_API  = "${REGISTRY}/${OWNER}/api-gateway"
  }

  options { timestamps() }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
        script {
          // 짧은 커밋 SHA를 버전으로 사용
          env.VERSION = sh(returnStdout: true, script: 'git rev-parse --short=8 HEAD').trim()
        }
      }
    }

    stage('Docker Login') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'ghcr-creds', passwordVariable: 'GHCR_TOKEN', usernameVariable: 'GH_USER')]) {
          sh """
            echo "${GHCR_TOKEN}" | docker login ${REGISTRY} -u "${GH_USER}" --password-stdin
          """
        }
      }
    }

    stage('Build image: api-gateway') {
      steps {
        dir('backend/services/api-gateway') {
          sh """
            docker build -t ${IMAGE_API}:${VERSION} .
          """
        }
      }
    }

    stage('Push image') {
      steps {
        sh """
          docker push ${IMAGE_API}:${VERSION}
        """
      }
    }

    stage('Kubectl (switch context)') {
      steps {
        withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG_FILE')]) {
          sh '''
            mkdir -p $HOME/.kube
            cp "$KUBECONFIG_FILE" $HOME/.kube/config
          '''
        }
      }
    }

    stage('Deploy to K8s') {
      steps {
        sh """
          kubectl -n ${K8S_NS} set image deploy/api-gateway api-gateway=${IMAGE_API}:${VERSION}
          kubectl -n ${K8S_NS} rollout status deploy/api-gateway --timeout=120s
        """
      }
    }
  }

  post {
    success {
      echo "Deployed: ${IMAGE_API}:${VERSION}"
    }
    failure {
      echo "Deployment failed"
      sh 'kubectl -n ${K8S_NS} describe deploy/api-gateway || true'
      sh 'kubectl -n ${K8S_NS} get pods -l app=api-gateway -o wide || true'
    }
  }
}
