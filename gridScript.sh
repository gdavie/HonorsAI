#!/bin/sh
#
# Force Bourne Shell if not Sun Grid Engine default shell (you never know!)
#
#$ -S /bin/sh
#
# I know I have a directory here so I'll use it as my initial working directory
#
#$ -wd /vol/grid-solar/sgeusers/daviegeor
#
# Mail me at the b(eginning) and e(nd) of the job
#
#$ -M george.davie@ecs.vuw.ac.nz
#$ -m be
#
# End of the setup directives
#
# Stdout from programs and shell echos will go into the file
#    gridScript.o$JOB_ID
#  so we'll put a few things in there to help us see what went on
#
echo ==UNAME==
uname -n
echo ==WHO AM I and GROUPS==
id
groups
echo ==SGE_O_WORKDIR==
echo $SGE_O_WORKDIR
echo ==/LOCAL/TMP==
ls -ltr /local/tmp/
echo ==/VOL/GRID-SOLAR==
ls -l /vol/grid-solar/sgeusers/
#
# OK, where are we starting from and what's the environment we're in
#
echo ==RUN HOME==
pwd
ls
echo ==ENV==
env
echo ==SET==
set
#
echo == WHATS IN LOCAL/TMP ON THE MACHINE WE ARE RUNNING ON ==
ls -ltra /local/tmp | tail
#
# Now let's do something useful, but first change into the job-specific directory that should
#  have been created for us
# Then copy something we already know exists into it
#
# Check we have somewhere to work now and if we don't, exit nicely.
#  We could do more to try and run here but this is just a test
#
if [ -d /local/tmp/daviegeor/$JOB_ID ]; then
        cd /local/tmp/daviegeor/$JOB_ID
else
        echo "There's no job directory to change into "
        echo "Here's LOCAL TMP "
        ls -la /local/tmp
        echo "AND LOCAL TMP daviegeor "
        ls -la /local/tmp/daviegeor
        echo "Exiting"
        exit 1
fi
#
# Now we are in the job-specific directory so
#
echo == WHATS IN LOCAL TMP daviegeor JOB_ID AT THE START==
ls -la 
#
# Copy the input file to the local directory
#
cp /vol/grid-solar/sgeusers/daviegeor/krb_tkt_flow.JPG .
echo ==WHATS THERE HAVING COPIED STUFF OVER AS INPUT==
ls -la 
# 
# Note that we need the full path to this utility, as it is not on the PATH
#
#/usr/pkg/bin/convert krb_tkt_flow.JPG krb_tkt_flow.png
#

if [ -z "$SGE_ARCH" ]; then
   echo "Can't determine SGE ARCH"
else
   if [ "$SGE_ARCH" = "lx-x86" ]; then
       JAVA_HOME="/usr/pkg/java/sun-6"
   fi
fi

if [ -z "$JAVA_HOME" ]; then
   echo "Can't define a JAVA_HOME"
else
   export JAVA_HOME
   PATH="/usr/pkg/java/bin:${JAVA_HOME}/bin:${PATH}"; export PATH

   java Hello
fi


echo ==AND NOW, HAVING DONE SOMTHING USEFUL AND CREATED SOME OUTPUT==
ls -la
#
# Now we move the output to a place to pick it up from later
#  (really should check that directory exists too, but this is just a test)
#
mkdir -p /vol/grid-solar/sgeusers/daviegeor/$JOB_ID
cp krb_tkt_flow.png  /vol/grid-solar/sgeusers/daviegeor/$JOB_ID
#
echo "Ran through OK"
