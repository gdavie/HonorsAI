package core;

import java.util.ArrayList;

/** Represents a single fitness case (e.g. case to be classified, point in a symreg) with
 features of type T.*/
public abstract class IFitnessCase {

	protected ArrayList<Double> features = new ArrayList<Double>();
	
	// Returns the value of the i'th feature.
    public double F(int i) { 
    	return features.get(i);
    }


}
