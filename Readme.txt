Shamir Secret Share in Java
===========================

1. Dependencies
The following are required to run the application in secretshare.jar:
 a - jre 1.5+
 
 The following are required to compile the project:
 a - jdk 1.6+
 b - gradle 1.10+
 c - miglayout 3.7.4+

 The following are required to completely build and test the project:
 c - JUnit 4.x
     This will allow the Unit Test .java files to compile.
 
 
2. Installation
 a - build the project with gradle
     $ gradle build
       [creates build/dist/lib/secretshare.jar]
     $ cp build/libs/secretshare-1.2.2.jar ./secretshare.jar
       [copy the .jar into the current directory]
 b - [TODO: push .jar artifact to maven central]

3. Use
   There are two main ways to use the application: split and combine.
   Split takes a secret (number or string) and splits it into 'n'shares.
   Combine takes 'k' of 'n' shares and re-creates the secret (number or string).

   split    -- run 'java -jar secretshare.jar split'
               to display usage

   combine  -- run 'java -jar secretshare.jar combine'
               to display usage

4. Example command line

  $ java -jar secretshare.jar split -k 3 -n 6 -m 16639793 -sS "Cat"
    [creates a share size 6 with threshold 3 with "Cat" as the secret string.
     Note: the low modulus of 16639793 limits the size of the secret number,
           which in turn limits the length of the secret string.]

  $ java -jar secretshare.jar split -k 3 -n 6 -m 16639793 -sS "Cat" \
       | java -jar secretshare.jar combine -stdin
    [runs the same command as above, but pipes that output into the 'combine'
     program, which then re-creates the secret and the secret string "Cat".]
    
  $ java -jar secretshare.jar split -k 3 -n 6 \
        -sS "The Cat In The Hat" 
    [creates a share size 6 with threshold 3 with the secret string.
     Note: no modulus was given, so a pre-defined 384-bit prime was used,
           which allows 48 characters of secret string.]

  $ java -jar secretshare.jar split -k 3 -n 6 \
        -sS "The Cat In The Hat" | \ 
        java -jar secretshare.jar combine -stdin
    [creates the same share as above, then pipes the output of 'split'
     into 'combine', and prints out the secret string.]

  $ java -jar secretshare.jar split -k 3 -n 6 \
        -sS "The Cat In The Hat 4096bits"  -prime4096
    [creates a share size 6 with threshold 3 with the secret string.
     Note: the modulus was given as "prime4096", so the pre-defined 40964-bit prime 
           was used, which allows 512 characters of secret string.]

Note: Using a shared modulus is ok - the modulus is NOT secret.
      You can use a randomly generated prime modulus if you'd like.
      It just takes longer.
          Timing difference:                             Time To Generate Split
      a) using -prime4096 on 512-character secret    ==    0.3 seconds
      b) using -primeN    on 512-character secret    ==   57.1 seconds
