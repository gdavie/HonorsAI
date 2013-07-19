package core;

public abstract class IFitnessMeasure {

	protected double[] components;
	protected double fitness;
	
	public IFitnessMeasure(){
		components = new double[Config.getInstance().numClasses];
	}
	
	public IFitnessMeasure(IFitnessMeasure ifm){
		fitness = ifm.fitness;
		components = ifm.components.clone();//changed this
	}
	
	public abstract IFitnessMeasure clone();
	
	/** Calculates the error of a register collection of final register values in a 
     problem-specific way. Updates the internal measurement of the error to reflect this.
     fRV are the finalRegisterValues that any change to the fitness measure's error is based 
     on. fc is the fitness case which lead to the final register values being passed to it.*/
	public abstract double updateError(RegisterCollection fRV, IFitnessCase fc);
	
	//Allow class label checking from cache.
	public abstract int updateErrorHCS(RegisterCollection fRV,  int classLabel);
	public abstract void updateFitnessHCS(int fitness);
	
	/** Zeroes the fitness - i.e. sets it to perfect, assumes error is going to be added to it.*/
    public void zeroFitness() { 
    	fitness = 0;
    	for(int i = 0; i < components.length; i++){
    		components[i] = 0;
    	}
    }

    /** Returns some value representing an overall level of fitness. 0 is assumed to be perfect,
     and higher is assumed to be worse. Fitnesses less than zero probably aren't meaningful.*/
    public double overallFitness() { 
    	return fitness; 
    }
    
    /**Returns a stringified fitness measure (it should be only one line, but not prefixed with
     "//" or any other "I am a comment" indication - it should be naked text only.*/
    public abstract String toString();
    
    public double[] getComponents(){
    	return components;
    }
    
    public void copy(IFitnessMeasure ifm){
    	fitness = ifm.fitness;
    	System.arraycopy(ifm.components, 0, components, 0, components.length);
    }
    
}
