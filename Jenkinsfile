"PIS-Backend test, build, deploy pipeline"
pipeline {
    agent any
    stages {
        stage ('Test') {
            steps {
                sh './gradlew test'
            }
        }
        stage ('Build') {
            steps {
                sh 'sdk install java 22.1.0.r17-grl -Y'
                sh 'gu install native-image'
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