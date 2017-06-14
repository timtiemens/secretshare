#!/bin/bash

#
# Take 10 random numbers (less than the modulus),
#  combine 120 times (the number of unique subsets of a set size 10)
# For these 10 numbers, each "pick 3" combination results in a
#  different recovered secret.
#


java -jar secretshare.jar combine -m 16639793  -k 3 -paranoid 120,limitPrint=4,stopCombiningWhenAnyCount=3       -s1 123456 -s5 48382 -s2 32223 -s3 392933      -s4 923334 -s6 123122 -s7 939444 -s8 838333  -s9 453322 -s10 499222
