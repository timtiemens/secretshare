#!/bin/bash

grep Copyright $1 >/dev/null
if [ $? -eq 0 ]
then
#  echo "GOOD"
  exit 0
else
  echo "BADD $1"
  exit 1
fi

