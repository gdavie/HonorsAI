#####################		AZVER			#############################################
#####

#####
#	LINUX Compilers
#####
FCMP=gfortran -w -frecord-marker=4 -O 
FCMP77=gfortran -w -frecord-marker=4 -O -x f77 -fPIC

ASUBS=bsort.o sdispSub.o sregnSub.o model.o


DEST=../bin
	
model.o:
	$(FCMP77) -c model.f77
	
bsort.o:
	$(FCMP77) -c bsort.f77

sdispSub.o: 
	$(FCMP77) -c sdispSub.f77
	
sregnSub.o:
	$(FCMP77) -c sregnSub.f77

CPS.o:
	$(FCMP77) -c CPS.f77
	
cleanall: clean CPS

CPS: CPS.o $(ASUBS)  
	$(FCMP) CPS.o $(ASUBS)  -o CPS 
	cp CPS /home/az/Working_share/Test/
	mv CPS $(DEST)
	
clean:
	rm -f *.o
	rm -rf ../bin/*

