package core;

public class MultiClassFitnessMeasure extends IFitnessMeasure {

	/** default cctor, dtor - no members*/
	public MultiClassFitnessMeasure(){
		super();
	}
	
	public MultiClassFitnessMeasure(MultiClassFitnessMeasure mcfm){
		super(mcfm);
	}

	/** fRV are the finalRegisterValues that any change to the fitness measure's error is based 
     on. fc is the fitness case which lead to the final register values being passed to it.*/
	public double updateError(RegisterCollection fRV,  IFitnessCase fc){
		MultiClassFitnessCase mcfc;
		if( (mcfc = (core.MultiClassFitnessCase)fc) != null) {
			// then its a valid FC for multi class
			if(fRV.largestRegisterIndex() != mcfc.classNumber()) {
				fitness++;
				components[mcfc.classNumber]++;
				return 1;
			}
			return 0;
		}
		else { // wrong type of fitness case
			try {
				throw new Exception("Error: tried to pass a non-multi class fitness case to UpdateError");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}
	}
	/** fRV are the finalRegisterValues that any change to the fitness measure's error is based 
    on. classLabel is simply the int from the class labels register.*/
	public int updateErrorHCS(RegisterCollection fRV,  int classLabel){
			if(fRV.largestRegisterIndex() != classLabel) {
				fitness++;
				//components[classLabel]++;
				return 1;
			}
			return 0;
	}
	
	public void updateFitnessHCS(int fitness){
			this.fitness=fitness;
			//fitness++;
			//components[classLabel]++;
}
	// Inherited from IFitnessMeasure
	public String toString(){
		String buffer = "";
		buffer += overallFitness();
		return buffer;
	}

	@Override
	public IFitnessMeasure clone() {
		// TODO Auto-generated method stub
		return new MultiClassFitnessMeasure(this);
	}

}
