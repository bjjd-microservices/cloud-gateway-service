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
      steps {
        git url: 'https://github.com/bjjd-microservices/cloud-gateway-service.git', branch: 'master'
      }
    }
    stage('QualityStatusGateCheck') {
      when {
                      branch 'production'
      }
      steps {
        dir('cloud-gateway-service') {
            script {
              withSonarQubeEnv('sonarserver') {
                sh "mvn sonar:sonar"
              }
              timeout(time: 1, unit: 'HOURS') {
                def qg = waitForQualityGate()
                if (qg.status != 'OK') {
                  error "Pipeline aborted due to quality gate failure: ${qg.status}"
                }
              }
            }
          }
      }
    }
    stage('Build Project') {
      steps {
        sh "mvn clean install -DskipTests -Pkubernetes"
        script {
          def version = sh script: 'mvn help:evaluate -Dexpression=project.version -q -DforceStdout', returnStdout: true
          print version
          env.version = version
        }
      }
    }
    stage('Build Docker Image and Push on Docker Hub') {
      steps {

          withCredentials([string(credentialsId: 'DockerHubPassword', variable: 'DockerHubPassword')]) {
            //1. Build docker image
             sh "docker build -t rajivbansal2981/cloud-gateway-service:${env.version} ."

            //2. Login and push docker image
            sh "docker login -u rajivbansal2981 -p ${DockerHubPassword}"
            sh "docker push rajivbansal2981/cloud-gateway-service:${env.version}"

            //3. Remove docker image from jenkins machine
            sh "docker rmi rajivbansal2981/cloud-gateway-service:${env.version}"

        }
      }
    }
    stage('Validating helm configurations using Datree'){
                 steps {
                     dir('k8s/') {
                        withEnv(['DATREE_TOKEN=3736500e-a75c-49ca-b271-24ea5f611c90']) {
                        //    sh "helm datree test cloud-gateway-service/"
                        }
                     }
                 }
           }
        stage('Package helm chart and Push to Github Repository') {
          steps {
            script{
                 withCredentials([string(credentialsId: 'NexusRepositoryPassword', variable: 'NexusRepositoryPassword')]) {
                    dir('k8s') {
                        def chartVersion =  sh script: "helm show chart cloud-gateway-service | grep version | cut -d: -f 2 | tr -d ' ' | tr -d '\\n'",returnStdout: true
                        def chartName =  sh script: "helm show chart cloud-gateway-service | grep name | cut -d: -f 2 | tr -d ' ' | tr -d '\\n'",returnStdout: true
                        def chartPackage ="${chartName}-${chartVersion}.tgz"
                        def chartPath = "cloud-gateway-service"

                        //1. Packaging helm chart
                        sh "helm package ${chartPath} --version ${chartVersion}"
                        sh "helm upgrade --install -n bjjd-system cloud-gateway-service cloud-gateway-service"

                      sh """
                       curl -u admin:$NexusRepositoryPassword -v http://localhost:8081/repository/helm_hosted/cloud-gateway-service/ --upload-file cloud-gateway-service-0.1.0.tgz
                     """
                  }
                }

            }
          }
        }
        stage('Deploy docker image on kubernetes using helm') {
                  steps {
                    script{

                            dir('k8s') {

                                //1. Packaging helm chart
                             //   sh "helm upgrade --install -n bjjd-system cloud-gateway-service cloud-gateway-service"



                        }

                    }
                  }
                }
        stage('Verify the Deployment on MicroK8s') {
              steps {

                    sh "kubectl version"
               }
        }
       stage('DeployDockerApplicationInJenkinsServer') {
                when {
                      branch 'production'
                }
                steps {
                      sh "docker run -d -p 4379:4379 --name cloud-gateway-service rajivbansal2981/cloud-gateway-service:${env.version}"
                  }
       }
  }
  post {
       		always {
       			mail bcc: '', body: "<br>Project: ${env.JOB_NAME} <br>Build Number: ${env.BUILD_NUMBER} <br> URL de build: ${env.BUILD_URL}", cc: '', charset: 'UTF-8', from: '', mimeType: 'text/html', replyTo: '', subject: "${currentBuild.result} CI: Project name -> ${env.JOB_NAME}", to: "rajivbansal2981@gmail.com";
       		}
       }
}
