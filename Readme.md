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


## Architecture Documentation

See [Architecture.md](docs/Architecture.md)


## Use

   There are two main ways to use the application: split and combine.
   Split takes a secret (number or string) and splits it into 'n' shares.
   Combine takes 'k' of 'n' shares and re-creates the secret (number or string).

   a. split  - To display usage:
   ```
   $ java -jar secretshare.jar split
   ```

   b. combine  - To display usage:
   ```
   $ java -jar secretshare.jar combine
   ```

## Examples of command line invocations


<details>
  <summary>a. Create a share size 6 with threshold 3 with "Cat" as the secret string.   Note: the low modulus of 16639793 limits the size of the secret number, which in turn limits the length of the secret string.</summary><blockquote>

  ```
  $ java -jar secretshare.jar split -k 3 -n 6 -m 16639793 -sS "Cat"
  ```
  <details><summary>Sample output</summary>

  ```
  Secret Share version 1.4.5-SNAPSHOT
  Date                          : 2019-11-17 19:50:36
  UUID                          : f8d4ec0b-f3e0-4946-af1c-6142477beb04
  n = 6
  k = 3
  modulus = 16639793
  modulus = bigintcs:fde731-829FB0
  
  Share (x:1) = 9140967
  Share (x:2) = 4154189
  Share (x:3) = 6095319
  Share (x:4) = 14964357
  Share (x:5) = 14121510
  Share (x:6) = 3566778
  Share (x:1) = bigintcs:8b7ae7-32F939
  Share (x:2) = bigintcs:3f634d-23AA55
  Share (x:3) = bigintcs:5d01d7-DB6BF4
  Share (x:4) = bigintcs:e45685-F8C8D8
  Share (x:5) = bigintcs:d77a26-22E9B6
  Share (x:6) = bigintcs:366cba-B203FC
  ```
  </details>
</blockquote></details>




## Note on the Modulus

Using a shared modulus is ok - the modulus is NOT secret.
You can use a randomly generated prime modulus if you'd like.
It just takes longer.

```
Timing difference:                             Time To Generate Split
a) using -prime4096 on 512-character secret    ==    0.3 seconds
b) using -primeN    on 512-character secret    ==   57.1 seconds
```

## Note on the size of 'k'

N.B.: 'split' is perfectly capable of generating k=100, k=200, even k=1000
        in a reasonable amount of time (k=1000 took 35 seconds).
      Note, however, that for some values of k,  you will never [as
        a matter of practice] be able to recover the original secret
        from the shares with the secretshare.jar application.
      For k=1000, perhaps a super computer cluster
        could solve the matrix and recreate the original secret.  Maybe.

SIMPLEX SOLVER (versions 1.4.2 and later)

| k      |  seconds    | minutes | years |
| ---    | ---         | ---     | ---   |
|k = 22  |   0 seconds | | |
|k = 23  |   0 seconds | | |
|k = 24  |   0 seconds | | |
|k = 28  |   0 seconds | | |
|k = 29  |   0 seconds | | |
|k = 30  |   0.44 seconds
|k = 50  |   2 seconds
|k = 75  |   5 seconds
|k = 95  |  12 seconds
|k = 130 |  47 seconds
|k = 180 | 180 seconds
|k = 230 | 623 seconds
|k = 280 |1662 seconds | 28 minutes |
|k = 650 |     -       |  -         |                1 year
|k = 1000| 346276986880 seconds | - |           11,000 years

The formula is roughly "4 times longer for each +50 in k".
For k greater than 280, all times are calculated.  
k=280 is the last "measured".
So, k=280 is a pretty good practical limit, or k=95 for "immediate" results.

N.B.: Earlier versions of secretshare (1.4.1 and earlier) used a very
        inefficient solving algorithm.  For these versions, your "k" is
        in effect limited to less than k=30, and is probably more like k=20.

<details>
  <summary>ORIGINAL SOLVER (versions 1.4.1 and earlier)
Value 'k' versus recorded runtimes to complete the "combine" operation:</summary>

| k     |  seconds   | minutes    | hours    | days     |
|-------|------------|------------|----------|----------|
k = 19  |   3 seconds|
k = 20  |  10 seconds|
k = 21  |  39 seconds|
k = 22  | 156 seconds|
k = 23  | 646 seconds|
k = 24  |2460 seconds| 41 minutes |
k = 25  |            |164 minutes |
k = 26  |            |656 minutes | 11 hours |
k = 27  |            |            | 44 hours |
k = 28  |            |            |176 hours |  7.3 days|
k = 29  |            |            |          | 30   days|
k = 30  |            |            |          |120   days|

The formula is roughly 10 * 4^(k - 20) seconds.
For k greather than 25, all times are calculated. 
k=24 is the last "measured".
For k = 90, that works out to be  = 10 * 4^(90 - 20) = 1E42 seconds.
Since a year has ~3E7 seconds, that will never happen.
So, k=25 is a pretty good practical limit, or k=20 for "immediate" results.
</details>


## Additional Documentation

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
