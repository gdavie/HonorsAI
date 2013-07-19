package ec.app.libCPS;

import ec.app.modelling.*;
import cern.jet.math.*;

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




public class CPSwrapper {
    private static int MAXSIZE = 0;
    private boolean traceCPSflow=false;
    public static native void runCPS(double frq[],double mdl[]);
    private static native double[] getFreq();
    private static native double[] getC();
    private static native double[] getEllip();


    public static void main(String args[]) {
//    	/CPSwrapper c = new CPSwrapper();
    }

    public  FromCPS callCPS(ToCPS model){

    	//CPSwrapper c = new CPSwrapper();
    	double theory =0.0;
    	//de-encapsulate the input helper class
    	double r = model.getAperture();
    	double frq[] = model.getFrequencies();
    	double mdl[] = model.getLayerModel();
    	int halfspace = mdl.length;
    	//Add explicit halfspace
    	double omdl[] = new double[halfspace+3];
    	for(int m=0; m<halfspace;m++){
    		omdl[m]=mdl[m];
    	}

        omdl[halfspace++]=0.0000;
        omdl[halfspace++]=4.8600;
        omdl[halfspace]=-9999; //termination value

        //Call libCPS
        if(traceCPSflow)System.out.println("CPSjava-->c");
        CPSwrapper.runCPS(frq, omdl);
        //Retrieve the three columns.
        double  freq[] = CPSwrapper.getFreq();
        double  cc[] = CPSwrapper.getC();
        double  ellip[] = CPSwrapper.getEllip();

        //output arrays
        double  ofreq[] = new double[MAXSIZE];
        double  occ[] = new double[MAXSIZE];
        double  oellip[] = new double[MAXSIZE];

        int counter =0;
        /*Perform the Bessel function*/
        for (int i=0; i<MAXSIZE; i++){
    	    	double _c=cc[i]*1000;
    	    	double twoPi = 2.0*Math.PI;
    	    	theory = cern.jet.math.Bessel.j0(twoPi*freq[i]*r/_c) ;
    	    	ofreq[i]=freq[i];
    	    	occ[i]=theory;
    	    	oellip[i]=ellip[i];
    	    	/*May not need this now*/
    	    	counter=i;
        }
    	//Encapsulate the return helper class
    	FromCPS retval = new FromCPS(ofreq, occ, oellip, counter);
    	return retval;

    }

    static {
        // Call up the static library libCPS.so created from CPSinterface.c
    	String dir = System.getProperty("user.dir")+"/lib/libCPS.so";
    	System.out.println(dir);
        System.load(dir);
    }

	public static void setMAXSIZE(int mAXSIZE) {
		MAXSIZE = mAXSIZE;
	}
	public static int getMAXSIZE() {
		return MAXSIZE;
	}


}
