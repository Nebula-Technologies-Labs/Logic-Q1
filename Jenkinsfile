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

                    echo "===== CURRENT DIRECTORY ====="
                    pwd

                    echo "===== ROOT FILES ====="
                    ls -lah

                    echo "===== TARGET DIRECTORY ====="
                    ls -lah target

                    echo "===== JAR FILES ====="
                    find target -name "*.jar"

                    echo "===== DOCKER BUILD TEST ====="
                    docker build -t crm-test .

                    echo "===== DOCKER COMPOSE CONFIG ====="
                    docker compose config

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

                    for i in {1..12}
                    do
                        if curl -f http://localhost:3210/actuator/health; then
                            exit 0
                        fi

                        sleep 10
                    done

                    echo "Health check failed"
                    exit 1
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