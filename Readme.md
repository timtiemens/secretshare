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
 2.  (gradlew, uses gradle 2.2.1)


The following are used to completely build and test the project:
 1.  JUnit 4.x
This is needed for the (Unit and Integration) Test .java files to compile.
 
 
Build
------

1. Compile locally - build the project with gradlew (gradle wrapper)
```
    $ ./gradlew build
  [creates build/libs/secretshare-1.4.2.jar]
    $ cp build/libs/secretshare-1.4.2.jar ./secretshare.jar
  [copies the .jar into the current directory]
```

Officially Released Artifact
------

2. Use the artifact in your build - dependency information:
```
      group:   com.tiemens
       name:   secretshare
    version:   1.4.2
```
Central Repository - [SecretShare1.4.2] - to see dependency information
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
  
  d.  Create the same share as above, then pipes the output of "split" into the input of "combine", which prints out the secret string.
  ```
  $ java -jar secretshare.jar split -k 3 -n 6 -sS "The Cat In The Hat" \
   | java -jar secretshare.jar combine -stdin
  ```

  e.  Create the same share as above, but use a pre-defined 4096-bit prime modulus.  The 4096 bit prime allows 512 characters of secret string.
  ```
  $ java -jar secretshare.jar split -k 3 -n 6 -sS "The Cat In The Hat 4096bits" \
  -prime4096
  ```

  f.  Create the same share as above, but output in a manner better suited for splitting up the shares in order to give them out individually with all required information.
  ```
  $ java -jar secretshare.jar split -k 3 -n 6 -sS "The Cat In The Hat 4096bits" \
  -prime4096 -printIndiv
  ```

  g.  Combine 3 shares to recreate the original secret.
  ```
  $ java -jar secretshare.jar combine -k 3 \
     -prime384 \
      -s2 1882356874773438980155973947620693982153929916 \
      -s4 1882357204724127580025723830249209987221192644 \
      -s5 1882357444072759374568880025530775541595539408
  ```

Important Notes about Shares of the Secret
-----
Note that each share of the secret requires at least these pieces:
 1. the "k" value [same for all shares],
 2. the "x" value     [unique for this share],
 3. the "share" value [unique for this share]

Optional -  the "modulus" value [same for all shares]
  It is still unclear under what circumstances the modulus is required.
  If no calculation in the split was larger than the modulus, then it is never required.
  TODO: create an explicit test where modulus was triggered, confirm split-combine still work.

Optional - the UUID of the split [same for all shares]
  If you have split multiple secrets into shares,
it is also nice to have the UUID of the split operation,
so that you can make sure all your shares belong to the same split.
Due to the nature of the algorithm, shares from different splits
will 'combine' and will produce a ''secret'' (string or number),
but it will not be the original secret.


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
   Generated Mon Dec 29 19:39:01 CST 2014
   It has spaces at the front of every line.
$ openssl enc -pass pass:TheKeyUsedToEncrypt -aes-256-cbc -salt \
              -in PAYLOAD.txt -out PAYLOAD.enc
$ ls -l PAYLOAD.enc
-rw-r--r--  1 timtiemens  users   128 Dec 29 19:40 PAYLOAD.enc

  # Create the shares from the key.
  # Use '-printIndiv' to make it easier to distribute the shares.
$ PRINT=-printIndiv
  # For this example, we'll print them all together
$ PRINT=-printOne
$ java -jar secretshare.jar split -k 3 -n 6 -sS "TheKeyUsedToEncrypt" $PRINT
Secret Share version 1.4.2
Date                          : 2014-12-29 16:59:00
UUID                          : 363e3f28-f43f-4c45-9fa7-4360b7e22cba
n = 6
k = 3
modulus = 83085671664126938805092614721037843700776366159998897420433674117190444262260240009907206384693584652377753448639527
modulus = bigintcs:000002-1bd189-52959f-874f79-3d6cf5-11ac82-e6cea4-46c19c-5f523a-5318c7-e0f379-66f9e1-308c61-2d8d0b-dba253-6f54b0-ec6c27-3198DB

Share (x:1) = 1882356784171382174829380260273743531461013952
Share (x:2) = 1882356874773438980155973947620693982153929916
Share (x:3) = 1882357014957687448554755137612516134073989480
Share (x:4) = 1882357204724127580025723830249209987221192644
Share (x:5) = 1882357444072759374568880025530775541595539408
Share (x:6) = 1882357733003582832184223723457212797197029772
Share (x:1) = bigintcs:000054-68656a-419cd4-9f11fa-b4fbc5-3598a4-5b71c0-2A05DC
Share (x:2) = bigintcs:000054-6865ae-6aef1d-5cdc3d-a2b3a1-e92ad8-8df4bc-F57505
Share (x:3) = bigintcs:000054-686617-e1702f-acc42d-1d96db-891a0f-10f968-26DB1B
Share (x:4) = bigintcs:000054-6866a6-a5200b-8ec9c9-25a572-156647-e47fc4-3AF50D
Share (x:5) = bigintcs:000054-68675a-b5feb1-02ed11-badf65-8e0f83-0887d0-6B58F1
Share (x:6) = bigintcs:000054-686834-140c20-092e06-dd44b5-f315c0-7d118c-9D944F

  # Give the keys x:1, x:2, x:3, x:4, x:5 and x:6 away.
  # Give the file PAYLOAD.enc away, or publish it somewhere public.

  #  time passes 

  # Later, somebody acquires 3 shares - for example, x:2, x:4 and x:5 -
  #  and then does this:
 
$ java -jar secretshare.jar combine -k 3 \
   -s2 1882356874773438980155973947620693982153929916 \
   -s4 1882357204724127580025723830249209987221192644 \
   -s5 1882357444072759374568880025530775541595539408
Secret Share version 1.4.2
secret.number = '1882356743151517032574974075571664781995241588'
secret.string = 'TheKeyUsedToEncrypt'

  # Then, using PAYLOAD.enc and the secret.string, recovers the secret PAYLOAD:

$ openssl enc -d -pass pass:TheKeyUsedToEncrypt -aes-256-cbc \
              -in PAYLOAD.enc -out RECOVER.txt
$ cmp PAYLOAD.txt RECOVER.txt 
$ cat RECOVER.txt 
   This is the PAYLOAD
   Generated Mon Dec 29 19:39:01 CST 2014
   It has spaces at the front of every line.
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
k = 22      0 seconds
k = 23      0 seconds
k = 24      0 seconds
k = 25
k = 26
k = 27
k = 28
k = 29
k = 30      0.44 seconds
k = 50      2 seconds
k = 75      5 seconds
k = 95     12 seconds
k = 130    47 seconds
k = 180   180 seconds
k = 230   623 seconds
k = 280  1662 seconds  28 minutes
k = 650                                                        1 year
k = 1000  346276986880 seconds ....                       11,000 years
The formula is roughly "4 times longer for each +50 in k".
From k=280 on down, all times are calculated.  k=280 is the last "measured".
So, k=280 is a pretty good practical limit, or k=95 for "immediate" results

N.B.: Earlier versions of secretshare (1.4.1 and earlier) used a very
        inefficient solving algorithm.  For these versions, your "k" is
        in effect limited to less than k=30, and is probably more like k=20.

ORIGINAL SOLVER (versions 1.4.1 and earlier)
Value 'k' versus recorded runtimes to complete the "combine" operation:
k = 19      3 seconds
k = 20     10 seconds
k = 21     39 seconds
k = 22    156 seconds
k = 23    646 seconds
k = 24   2460 seconds  41 minutes
k = 25                164 minutes
k = 26                656 minutes   11 hours
k = 27                              44 hours
k = 28                             176 hours   7.3 days
k = 29                                        30   days
k = 30                                       120   days
The formula is roughly 10 * 4^(k - 20) seconds.
From k=25 on down, all times are calculated.  k=24 is the last "measured".
For k = 90, that works out to be  = 10 * 4^(90 - 20) = 1E42 seconds.
Since a year has ~3E7 seconds, that will never happen.
So, k=25 is a pretty good practical limit, or k=20 for "immediate" results.


Documentation
----
[Original Sourceforge Secret Sharing in Java] - original SCM location.  Out-of-date.

[Resources] - more links to useful Shamir Secret Share documentation and projects


[Original Sourceforge Secret Sharing in Java]:http://secretsharejava.sourceforge.net/
[Resources]:extrastuff/resources.md
[SecretShare1.4.1]:http://mvnrepository.com/artifact/com.tiemens/secretshare/1.4.1
[SecretShare1.4.2]:http://mvnrepository.com/artifact/com.tiemens/secretshare/1.4.2
