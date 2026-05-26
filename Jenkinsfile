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

                echo 'Deploying MASTER branch...'

                sh '''
                cp target/*.jar /opt/crm/crm.jar

                cd /opt/crm

                docker compose down

                docker compose up --build -d
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