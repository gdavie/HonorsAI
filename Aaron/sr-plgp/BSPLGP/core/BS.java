package core;

/** This is the ESP algorithm without blueprints. Instead we dynamically determine the
 * optimum program combinations using PSO on a n dimensional grid interpretation formed
 * an approximation to TSP.
 * @author Carlton
 *
 */
public class BS extends Many {
	
	public BS(String[] args) {
		super(args);
	}

	@Override
	public BSPopulation create() {
		// Building the population object, randomising the fitness of it all:
		return BSPopulation.create(new MultiClassProgramFactory(), c.numFeatures, c.numRegisters);
	}
	
	public static void main(String [] args) {
		new BS(args);
	}

}
