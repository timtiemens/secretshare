Shamir's Secret Share in Java
==============================
Java implementation of Shamir's Secret Sharing algorithm 
as described in Applied Cryptography [as LaGrange Interpolating Polynomial Scheme].


Dependencies
-------------
The following are required to run the application in secretshare.jar:
 1. jre 1.6+
 

The following are required to compile the project:
 1.  jdk 1.6+
 2.  gradle 1.10+
 3.  miglayout 3.7.4+


The following are required to completely build and test the project:
 1.  JUnit 4.x
This will allow the (Unit and Integration) Test .java files to compile.
 
 
Installation
------
1. Compile locally or use artifact.

  a. Compile locally - build the project with gradle
```
    $ gradle build
  [creates build/dist/lib/secretshare.jar]
    $ cp build/libs/secretshare-1.3.0.jar ./secretshare.jar
  [copies the .jar into the current directory]
```
  b. Use artifact - [TODO: push .jar artifact to maven central, document groupId, etc. here]


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

  c. Create a share size 6 with threshold 3 as above, but with a long secret string.  Note: no modulus was given, so a pre-defined 384-bit prime was used as the modulus.  384 bits allows 48 characters of secret string.
  ```
  $ java -jar secretshare.jar split -k 3 -n 6 -sS "The Cat In The Hat"
  ```
  
  d.  Create the same share as above, then pipes the output of "split" into the input of "combine", which prints out the secret string.
  ```
  $ java -jar secretshare.jar split -k 3 -n 6 -sS "The Cat In The Hat" \
   | java -jar secretshare.jar combine -stdin
  ```

  e.  Create the same share as above, but use a pre-defined 4096-bit prime modulus.  4096 bits allows 512 characters of secret string.
  ```
  $ java -jar secretshare.jar split -k 3 -n 6 -sS "The Cat In The Hat 4096bits" \
  -prime4096
  ```



Note on the Secret
-----
From above, you can see the largest pre-defined prime modulus is 4096 bits, which only allows 512 characters of secret.
In case it isn't obvious, the best way to use the shared secret is to use it as the key/pass-phrase for a symmetric encryption.
See gpg -c (aka gpg --symmetric) for an example of symmetric encryption.


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

Documentation
----
[Original Sourceforge Secret Sharing in Java] - original SCM location.  Out-of-date.

[Resources] - more links to useful Shamir Secret Share documentation and projects


[Original Sourceforge Secret Sharing in Java]:http://secretsharejava.sourceforge.net/
[Resources]:extrastuff/resources.md

