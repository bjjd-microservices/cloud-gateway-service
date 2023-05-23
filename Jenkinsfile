pipeline {
    agent any
    environment {
            GITHUB_CREDENTIALS = credentials('github-credentials-id')
    }
    tools {
        maven 'M3'
     }
    stages {
        stage('Checkout from Github') {
            steps{
             git url: 'https://github.com/bjjd-microservices/cloud-gateway-service.git' , branch: 'master'
            }
        }

    }

}

