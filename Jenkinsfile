"PIS-Backend test, build, deploy pipeline"
pipeline {
    agent any
    tools {
        jdk 'GraalVM'
    }
    stages {
        stage ('Test') {
            steps {
                sh './gradlew test'
            }
        }
        stage ('Build') {
            steps {
                sh './gradlew build'
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