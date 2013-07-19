package core;


/** basic LGP implementation of a multiclass classification system*/
public class PM extends Main{
	// Initialise the static members which specify the proportion selected through elitism and
	// the size of the selection tournaments for other evolutionary operators:

	/**
	 * args[0] = num pieces
	 * args[1] = length
	 * args[2] = file
	 */
	public PM(String [] args){
		super(args);
	}
	
	public void doSetup(){
		super.doSetup(new MultiClassFileReader(), null);
	}
	
	@Override
	public PMPopulation create() {
		// Building the population object, randomising the fitness of it all:
		return PMPopulation.create(new MultiClassProgramFactory(), c.numFeatures, c.numRegisters);
	}
	
	public static void main(String [] args) {
		new PM(args);
	}

}
