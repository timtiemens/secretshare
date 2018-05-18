node {
    stage('Clone') 
        git url:  'git://reposerver/secretshare.git'

    env.JAVA_HOME="${tool 'jdk18'}"
    env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
    sh 'java -version'

    stage('Compile') 
        sh './gradlew clean build'

    stage('Integration Tests') 
        sh './gradlew integTest'

    stage('Jacoco Report') 
        sh './gradlew jacocoTestReport'

//    stage('SonarQube') 
//        withSonarQubeEnv('sonar') {
//            sh './gradlew sonarqube'
//        }
}

