#!/bin/sh
N=6
K=3
SECRET="Cat In The Hat"

clear
echo "tiemens:\$ java -jar secretshare.jar split -k $K -n $N -sS \"$SECRET\""
java -jar build/dist/lib/secretshare.jar split -k $K -n $N -sS '$SECRET'



