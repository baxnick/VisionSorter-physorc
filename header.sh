#!/bin/bash

for f in `find src \( -name "*.java" \)`
do
   echo "`cat LICENSE $f`" > $f
done
