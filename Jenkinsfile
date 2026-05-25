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

        DB_HOST = '168.220.248.162'
        DB_PORT = '5432'
        DB_NAME = 'logicdb'

        DB_CREDS = credentials('postgres-creds')

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

        stage('Build & Test') {
            steps {

                sh """
                mvn clean verify \
                -Dspring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME} \
                -Dspring.datasource.username=${DB_CREDS_USR} \
                -Dspring.datasource.password=${DB_CREDS_PSW}
                """
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
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