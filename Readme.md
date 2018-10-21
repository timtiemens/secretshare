Shamir's Secret Share in Java
==============================
Java implementation of Shamir's Secret Sharing algorithm 
as described in Applied Cryptography [as LaGrange Interpolating Polynomial Scheme].


Dependencies
-------------
The following are required to run the application in secretshare.jar:
 1. jre 1.6+
 

The following are used to compile the project:
 1.  jdk 1.6+
 2.  gradlew (uses gradle 2.2.1)


The following are used to completely build and test the project:
 1.  JUnit 4.x
This is needed for the (Unit and Integration) Test .java files to compile.
 
 
Build
------

1. Compile locally - build the project with gradlew (gradle wrapper)
```
    $ ./gradlew build
  [creates build/libs/secretshare-1.4.4.jar]
    $ cp build/libs/secretshare-1.4.4.jar ./secretshare.jar
  [copies the .jar into the current directory]
```

Officially Released Artifact
------

2. Use the artifact in your build - dependency information:
```
      group:   com.tiemens
       name:   secretshare
    version:   1.4.4
```
Central Repository - [SecretShare1.4.4] - to see dependency information
formatted for Maven, Ivy, Grape, Gradle, Buildr, etc.


Use
-----

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
    
Examples of command line invocations
-----

  a. Create a share size 6 with threshold 3 with "Cat" as the secret string.   Note: the low modulus of 16639793 limits the size of the secret number, which in turn limits the length of the secret string.
  ```
  $ java -jar secretshare.jar split -k 3 -n 6 -m 16639793 -sS "Cat"
  ```

  b. Create a share size 6 with threshold 3 as above, but pipes the output of "split" to the input of "combine", which then re-creates the secret number and the secret string "Cat".
  ```
  $ java -jar secretshare.jar split -k 3 -n 6 -m 16639793 -sS "Cat" \
   | java -jar secretshare.jar combine -stdin
  ```

  c. Create a share size 6 with threshold 3 as above, but with a long secret string.  Note: no modulus was given, so a pre-defined 384-bit prime was used as the modulus.  The 384 bit prime allows 48 characters of secret string.
  ```
  $ java -jar secretshare.jar split -k 3 -n 6 -sS "The Cat In The Hat"
  ```

  d. Create the same share as above, then pipes the output of "split" into the input of "combine", which prints out the secret string.
  ```
  $ java -jar secretshare.jar split -k 3 -n 6 -sS "The Cat In The Hat" \
   | java -jar secretshare.jar combine -stdin
  ```

  e. Create the same share as above, but use a pre-defined 4096 bit prime modulus.  The 4096 bit prime allows 512 characters of secret string.
  ```
  $ java -jar secretshare.jar split -k 3 -n 6 -prime4096 \
      -sS "The Cat In The Hat 4096bits"
  ```

  f. Create the same share as above, but output in a manner better suited for physically splitting up the shares in order to give them out individually with all required information.
  ```
  $ java -jar secretshare.jar split -k 3 -n 6 -prime4096 \
      -sS "The Cat In The Hat 4096bits" -printIndiv
  ```

  g. Combine 3 shares to recreate the original secret.  Note: it is important that the -prime argument is specified before -s arguments.
  ```
  $ java -jar secretshare.jar combine -k 3 -prime384 \
      -s2 1882356874773438980155973947620693982153929916 \
      -s4 1882357204724127580025723830249209987221192644 \
      -s5 1882357444072759374568880025530775541595539408
  ```

  h. Combine 4 shares, 3 good and 1 bad, using paranoid combination option.
  ```
  $ java -jar secretshare.jar combine -k 3 -prime384 \
      -paranoid 4 \
      -s2 1882356874773438980155973947620693982153929916 \
      -s3 12345678912345678912345678912345678 \
      -s4 1882357204724127580025723830249209987221192644 \
      -s5 1882357444072759374568880025530775541595539408
  ```

  i. Combine shares, showing examples for the -paranoid argument.   Control how many extra combines to run (110), how many to print (4), and stop when an answer  has been seen at least this many times (30).  Use the -paranoid option if you (1) have extra shares and (2) some of your shares are corrupt.
  ```
  $ java -jar secretshare.jar combine -k 3 -m 16639793 \
      -paranoid 110,limitPrint=4,stopCombiningWhenAnyCount=30 \
      -s1 7210616 -s2 11715382 -s3 4444444 -s4 9215151 -s5 2210154 \
      -s6 13554960 -s7 9969983 -s8 8095016 -s9 7654321 -s10 1234567
  ```

  j. Print information about Secret Share, including version, 192 bit, 384 bit and 4096 bit primes.
  ```
  $ java -jar secretshare.jar info
  ```

Important Notes about Shares of the Secret
-----
Note that each share of the secret requires at least these pieces:
 1. the "prime" modulus value [same for all shares],
 2. the "k" value [same for all shares],
 3. the "x" value     [unique for this share],
 4. the "share" value [unique for this share]

Optional - the UUID of the split [same for all shares]
  If you have split multiple secrets into shares,
it is also nice to have the UUID of the split operation,
so that you can make sure all your shares belong to the same split.
Due to the nature of the algorithm, shares from different splits
will 'combine' and will produce a ''secret'' (string or number),
but it will not be the original secret.

Note on the Prime Modulus
-----
  By default, the 384-bit prime is used for the split/combine operations.
  Place the "-prime" option before all -s* arguments.
  If no calculation in the split was larger than the modulus, then it turns out the prime argument is not required.  Determining when this is or is not true is a bit tricky, however.

Note on the Secret
-----
From above, you can see the largest pre-defined prime modulus is 4096 bits, which only allows 512 characters of secret.
In case it isn't obvious, the best way to use the secret share program is to use it to split the secret key that was used by a symmetric encryption program to encrypt the actual secret --  i.e. split and share the key, not the original secret content.
See gpg -c (aka gpg --symmetric) for an example of symmetric encryption.  See openssl enc for another example of symmetric encryption.

A complete scenario:
-----

```
  # Encrypt the PAYLOAD.
$ cat PAYLOAD.txt
   This is the PAYLOAD
   Generated Wed May  9 19:58:01 CDT 2018
   It has spaces at the front of every line
$ openssl enc -pass pass:TheKeyUsedToEncrypt -aes-256-cbc -salt \
              -in PAYLOAD.txt -out PAYLOAD.enc
$ ls -l PAYLOAD.enc
-rw-r--r--  1 timtiemens  users   128 May  9 19:58 PAYLOAD.enc

  # Create the shares from the key.
  # Use '-printIndiv' to make it easier to distribute the shares.
$ PRINT=-printIndiv
  # For this example, we'll print them all together
$ PRINT=-printOne
$ java -jar secretshare.jar split -k 3 -n 6 -sS "TheKeyUsedToEncrypt" $PRINT
Secret Share version 1.4.4
Date                          : 2018-05-09 19:59:55
UUID                          : 5c1ed0de-b281-49db-890c-4fc12321775a
n = 6
k = 3
modulus = 83085671664126938805092614721037843700776366159998897420433674117190444262260240009907206384693584652377753448639527
modulus = bigintcs:000002-1bd189-52959f-874f79-3d6cf5-11ac82-e6cea4-46c19c-5f523a-5318c7-e0f379-66f9e1-308c61-2d8d0b-dba253-6f54b0-ec6c27-3198DB

Share (x:1) = 5029843236858503316023507982074352651731277871
Share (x:2) = 11756782766980296102287198955234016324659726464
Share (x:3) = 22063175333516895391366046995050655800780587367
Share (x:4) = 35949020936468301183260052101524271080093860580
Share (x:5) = 53414319575834513477969214274654862162599546103
Share (x:6) = 74459071251615532275493533514442429048297643936
Share (x:1) = bigintcs:0000e1-8bc4c6-aff0f0-47d7e4-9c63f3-dceb5a-05182f-086F30
Share (x:2) = bigintcs:00020f-313f50-ea5144-91be7f-eb359a-28850f-c84880-A92812
Share (x:3) = bigintcs:0003dd-58d4ea-149a52-511936-40e438-513093-c30167-AC7697
Share (x:4) = bigintcs:00064c-028592-2ecc19-85e807-9d6fce-56ede5-f542e4-391126
Share (x:5) = bigintcs:00095b-2e5149-38e69a-302af4-00d85c-39bd06-5f0cf7-80574A
Share (x:6) = bigintcs:000d0a-dc380f-32e9d4-4fe1fb-6b1de1-f99df5-005fa0-4C058A

  # Give the keys x:1, x:2, x:3, x:4, x:5 and x:6 away.
  # Give the file PAYLOAD.enc away, or publish it somewhere public.

  #  time passes 

  # Later, somebody acquires 3 shares - for example, x:2, x:4 and x:5 -
  #  and then does this:
 
$ java -jar secretshare.jar combine -k 3 \
   -s2 11756782766980296102287198955234016324659726464 \
   -s4 35949020936468301183260052101524271080093860580 \
   -s5 53414319575834513477969214274654862162599546103
Secret Share version 1.4.4
secret.number = '1882356743151517032574974075571664781995241588'
secret.string = 'TheKeyUsedToEncrypt'

  # Then, using PAYLOAD.enc and the secret.string, recovers the secret PAYLOAD:

$ openssl enc -d -pass pass:TheKeyUsedToEncrypt -aes-256-cbc \
              -in PAYLOAD.enc -out RECOVER.txt
$ cmp PAYLOAD.txt RECOVER.txt 
$ cat RECOVER.txt
   This is the PAYLOAD
   Generated Wed May  9 19:58:01 CDT 2018
   It has spaces at the front of every line
```




Note on the Modulus
-----
Using a shared modulus is ok - the modulus is NOT secret.
You can use a randomly generated prime modulus if you'd like.
It just takes longer.

```
Timing difference:                             Time To Generate Split
a) using -prime4096 on 512-character secret    ==    0.3 seconds
b) using -primeN    on 512-character secret    ==   57.1 seconds
```

Note on the size of 'k'
----

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

ORIGINAL SOLVER (versions 1.4.1 and earlier)
Value 'k' versus recorded runtimes to complete the "combine" operation:

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


Architecture Documentation
----
See [Architecture.md](docs/Architecture.md)


Documentation
----
[Original Sourceforge Secret Sharing in Java] - original SCM location.  Out-of-date.

[Resources] - more links to useful Shamir Secret Share documentation and projects


[Original Sourceforge Secret Sharing in Java]:http://secretsharejava.sourceforge.net/
[Resources]:extrastuff/resources.md
[SecretShare1.4.1]:http://mvnrepository.com/artifact/com.tiemens/secretshare/1.4.1
[SecretShare1.4.2]:http://mvnrepository.com/artifact/com.tiemens/secretshare/1.4.2
[SecretShare1.4.3]:http://mvnrepository.com/artifact/com.tiemens/secretshare/1.4.3
[SecretShare1.4.4]:http://mvnrepository.com/artifact/com.tiemens/secretshare/1.4.4
[SecretShare1.4.4.alt]:https://search.maven.org/#artifactdetails%7Ccom.tiemens%7Csecretshare%7C1.4.4%7Cjar
