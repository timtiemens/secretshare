#!/bin/sh

#
# This is from SourceForge Ticket #2 - 
#    by using "Hello Secret!" and the modulus, the run FAILS
# As you can see, the modulus is .just. a bit bigger than the secret.
# If you let the system use -prime384 [the default] for the modulus, 
#   then everything works.
#
#  The secret string is 'Hello Secret!'
SECRET_NUMBER_EQUIV="5735816763073004597640754983969"
USE_MOD="-m          5735816763073004597640754984037"
#USE_MOD=" " 
N=6
K=6
java -jar build/dist/lib/secretshare.jar  split -n $N -k $K -sS 'Hello Secret!' $USE_MOD -paranoid all


