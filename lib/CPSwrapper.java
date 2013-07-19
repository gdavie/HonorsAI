// CPSwrapper.java
/**
 			Aaron Scoble
 			Victoria University of Wellington 20/01/13
This wrapper program will be passed two arrays, one of which will be the list of frequencies,
and the other of which will represent the model.

Currently the model and the aperture size are hard-coded, they will be passed in from the main
AI system. The output is simply dumped to screen for now, although we will return that eventually.

Probable design will include a class that will represent the model, as well as a class that
will represent the return. This will allow this wrapper to act as a single callable function.
**/


import java.io.*;
import cern.jet.math.*;

class CPSwrapper {
    final static int MAXSIZE = 4090;

    private native void runCPS(double frq[],double mdl[]);
    private static native double[] getFreq();
    private static native double[] getC();
    private static native double[] getEllip();

    public static void main(String args[]) {
   //     System.out.println("-- We are in the Java program JavaCode --");
        CPSwrapper c = new CPSwrapper();
        double frq[] = new double[MAXSIZE];
        //Model file, eventually passed into CPSwrapper from PLGP execution
        double mdl[] = new double[5];
             mdl[0]=0.0865;
             mdl[1]=0.2975;
             mdl[2]=0.0000;
             mdl[3]=4.8600;
             mdl[4]=-9999; //terminaltion value
         double theory;
         double r = 30;// side of triangle in metres.
        //Frequency file, read once per whole PLGP program execution.
        //This will be placed before generation 0 and will not be repeated.
        // frq can then be passed around throughout the whole PLGP execution
        try{
        FileReader fr = new FileReader("coh30m.dat");
        BufferedReader br = new BufferedReader(fr);
        String s;
        int counter=0;
        while((s = br.readLine()) != null) {
        	String [] tokens = s.split(" +");
        	frq[counter]=Double.parseDouble(tokens[1]);
        	counter++;
        }
        fr.close();
        }catch(Exception e){
        	//swallow
        }

        //Check the execution time for CPS
        long startTime = System.nanoTime();
        //Perform the calculations and store resultant arrays in memory
        c.runCPS(frq, mdl);
        //Show execution time
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
//	System.out.println("CPS time "+duration/ 1000000000.0);

       double  freq[] = new double[MAXSIZE];
       double  cc[] = new double[MAXSIZE];
       double  ellip[] = new double[MAXSIZE];

       //Retrieve the three columns.
       //If the arrays are strictly aligned, (which they aren't just yet),
       //TODO remove all entries where freq <=0
       //The freq ahould be identical to the frq array.
       //That means we only need c at this point.
       //ellip will be useful when we start to look at HVSR
       freq=c.getFreq();
       cc=c.getC();
       ellip=c.getEllip();

        System.out.println("Freq, Coherency");
  //  System.out.println("Print contents of array");
    for (int i=0; i<MAXSIZE; i++){
    	    if(freq[i]!=0.0){//TODO invalidate this test
    	    	double _c=cc[i]*1000;
    	    	//This is the Bessel function calculation
    	    	theory = cern.jet.math.Bessel.j0(2.0*Math.PI*freq[i]*r/_c) ;
    	    	      //System.out.format("%-10.5f %-10.5f %-10.5f%n",freq[i], cc[i], ellip[i]);
    	    	      //System.out.println(freq[i]+","+cc[i]);
    	    	      System.out.println(freq[i]+","+theory);
    	    }
    }
  }

    static {
        // Call up the static library libCPS.so created from CPSinterface.c
	String cwd = System.getProperty("user.dir");
	//TODO this locates and loads libCPS. We will most likely want to change this.
	//perhaps an entry in the parameter file would do the trick.
        System.load(cwd+"/lib/libCPS.so");
    }
}
