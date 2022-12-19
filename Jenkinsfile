"PIS-Backend test, build, deploy pipeline"
pipeline {
    agent any
    tools {
        jdk 'GraalVM'
    }
    stages {
        stage('SonarQube Analysis') {
            steps {
                sh './gradlew sonarqube \
                        -Dsonar.projectKey=pis-back \
                        -Dsonar.host.url=http://localhost:9000 \
                        -Dsonar.login=sqp_c226232c6e08776493055afd83f90e9c5560675a'
            }
        }
        stage ('Test') {
            steps {
                sh './gradlew test'
            }
        }
        stage ('Build') {
            steps {
                sh './gradlew nativeBuild'
                sh './check.sh check-image'
                sh 'docker build --tag back .'
            }
        }
        stage ('Deploy') {
            steps {
                sh './check.sh check-ps'
                sh 'docker run -d -p 8080:8080 --name back back'
            }
        }
    }
}