pipeline {
  agent any

  stages {
    stage ('Build docker image') {
      steps {
        sh 'echo "Executando build"'
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
