#!/bin/bash
# This is a commodity script for running the server on a unix-based
# operating system with bash. Finds all jars in the ./lib/ directory 
# and adds them to $cp, which is then used as classpath for the server.
# Version 1.0, 22.03.2006 Andreas Ravnestad

# Initial classpath value
cp=".:./bin"

# Find all files that ends with .jar in the ./lib dir
for jar in ./lib/*.jar 
do 
   cp=$cp":"$jar; 
done

# Run the server
java -cp $cp sleepy.Main
