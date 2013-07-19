package core;

import gp_operators.PMMutation;
import gp_operators.PartCrossover;
import info.Info;

import java.util.List;
import java.util.Map;


public final class PMPopulation extends IPopulation<PMPopulation, PMProgram> {

	/** Uses the default cctor, dtor*/ 
	protected PMPopulation(IProgramFactory<MultiClassProgram> mcpf, int numFeature, int numRegister) {
		super(new PMProgramFactory(), Config.getInstance().populationSize, numFeature, numRegister);

		setup();
	}

	@Override
	/** Builds the next generation from the current one, based on program's assigned fitness.*/
	public Map<PMProgram, List<PMProgram>> iteratePopulation(double proportionElitism, int tournamentSize){

		return super.iteratePopulation(config.proportionElitism, config.tournamentSize);

	}

	private void setup(){
		gp_ops.addElement(new PartCrossover<PMProgram, MultiClassProgram>(),20);
		gp_ops.addElement(new PMMutation<PMProgram, MultiClassProgram>(),20);
		//gp_ops.addElement(new PMCrossover<PMProgram, MultiClassProgram>(),20);
	}

	/** Updates the fitness of all programs which currently have false fitness-is-correct status
	 flags. After this method is called all programs in this population will have their
	 fitness values set correctly.*/
	@Override
	public <F extends IFitnessCase> void evaluateFlaggedPrograms(FitnessEnvironment<F> tfe, FitnessEnvironment<F> vfe){

		//causes alternation between using weighted evaluation and normal evaluations
		if(config.alternating){
			PMProgram.alternate();
		}
		super.evaluateFlaggedPrograms(tfe, vfe);

		//throw in a distance evaluation afterwards.
		if(false){
			System.out.println("INTRA dist: " + Info.avgIntraPopDist(this));
			System.out.println("INTER dist: " + Info.avgInterPopDist(this));
			System.out.println("TOTAL dist: " + Info.avgTotDist(this));
		}

	}

	public static PMPopulation create(IProgramFactory<MultiClassProgram> mcpf, int numFeature, int numRegister) {

		return new PMPopulation(mcpf, numFeature, numRegister);
	}

}
