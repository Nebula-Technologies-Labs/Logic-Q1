pipeline {
    agent any

    tools {
        jdk 'jdk25'
        maven 'maven3'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 20, unit: 'MINUTES')
    }

    environment {
        APP_NAME = 'crm'
        SPRING_PROFILES_ACTIVE = 'test'
        MAVEN_OPTS = '-Xmx1024m -Dmaven.repo.local=.m2/repository'
        DEPLOY_DIR = '/opt/crm'
    }

    triggers {
        githubPush()
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Verify Environment') {
            steps {
                sh 'java -version'
                sh 'mvn -version'
                sh 'docker --version'
            }
        }

        stage('Build, Test & Package') {
            steps {
                sh '''
                mvn clean package \
                  -Dspring.profiles.active=test
            '''
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Deploy To Production') {

            when {
                branch 'master'
            }

            steps {

                echo 'Deploying master branch...'

                sh '''
                mkdir -p ${DEPLOY_DIR}

                rsync -av --delete \
                  --exclude .git \
                  --exclude .env \
                  ./ ${DEPLOY_DIR}/

                cd ${DEPLOY_DIR}

                docker compose down || true
                docker compose up -d --build
            '''
            }
        }

        stage('Health Check') {

            when {
                branch 'master'
            }

            steps {

                sh '''
                echo "Waiting for application startup..."

                sleep 20

                docker ps | grep crm-backend

                docker logs crm-backend --tail 50

                docker exec crm-backend \
                  curl -f http://localhost:3210/auth/register || true
            '''
            }
        }
    }

    post {

        success {
            echo 'Build completed successfully.'
        }

        failure {
            echo 'Build failed.'

            sh '''
            docker logs crm-backend --tail 200 || true
        '''
        }

        always {
            junit 'target/surefire-reports/*.xml'
            cleanWs()
        }
    }
}