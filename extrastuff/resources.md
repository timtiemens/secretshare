Resources
=========

Other implementations

Clojure
-------

[Shamir Secret Sharing for Clojure based on secretsharejava] 

Wonderful application in Clojure.  Based on this library as hosted at SourceForge.

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

No source available [which means it should not be trusted]. Simple "share format" of NNkkFFhhhhhhhhhhh (example: 01033ED38FFE2E2F57CDE8BB) where NN is the "x"/01, kk is the "k"/03, ff are useless flags/3E, and the rest is hex encoding of the characters in this secret share.

C
----------

[Shamir's Secret Sharing Scheme]

Too simple "share format" of NN-hhhhhhhhhh where NN/x is provided, but not "k"


[Implementation of Shamir's method for sharing a secret]:http://charles.karney.info/misc/secret.html
[Shamir's "How to share a Secret"]:http://www.christophedavid.org/w/c/w.php/Calculators/ShamirSecretSharing
[Shamir's Secret Sharing Scheme]:http://point-at-infinity.org/ssss/
[Shamir Secret Sharing for Clojure based on secretsharejava]:https://github.com/pelle/secretshare
[Shamir Secret Sharing in Java]:http://sourceforge.net/projects/secretsharejava/

