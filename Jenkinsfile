pipeline {
  agent any

  stages {
    stage ('Build docker image') {
      steps {
        script {
          dockerapp = docker.build("ezband/ezband-app:${env.BUILD_ID}", '-f ./EzBand-Backend/Dockerfile ./EzBand-Backend')
        }
      }
    }

    stage ('push docker image') {
      steps {
        sh 'echo "Executando push"'
      }
    }

    stage ('Deploy') {
      steps {
        sh 'echo "Executando deploy"'
      }
    }
  }
}
