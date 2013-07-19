package core;


/** basic LGP implementation of a multiclass classification system*/
public class MultiClass extends Main{
	// Initialise the static members which specify the proportion selected through elitism and
	// the size of the selection tournaments for other evolutionary operators:

	public MultiClass(String [] args){
		super(args);
	} 
	
	public void doSetup(){
		super.doSetup(new MultiClassFileReader(), "ionosphere");
	}
	
	@Override
	public MultiClassPopulation create() {
		// Building the population object, randomising the fitness of it all:
		return new MultiClassPopulation(c.populationSize, c.numFeatures, c.numRegisters);
	}

	public static void main(String [] args) {
		new MultiClass(args);
	}
}
