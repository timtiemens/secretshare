# Shamir's Secret Share in Java

----

Java implementation of Shamir's Secret Sharing algorithm 
as described in Applied Cryptography [as LaGrange Interpolating Polynomial Scheme].

----

Table of contents

 * [Dependencies](#dependencies)
 * [Build](#build)
 * [Officially Released Artifact](#officially-released-artifact)
 * [Architecture Documentation](#architecture-documentation)
 * [Use](#use)
   * [Examples of command line invocations](#examples-of-command-line-invocations)
   * [A complete scenario](#a-complete-scenario)
 * [Note on the Modulus](#note-on-the-modulus)
 * [Note on the size of 'k'](#note-on-the-size-of-k)
 * [Additional Documentation](#additional-documentation)


## Dependencies

The following are required to run the application in secretshare.jar:
 1. jre 11 (LTS)
 

The following are used to compile the project:
 1.  jdk 11 (LTS)
 2.  gradlew (uses gradle 5.6.2)


The following are used to completely build and test the project:
 1.  JUnit 4.x
This is needed for the (Unit and Integration) Test .java files to compile.
 
Note: the last version of this library to support jdk 1.6 (and jdk 8) was 1.4.4 - see [SecretShare 1.4.4 Release Tag] or [SecretShare 1.4.4 Maven Central].  It is sad to let 1.6 go, but Java SE 6 was released in 2006, and EOL'd in 2013.  Java SE 8 was released in 2014, and EOL'd in 2019/Jan.



## Build

1. Compile locally - build the project with gradlew (gradle wrapper)
```
    $ ./gradlew build
  [creates build/libs/secretshare-1.4.4.jar]
    $ cp build/libs/secretshare-1.4.4.jar ./secretshare.jar
  [copies the .jar into the current directory]
```

## Officially Released Artifact

2. Use the artifact in your build - dependency information:
```
      group:   com.tiemens
       name:   secretshare
    version:   1.4.4
```
Central Repository - [SecretShare 1.4.4] - to see dependency information
formatted for Maven, Ivy, Grape, Gradle, Buildr, etc.





[Original Sourceforge Secret Sharing in Java] - original SCM location.  Out-of-date.

[Resources] - more links to useful Shamir Secret Share documentation and projects


[Original Sourceforge Secret Sharing in Java]:http://secretsharejava.sourceforge.net/
[Resources]:extrastuff/resources.md
[SecretShare1.4.1]:http://mvnrepository.com/artifact/com.tiemens/secretshare/1.4.1
[SecretShare1.4.2]:http://mvnrepository.com/artifact/com.tiemens/secretshare/1.4.2
[SecretShare1.4.3]:http://mvnrepository.com/artifact/com.tiemens/secretshare/1.4.3
[SecretShare 1.4.4]:http://mvnrepository.com/artifact/com.tiemens/secretshare/1.4.4
[SecretShare1.4.4.alt]:https://search.maven.org/#artifactdetails%7Ccom.tiemens%7Csecretshare%7C1.4.4%7Cjar
[SecretShare 1.4.4 Release Tag]:https://github.com/timtiemens/secretshare/releases/tag/v1.4.4
[SecretShare 1.4.4 Maven Central]:http://mvnrepository.com/artifact/com.tiemens/secretshare/1.4.4
