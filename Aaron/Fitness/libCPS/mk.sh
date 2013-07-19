#!/bin/bash
#####################		Library Builder     #############################################
#
#	This will compile the Fortran code, and a C wrapper for it, making a library we can call
#		From our java code. We could probably tidy this final process up a little,
#		So that we have the ability to alter the library, but will otherwise simply
#		Need an API call to a library that contains the CPS suite as a single function.
#
#
##################################################################################################
#Clean and make the fortran files first --> check the makefile for details
make cleanall
#rm CPSwrapper.h
#copy wrapper source file here
cp ../src/CPSwrapper.java .

#We're insisting on java 1.6 here, no huge reason for 1.6, but had some compile inconsistencies
# that this call fixed.
javac -source 1.6 -target 1.6 CPSwrapper.java 
#
#Create the jni header file
javah -jni CPSwrapper 
#
#Cross - compile the stuff
# Uni machines, this may not work very well.
#===================
gcc -c -D_REENTRANT -fPIC -I /usr/lib/jvm/java-6-openjdk-amd64/include  -I /usr/lib/jvm/java-6-openjdk-amd64/include/linux -c CPSinterface.c 

#link the object files and create "libCPS.so".
#================================
gcc -shared CPSinterface.o model.o bsort.o sdispSub.o sregnSub.o  CPS.o -lgfortran -o libCPS.so

#Copy the library to the lib folder
cp libCPS.so /home/az/Working_share/git/sr-plgp/lib/
mv libCPS.so /home/az/workspace/Fitness/lib/
#copy the class file to bin folder.
mv CPSwrapper.class ../bin/
#Need to remove so that the original file doesn't complain
rm CPSwrapper.java
mv *.o ../bin/


