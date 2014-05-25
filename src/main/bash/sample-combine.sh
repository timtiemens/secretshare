#!/bin/sh
K=3
SHARE1="-s1 bigintcs:008da9-78eb07-2b60b7-9fb7b2-64e7c5-4BB641"
SHARE4="-s4 bigintcs:03c581-5e10da-7688d4-45bb60-65c0ac-282A84"
SHARE5="-s5 bigintcs:05a073-f209fe-eb632e-d26b42-49caf5-0265F5"


clear
echo "tiemens:\$ java -jar secretshare.jar combine -k $K \\ "
echo "        $SHARE1 \\ "
echo "        $SHARE4 \\ "
echo "        $SHARE5"
java -jar build/dist/lib/secretshare.jar combine -k $K $SHARE1 $SHARE4 $SHARE5

