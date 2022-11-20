pipeline {
    agent {
        docker {
            image 'gradle:jdk15'
            args '-u root -e TESTCONTAINERS_RYUK_DISABLED=true -v /root/.gradle:/root/.gradle'
            reuseNode true
        }
    }
    stages {
        stage('Checkout code') {
            steps {
                checkout scm
            }
        }
        stage('Code build') {
            steps {
                sh 'gradle clean assemble'
            }
        }
        stage('Run unit and integration tests') {
            steps {
                sh 'gradle test'
            }
        }
        stage('Build Docker image') {
            environment {
                DOCKER_HUB = credentials('docker-hub')
                SPRING_DATASOURCE_PASSWORD = credentials('SPRING_DATASOURCE_PASSWORD')
            }
            steps {
                script {
                    sh 'gradle jib \
                        -Djib.to.image=${SERVICE_IMAGE_REGISTRY} \
                        -Djib.to.auth.username=${DOCKER_HUB_USR} \
                        -Djib.to.auth.password=${DOCKER_HUB_PSW} \
                        -Djib.container.jvmFlags=-Dsf.database.host="${SF_DATABASE_HOST}",-Dspring.datasource.password="${SPRING_DATASOURCE_PASSWORD}"'
                }
            }
        }
    }
    post {
        success {
            mail subject: "Build success: ${env.JOB_NAME} (${env.BUILD_NUMBER})",
                    body: """${env.BUILD_URL} build successfully""",
                      to: "${EMAIL_TO}"
        }
        failure {
            mail subject: "Build failed: ${env.JOB_NAME} (${env.BUILD_NUMBER})",
                    body: """${env.BUILD_URL} is failing""",
                      to: "${EMAIL_TO}"
        }
    }
}