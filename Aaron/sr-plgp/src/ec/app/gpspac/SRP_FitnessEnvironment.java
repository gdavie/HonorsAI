package ec.app.gpspac;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;


/**This class stores the memory environment, fitness cases and have methods for evaluating
 programs. T is the type of the features and registers for this problem. This class manages
 the memory of the fitness cases added to it.*/
public class SRP_FitnessEnvironment{

	private ArrayList<Double> lstFreq = new ArrayList<Double>();
	private ArrayList<Double> lstCoh = new ArrayList<Double>();
	private ArrayList<Double> trimmedFreq = new ArrayList<Double>();
	private ArrayList<Double> trimmedCoh = new ArrayList<Double>();
//	private double[] freq;
//	boolean useThreshold;
//	int zThreshold;

	public SRP_FitnessEnvironment(){
	}

    public ArrayList<Double> getLstFreq() {
		return lstFreq;
	}

	public ArrayList<Double> getLstCoh() {
		return lstCoh;
	}

    /** Returns the number of cases in this fitness environment:*/
    public int numberOfPoints() {
    	return lstFreq.size();
    }

    /** Assumes the file is 4 columns with no header.
     * we only use the first two columns*/
    public void addCasesFromFile(String filePath){

    	Scanner scan;
		try {
			scan = new Scanner(new File(filePath));
	    	while(scan.hasNext()){
	    		String s = scan.nextLine();
	    		double freq = scan.nextDouble();
	    		double coh = scan.nextDouble();
	    		scan.nextDouble(); //drop column 3
	    		scan.nextDouble(); //drop column 4
	    	    /** Adds the specified case to the FitnessEnvironment - these get passed to fitness measures
	    	     when a program's error is being updated, so that is where they should probably be checked
	    	     as being the correct type. An instance of this class will manage the memory of the
	    	     pointers to the fitness cases.*/
	    		lstFreq.add(freq);
	    		lstCoh.add(coh);
//	    		System.out.println(freq + " "+coh);
	    	}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//if (useThreshold)components();
    }

    /**
     * We need to divide the curve into components.
     * The main motivation for doing so is that the zero-crossing points
     * are the most reliable points in the curve, so we should be able to match
     * those without error.
     *
     * The secondary reason is that we will wish to consider only a
     * number of components rather than all of them.
     *
     *  To begin with, only the first two components will be considered..
     *  that relates to the low frequencies and the first trough, ending
     *  at the second zero crossing point.
     *
     *  A zero crossing point will be found by the polarity of the curve changing
     *  from positive to negative, or vice-versa.
     *  That approach doesn't work, as we want the trend of the plot, not every single point.
     *  There is much oscillation that ruins this apporach.
     *
     *  We will need to define which of these points becomes the index for the crossing point.
     *  We could perhaps use interpolation to provide a theoretical crossing point,
     *  but for now we will simply use the point at which the polarity is known to have changed.
     *
     *  Of note here, is that the data points are listed from highest frequency to lowest.
     */
    private void components(){
    	System.out.println("Components***********-----------------");
    	//polarity :: T is positive, F is negative.
    	boolean polarity = true;//Always(?) starts positive
    	boolean thisPol=true;
    	boolean trend = true;
 //   	boolean changeSign;
 //   	boolean zeroPoint;
    	int count =0;
    	ArrayList<Integer> crossings = new ArrayList<Integer>();
    	for(int i=0; i<lstFreq.size(); i++){
   // 		zeroPoint=false;
    		//polarity is TRUE if coherency is non-negative (POSITIVE)
    		thisPol=(lstCoh.get(i)>0);
    		//Set initial polarity and trend to first data point
    		if(i==0){
    			polarity = thisPol;
    			trend = polarity;
    		}
    		//This is a change of sign if thisPol and polarity are different
    		if(thisPol!=polarity){
    			//Start the threshold count
    			count = 0;
    			//changeSign=true;
    		}else{
    			//Same sign as last point, increment count
    			count ++;
    		}
    		polarity=thisPol;
    		/**if(count>zThreshold){
    			//If the trend has changed, add point, else we are continuing trend so there is no crossing
    			//Add to the list of crossings, the point where sign changed.
    			//Now we have passed threshold but add the original point
    			if(!trend==thisPol){
        			crossings.add(i-count);
        			//Confirm the trend of the polarity now we have passed threshold
        			trend=thisPol;
    			}

    			count = 0;
    		}*/
    	}
    	//Now go through the first three crossing points and show them
    	Collections.reverse(crossings);
    	//create the trimmed lists
    	for(int i= crossings.get(2); i< lstFreq.size(); i++){
    		trimmedFreq.add(lstFreq.get(i));
    		trimmedCoh.add(lstCoh.get(i));
    	}
 //   	System.out.println("END=====================");
    	//Now assign these to the lists we're using
    	lstFreq=trimmedFreq;
    	lstCoh=trimmedCoh;
    }

    /** Returns a string representation of this FitnessEnvironment, i.e. the value of the
     features for the current fitness case and the current values of the registers. Does
     not place a trailing new line after the string representation, even though its multiline.*/
    public String toString(){
    	 String buffer = "";
    	  for(int i = 0; i < size(); ++i) {
    	    buffer += lstFreq.get(i) + " "+ lstCoh.get(i)+ "\n";
    	  }
    	  buffer += "\n";
    	  buffer += "Number of cases loaded: " + size();

    	  return buffer;
    }

    public int size(){
    	return lstFreq.size();
    }
    /**
     *
     * @param freq the frequencies from libCPS. We use this to drop 'zero' rows.
     * @param cc calculated coherencies
     * @return
     */
    public double calculateFitnessRMSE(double[] freq, double[] cc, double[] ellip){
    	double sum=0.0;
    	double theoryFreq=0.0;
    	double theory =0.0;
    	double target=0.0;
    	double error=0.0;
    	double weight = 0.02;//This should be a config param
    	int n=0;
    	int ff=2;
    	for(int i=0; i< freq.length; i++){
    		theoryFreq = freq[i];
    		double denominator = theoryFreq*theoryFreq;
    		denominator=weight*theoryFreq;
    		//TODO limited fitness function to under 20.
    		if(theoryFreq>0&&theoryFreq<20){
    //		if(theoryFreq>0){
    			theory=cc[i];
    			target=lstCoh.get(i);
    			switch(ff){
    			case 0:
    				error = (Math.abs(theory-target))/denominator;
    				break;
    			case 1:
    				error=(theory-target)*(theory-target);
    				break;
    			case 2:
    				error = (Math.abs(theory-target))/denominator;
    				break;
    			case 3:
    				error = ((theory-target)*(theory-target))/denominator;
    				break;
    			default:
    				System.out.println("Need to define FF");
    				System.exit(1);
    			}
    			sum+=error;
    			//This fitness measure will reward lower frequencies with a higher sum
    			//dividing by freq^2 will add a much smaller amount to the sum with higher freq.
 //   			sum = sum + (Math.abs(theory-target))/theoryFreq/theoryFreq;
    		}
    		n++;
    	}
    	//Get mean of the error
		if(ff>0){
			sum = sum/n;
		}
    	//Get root of the error
    	return Math.sqrt(sum);
    }

    //Don't know if this is the best way to do this, may be better to simply
    //add as an array in the first place -- may depend on how the error checking RMSE
    // fitness part will work.
    public double[] getFreq(){
    	int sz = lstFreq.size();
    	double[] retval = new double[sz];
    	for (int i=0; i<sz; i++){
    		retval[i]=lstFreq.get(i);
    	}
    	return retval;
    }


}
