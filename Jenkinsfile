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

                sh '''
            mkdir -p /opt/crm

            rsync -av --delete \
              --exclude .git \
              --exclude .env \
              ./ /opt/crm/

            cd /opt/crm

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
            sleep 20

            curl -f http://localhost:3210/actuator/health
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
        }

        always {
            junit 'target/surefire-reports/*.xml'
            cleanWs()
        }
    }
}