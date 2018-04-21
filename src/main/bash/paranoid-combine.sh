#!/bin/sh
K=3

# Dogs
SHARE1="-s1 5446929"
SHARE2="-s2 8221875"
SHARE3="-s3 12809805"
SHARE4="-s4 19210719"
DOGS=" $SHARE1 $SHARE2 $SHARE3  $SHARE4 "

# Cats
SHARE5="-s5 98112100"
SHARE6="-s6 134607460"
SHARE7="-s7  177021524"
SHARE8="-s8 225354292"
SHARE9="-s9 279605764"
SHAREA="-s10  339775940"
CATS=" $SHARE5 $SHARE6  $SHARE7  $SHARE8  $SHARE9  $SHAREA "

P_MAX="maxCombinationsAllowedToTest=50"
P_MAX_N="50"
P_STOP="stopCombiningWhenAnyCount=3"
P_LIMIT="limitPrint=3"

# mix-n-match the P_ arguments:
PARANOID="$P_MAX,$P_STOP,$P_LIMIT"

# or choose one of these:
PARANOID="110,stopCombiningWhenAnyCount=4,limitPrint=6"
PARANOID="110,limitPrint=4"

INPUT="  $DOGS $CATS "
INPUT=" $SHARE1 $SHARE5 $SHARE2 $SHARE3 $SHARE4 $SHARE6 $SHARE7 $SHARE8 $SHARE9 $SHAREA"

java -jar build/libs/secretshare-?.?.?.jar combine -k $K \
      -paranoid $PARANOID \
     $INPUT

#  -paranoid $PARANOID \

