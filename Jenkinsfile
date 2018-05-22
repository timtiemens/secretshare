pipeline {
    agent any

    parameters {
        booleanParam(
            defaultValue: false,
            description: 'Perform Integration Tests',
            name: 'INTEGRATION_TESTS')
    }

    stages {
        stage ('Clone') {
            steps {
                checkout scm
            }
        }
        stage ('Build') {
            // without 'tools', javadoc fails with this error
            // "Unable to find the 'javadoc' executable.  Tried home: /usr/lib/jvm/java-11-openjdk-amd64"
            tools {
                jdk 'jdk18'
            }
            steps {
                sh 'echo $PATH'
                sh 'java -version'
                sh './gradlew clean build'
                junit 'build/test-results/**/*.xml'
            }
        }
        stage ('Integration Tests') {
            when {
                // Only run integration tests if requested
                expression { params.INTEGRATION_TESTS }
            }
            steps {
                echo "Performing integration tests."
                sh './gradlew jacocoIntegTestReport'
                publishHTML(target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'build/reports/jacoco/jacocoIntegTestReport',
                        reportFiles: 'index.html',
                        reportName: "Jacoco Integ Report"
                 ])
            }
        }
        stage ('Jacoco Report') {
            when {
                // Only run integration tests if requested
                not { expression { params.INTEGRATION_TESTS } }
            }
            steps {
                sh './gradlew jacocoTestReport'
                publishHTML(target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'build/reports/jacoco/jacocoTestReport',
                        reportFiles: 'index.html',
                        reportName: "Jacoco Report"
                ])
            }
        }

    }
}
