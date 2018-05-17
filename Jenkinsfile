node {
    stage('Clone') {
        git url:  'git://reposerver/secretshare.git'
    }
    //def server = Artifactory.server 'artifactory'
    def rtGradle = Artifactory.newGradleBuild()
    rtGradle.useWrapper = true

    stage('Compile') {
        rtGradle.run tasks: 'build'
    }

    stage('Integration Tests') {
        rtGradle.run tasks: 'integTest'
    }

    stage('Jacoco Report') {
        rtGradle.run tasks: 'jacocoTestReport'
    }

    stage('SonarQube') {
        withSonarQubeEnv('sonar') {
            rtGradle.run tasks: 'sonarqube'
       }
    }
}
