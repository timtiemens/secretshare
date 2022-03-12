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

----

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


----

## Build
