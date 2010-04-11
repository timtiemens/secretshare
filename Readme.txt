Shamir Secret Share in Java
===========================

1. Dependencies
The following are required to run the application in secretshare.jar:
 a - jre 1.5+
 
 The following are required to compile the project:
 a - jdk 1.6+
 b - ant 1.7+

 The following are required to completely compile the project:
 c - JUnit 3.8
     This will allow the Unit Test .java files to compile.
 
 
2. Installation
 a - copy 'secretshare.jar' into your directory
     or
 b - build the project with ant
     $ ant
       [creates build/dist/lib/secretshare.jar]
     $ cp build/dist/lib/secretshare.jar .
       [copy the .jar into the current directory]

3. Use
   There are two main ways to use the application: split and combine.
   Split takes a secret (number or string) and splits it into shares
   Combine takes 'k' shares and re-creates the secret (number or string)

   split    -- run 'java -jar secretshare.jar split'
               to display usage

   combine  -- run 'java -jar secretshare.jar combine'
               to display usage

4. Example command line

  $ java -jar secretshare.jar split -k 3 -n 6 -m 59561 -sS "Cat"
    [creates a share size 6 with threshold 3 with "Cat" as the secret string.
     Note: the low modulus of 59561 limits the length of the secret string]

  $ java -jar secretshare.jar split -k 3 -n 6 -m 59561 -sS "Cat" \
       | java -jar build/dist/lib/secretshare.jar combine -stdin
    [runs the same command as above, but pipes that output into the 'combine'
     program, which then re-creates the secret and the secret string "Cat".]
    
  $ java -jar secretshare.jar split -k 3 -n 6 \
        -sS "The Cat In The Hat" 
    [creates a share size 6 with threshold 3 with the secret string.
     Note: no modulus was given, so a pre-defined 384-bit prime was used,
          which allows 48 characters of secret string.]

  $ java -jar secretshare.jar split -k 3 -n 6 \
        -sS "The Cat In The Hat" | \ 
        java -jar build/dist/lib/secretshare.jar combine -stdin
    [creates the same share as above, then pipes the output of 'split'
     into 'combine', and prints out the secret string]

    
TODO:
 
 Header
 $Id: $
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 
   
