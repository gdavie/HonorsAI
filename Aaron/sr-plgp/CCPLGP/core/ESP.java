package core;

public class ESP extends Many {
	
	public ESP(String[] args) {
		super(args);
	}

	@Override
	public ESPPopulation create() {
		// Building the population object, randomising the fitness of it all:
		return ESPPopulation.create(new MultiClassProgramFactory(), c.numFeatures, c.numRegisters);
	}
	
	public static void main(String [] args) {
		new ESP(args);
	}

}
