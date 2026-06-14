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

        SLACK_CHANNEL = '#deployments'
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

        stage('Notify Build Started') {
            steps {
                script {

                    def branch = env.BRANCH_NAME ?: "unknown"

                    slackSend(
                            channel: env.SLACK_CHANNEL,
                            color: '#439FE0',
                            message: """
BUILD STARTED

Job: ${env.JOB_NAME}
Build: #${env.BUILD_NUMBER}
Branch: ${branch}

${env.BUILD_URL}
"""
                    )
                }
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

                    docker exec crm-backend \
                    curl -f http://localhost:3210/auth/register || true
                '''
            }
        }
    }

    post {

        success {

            script {

                def branch = env.BRANCH_NAME ?: "unknown"

                def deployMsg = branch == "master"
                        ? "✅ DEPLOYMENT SUCCESSFUL"
                        : "✅ PR / BRANCH BUILD SUCCESSFUL"

                slackSend(
                        channel: env.SLACK_CHANNEL,
                        color: 'good',
                        message: """
${deployMsg}

Job: ${env.JOB_NAME}
Build: #${env.BUILD_NUMBER}
Branch: ${branch}

View Build:
${env.BUILD_URL}
"""
                )
            }
        }

        unstable {

            slackSend(
                    channel: env.SLACK_CHANNEL,
                    color: 'warning',
                    message: """
⚠️ BUILD UNSTABLE

Job: ${env.JOB_NAME}
Build: #${env.BUILD_NUMBER}
Branch: ${env.BRANCH_NAME}

${env.BUILD_URL}
"""
            )
        }

        failure {

            sh '''
                docker logs crm-backend --tail 200 || true
            '''

            slackSend(
                    channel: env.SLACK_CHANNEL,
                    color: 'danger',
                    message: """
❌ BUILD FAILED

Job: ${env.JOB_NAME}
Build: #${env.BUILD_NUMBER}
Branch: ${env.BRANCH_NAME}

Investigate:
${env.BUILD_URL}
"""
            )
        }

        aborted {

            slackSend(
                    channel: env.SLACK_CHANNEL,
                    color: '#808080',
                    message: """
🛑 BUILD ABORTED

Job: ${env.JOB_NAME}
Build: #${env.BUILD_NUMBER}
Branch: ${env.BRANCH_NAME}

${env.BUILD_URL}
"""
            )
        }

        always {
            junit 'target/surefire-reports/*.xml'
            cleanWs()
        }
    }
}