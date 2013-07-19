package ec.app.gpspac;

import ec.EvolutionState;
import ec.Individual;
import ec.app.libCPS.*;
import ec.app.tuto4.DoubleData;
import ec.gp.GPProblem;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;

public class SpacProblem extends GPProblem implements SimpleProblemForm {

	protected static SRP_FitnessEnvironment coherencies;
	private CPSwrapper cps = new CPSwrapper();
	private String hardCodedFile = "/u/students/daviegeor/4year/489/datasets/site01_20m.dat";//hardcoded value
	private int aperture = 20; //hardcoded value - distance between points - equaltril triangle
	
	 public double currentH;
	 public double currentVs;

	public void setup(final EvolutionState state, final Parameter base){
		// very important, remember this
		super.setup(state,base);

		// verify our input is the right class (or subclasses from it)
		if (!(input instanceof DoubleData)){
			state.output.fatal("GPData class must subclass from " + DoubleData.class, base.push(P_DATA), null);
		}

		//Parse coherencies from file
		coherencies = new SRP_FitnessEnvironment();
		coherencies.addCasesFromFile(hardCodedFile); //hardcoded value
		CPSwrapper.setMAXSIZE(coherencies.getLstFreq().size());
		System.out.println(CPSwrapper.getMAXSIZE());
		System.out.println("Loaded "+ (coherencies.getLstCoh().size()+1) + " Frequency points. Beginning evolution.");
		}

	@Override
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {
		// TODO Auto-generated method stub

	}

}

