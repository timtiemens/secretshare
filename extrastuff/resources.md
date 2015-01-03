Resources


Solvers
=========
[Solving A System of Linear Equations]

This is an independent confirmation that secretsharejava is doing the math correctly.  The basic idea is to split the secret into numbers, then use a different linear equation solver implementation to reconstruct the original secret.


Run the split:
```
$ java -jar secretshare.jar split -k 3 -n 6 -sN 44652
Share (x:1) = 111441
Share (x:2) = 242682
Share (x:3) = 438375
Share (x:4) = 698520
Share (x:5) = 1023117
Share (x:6) = 1412166
```

We choose x:1, x:2 and x:3 secrets, which correspond to the equations
111411 = a*x^0 + b*x^1 + c*x^2 = a + b + c
242682 = a*x^0 + b*x^1 + c*x^2 = a + 2b + 4c
438375 = a*x^0 + b*x^1 + c*x^2 = a + 3b + 9c

Then, in the linear equation solver, we are going to solve for what we call 'a', 'b', and 'c'.
So we enter "number of equations m = 3", and "number of unknowns, n = 3".  In the grid we enter:
```
1 1 1 111441
1 2 4 242682
1 3 9 438375
```
And when we push submit, we get the (single) solution:
```
X1 = 44652    X2 = 34563     X3 = 32226
```
Which means the original equation that "split" used was:
```
S = 44652 + 34563x + 32226x^2
```
To confirm our equation, we plug in "1", "2" and "3" for "x", and we get f(1)=111441, f(2)=242682 and f(3)=438375, thus confirming we have the correct coefficients for the equation.

To get back our original secret, we compute f(0), which gives us 44652, and is confirmed.

So - if you can find a linear-equation solver that can deal with huge values for the coefficients (e.g. ~ 2^4100), you can reconstruct the secret from your shares without using secretshare.jar :-).   

N.B.: Technically, for large values, you need a solver that can perform operations "modulus PRIME" - i.e. modulo the prime number.  We didn't run into that above, because the default prime is huge compared to the secret and the generated coefficients. 



Other implementations
=========

Clojure
-------

[Shamir Secret Sharing for Clojure based on secretsharejava] 

Wonderful application in Clojure.
Currently based on version 1.3.1 of this library.

Java
----

[Shamir Secret Sharing in Java]

The original location for this project.  Out of date.  Uses ant instead of gradle.  Remember the good 'ole days when Sourceforge choked when the project had a quote-mark in the title?  That is why it isn't called "Shamir's Secret Sharing in Java".

Perl
----

[Implementation of Shamir's method for sharing a secret] 

Small, easy to use. "Algorithm is applied to the key one byte at a time", which means as far as cryptography is concerned, it should not be trusted. It also means it is not compatible with my version.

Adobe Flex/Flash
-----------------

[Shamir's "How to share a Secret"]

No source available [which means it should not be trusted]. Simple "share format" of NNkkFFhhhhhhhhhhh (example: 01033ED38FFE2E2F57CDE8BB) where NN is the "x"/01, kk is the "k"/03, FF are useless flags (3E in the example), and the rest is hex encoding of the characters in this secret share.

C
----------

[Shamir's Secret Sharing Scheme] aka 'ssss'

Note: this program "hangs" when there is not enough entropy (a common situation when run in a virtual machine).
To increase the amount of entropy available:
```
$ sudo rngd -r /dev/urandom -o /dev/random
```

To check the amount available:
```
$ cat /proc/sys/kernel/random/entropy_avail
```

Uses a "share format" of XX-hhhhhhhhhh where "X" is the base-10 value of "x", and and "hhhhh" is the base-16 value of your share of the secret ("obviously").
The "k" value is given on the command line of ssss-combine, and is also base-10.
For better compatibility, we must use '-x' (hex input), since the "String" mode in ssss is ASCII, but the "String" mode for secretshare.jar is UTF-8.
We also disable the "diffusion" mode, since that isn't explained anywhere.

There is an error in the Makefile (the -lgmp must be on the end of the command line).
Run 'make', and you get ssss-split and ssss-combine applications.

```
$ ./ssss-split -t 3 -n 5 -x -D
Secret As Hex String: AE6C
01-ffe0
02-575d
03-06d7
04-34d2
05-654a
```

Converting to base-10, that gives us

```
$ java -jar secretshare.jar combine -k 3 \
 -s1 65504 \
 -s2 22365 \
 -s3 1751
```
Which prints "secret.number = '131168'", which is too bad, because the input secret was 0xAE6C, which is 44652, which means something went wrong.  Plugging the values into the linear equation solver confirms '131168' as the secret, however, the x2 = -76926.5 and x3 = 11262.5, i.e. it has fractional values for the coefficients, something that seems really wrong [but I have no evidence, beyond the fact that secretsharejava will never generate fractional coefficient values].

So - whatever ssss is doing, it is internally consistent, but don't expect to use a 3rd-party to combine your shares.
And since it is not confirmed, you have little reason to believe that the application is actually working (working = always gives you the correct answer and missing just 1 share means that all answers are possible.) 
In particular, it is really scary that the linear equation solver does not work with the values produced by ssss.  That means ssss is using some kind of "optimized" equation, that somehow lost sight of the Lagrange interpolation algorithm it purports to implement.
That, plus the fact that the ssss implementation is impossible to understand [i.e. impossible to map back into simple polynomial equations], means that I wouldn't trust it.  In particular, the implementation has this:
```
void field_add(z, x, y) {
  mpz_xor(z, x, y);
}
```
In other words, it thinks that "XOR" is the same operation as "ADD".  Which, maybe it is given other constraints in the implementation. 

But that just puts the whole package into a well-known category of mine:

    Computes the wrong answer blazingly fast(tm)





Information
=========

Built-in Modulus (all are primes)
---------------
 192 bits = 14976407493557531125525728362448106789840013430353915016137
 
 384 bits = 83085671664126938805092614721037843700776366159998897420433674117190444262260240009907206384693584652377753448639527
 
4096 bits = 16710222102610440107068043371465990121279427984758140486147735732543262527544919309581228990959960933454241707431028205407801175010972697716211777405621844447135311624699359973445785442150139493030849120189695139622021101430363403930757354949513385879948926539292859265140544779841897745831487644537568464106991023630108604575150490083044175049593271254925175508848427143088944400255558397883427448667101368958164663781091806630951947745404989962231943601603024661584134672986801498693341608816527553412312812319737861910590928243420749213395009469338508019541095885541890008803615972806597516557801530791875113872380904094611929773211709366081401737953645348323163171237010704282848106803127761278746182709924566001996544238514546167359724648214393784828708337709298145449348366148476664877596527269176552273043572304982318495803088033967414331004526063175049856118607130798717168809146278034477061142090096734446658190827333485703051687166399550428503452215571581604276048958396735937452791507228393997083495197879290548002853265127569910930648812921091549545147941972750158605112325079312039054825870573986374161254590876872367709717423642369650017374448020838615475035626771463864178105646732507808534977443900875333446450467047221

Same as above, but in the Big Integer Checksum format:
 
 192 bits = bigintcs:000002-62c8fd-6ec81b-3c0584-136789-80ad34-9269af-da237f-8ff3c9-12BCCD

 384 bits = bigintcs:000002-1bd189-52959f-874f79-3d6cf5-11ac82-e6cea4-46c19c-5f523a-5318c7-e0f379-66f9e1-308c61-2d8d0b-dba253-6f54b0-ec6c27-3198DB

4096 bits = bigintcs:100000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000000-000735-4C590B

Fun note: the 4096 bit prime is 2^4100 + 0x735.  It was found by running .nextProbablePrime( 2^4100 ).  It was further tested and confirmed to be prime with 100,000 rounds of Miller-Rabin (which took 50,000 seconds, or almost 14 hours).

Package note:  you can print these modulus values with 
```
  $ java -jar secretshare.jar info
```




[Implementation of Shamir's method for sharing a secret]:http://charles.karney.info/misc/secret.html
[Shamir's "How to share a Secret"]:http://www.christophedavid.org/w/c/w.php/Calculators/ShamirSecretSharing
[Shamir's Secret Sharing Scheme]:http://point-at-infinity.org/ssss/
[Shamir Secret Sharing for Clojure based on secretsharejava]:https://github.com/pelle/secretshare
[Shamir Secret Sharing in Java]:http://sourceforge.net/projects/secretsharejava/
[Solving A System of Linear Equations]:http://www.math.odu.edu/~bogacki/cgi-bin/lat.cgi?c=sys
