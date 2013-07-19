package core;

import gp_operators.ManyCrossoverUniform;
import gp_operators.ManyMacroMutation;
import gp_operators.ManyRandomBlueprint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dbg.DebugReader;
import dbg.RegisterEvaluator;

import misc.Stats;


public abstract class ManyPopulation<MPOP extends ManyPopulation<MPOP,MPR,IPR>,MPR extends ManyProgram<MPR,IPR>,IPR extends IProgram<IPR>> 
extends IPopulation<MPOP,MPR>{

	//AzM Introduce dbg and register evaluator
	boolean DEBUG=true;//AzM try to force debug from properties
	DebugReader h;
	RegisterEvaluator re;
	private int spSize;
	private int numSP;
	private int numBP;
	///////////////////////////////////
	
	protected IPopulation<?,IPR>[] sub_pops;//each is a subpopulation

	/**
	 * SWAPS BETWEEN PART FOCUSED ESP AND BLUEPRINT FOCUSED ESP
	 */
	boolean bp_focused = true;

	/** Uses the default cctor, dtor*/ 
	protected ManyPopulation(IPopulation<?,IPR>[] sub_pops, ManyProgramFactory<MPR,IPR> blueprintFactory,
			int numFeatures, int numRegisters) {
		super(blueprintFactory, Config.getInstance().numBluePrints, numFeatures, numRegisters);

		this.sub_pops = sub_pops;

		setup();
	}

	@Override
	/** Builds the next generation from the current one, based on program's assigned fitness.*/
	/*This is the original method*/
	public Map<MPR, List<MPR>> iteratePopulation(double proportionElitism, int tournamentSize){
		for(int i = 0; i < sub_pops.length; i++){
			Map<IPR, List<IPR>> m2 = sub_pops[i].iteratePopulation(config.proportionElitism, config.tournamentSize);
			for(ManyProgram<MPR,IPR> prog : programs){
				updatePointer(m2, prog.parts, sub_pops[i].programs, i);
			}
		}

		return super.iteratePopulation(config.bpProportionElitism, config.bpTournamentSize);
	}

	/** USED ONLY FOR HYBRID! allows us to iterate only the actual population, not the sub pops (which will
	 * not exist when this method is called). A hack because we cant call super.super :(*/
	public Map<MPR, List<MPR>> pmIteratePopulation(double proportionElitism, int tournamentSize){
		return super.iteratePopulation(config.proportionElitism, config.tournamentSize);
	}

	private void setup(){
		gp_ops.addElement(new ManyCrossoverUniform<MPR, IPR>(),20);
		gp_ops.addElement(new ManyMacroMutation<MPR, IPR>(),20);
		gp_ops.addElement(new ManyRandomBlueprint<MPR,IPR>(factory),20);
		//AzM Introduce dbg and register evaluator
//		System.out.println("*************Call Many_pop*************");
		numSP=config.numPieces;
		numBP=programs.size();
		if(DEBUG){
			h = new DebugReader();	
			re = new RegisterEvaluator(numBP, numSP, config.maxGenerations, config.numRegisters);
		}
		for(int i=0;i<programs.size();i++){
			programs.get(i).setId(i);
		}
	/////////////////////////////////
	
	}

	public <F extends IFitnessCase> void pmEvaluateFlaggedPrograms(FitnessEnvironment<F> tfe, FitnessEnvironment<F> vfe){

		super.evaluateFlaggedPrograms(tfe, vfe);
	}

	/** Updates the fitness of all programs which currently have false fitness-is-correct status
	 flags. After this method is called all programs in this population will have their
	 fitness values set correctly.*/
	@Override
	public <F extends IFitnessCase> void evaluateFlaggedPrograms(FitnessEnvironment<F> tfe, FitnessEnvironment<F> vfe){

		for(MPR a: programs){
			a.setFitnessStatus(false);
		}

		//super.evaluateFlaggedPrograms(tfe, vfe);

		//evaluate 
		long time = System.currentTimeMillis();
		twoStageEval(tfe, vfe);
		System.out.println("TEST TIME: " + (System.currentTimeMillis() - time)/1000.0);

		mapFitness();
		//AzM Introduce dbg and register evaluator
		if(re!=null){
			if(h.showSingleBPRegs||h.showGenNR)re.finishGen();//Increments generation counter			
		}
		//////////////////////////////////////////////////
	}

	public void mapFitness(){

		Stats train_avg = new Stats();
		Stats valid_avg = new Stats();

		// set up the mapping
		Map<IPR,List<Double>> train_fits = new HashMap<IPR,List<Double>>();
		Map<IPR,List<Double>> valid_fits = new HashMap<IPR,List<Double>>();
		for(int i = 0; i < sub_pops.length; i++){
			for(int j = 0; j < sub_pops[i].size(); j++){
				train_fits.put(sub_pops[i].get(j), new ArrayList<Double>());
				valid_fits.put(sub_pops[i].get(j), new ArrayList<Double>());
			}
		}

		//store the appropriate values
		for(int i = 0; i < programs.size(); i++){
			ManyProgram<MPR,IPR> prog = programs.get(i);
			for(IPR sub_prog: prog.parts){
				train_fits.get(sub_prog).add(prog.trainFitnessMeasure.fitness);
				valid_fits.get(sub_prog).add(prog.validFitnessMeasure.fitness);
			}
			train_avg.update(prog.trainFitnessMeasure.fitness);
			valid_avg.update(prog.validFitnessMeasure.fitness);
		}

		//process the stored values
		int count = 0;
		//**********************************************************
		//We will take a count of which populations are accessed here
		int[] spCounts = new int[sub_pops.length];
		for(int a = 0; a < sub_pops.length; a++){
			IPopulation<?,IPR> pop = sub_pops[a];
			for(int i = 0; i < pop.size(); i++){
				IPR sp = pop.programs.get(i);
				List<Double> train_l = train_fits.get(sp);
				List<Double> valid_l = valid_fits.get(sp);

				Collections.sort(train_l);
				Collections.reverse(train_l);
				Collections.sort(valid_l);
				Collections.reverse(valid_l);

				if(train_l.size() == 0 || valid_l.size() == 0){
					//System.out.println("example not in any blueprint:  " + i);
					count++;
					sp.trainFitnessMeasure.fitness = train_avg.mean;
					sp.validFitnessMeasure.fitness = valid_avg.mean;
					spCounts[a]++;
				}
				else{
					Stats t = new Stats(), v = new Stats();
					for(int j = 0; j < config.best_n && j < train_l.size();j++){//assumes same size of train and valid lists
						t.update(train_l.get(j));
						v.update(valid_l.get(j));
					}
					sp.trainFitnessMeasure.fitness = t.mean;
					sp.validFitnessMeasure.fitness = v.mean;
				}
			}
		}
		System.out.println(count + " examples were unexecuted.");
		int spSize = config.populationSize / config.numPieces;
		System.out.println("number executed per subpopulation ("+spSize+")");
		System.out.print("SPEX\t");
		for(int z=0; z<spCounts.length;z++){
			System.out.print((spSize-spCounts[z])+"\t");
		}
		System.out.println("");
	}

	public <F extends IFitnessCase>void twoStageEval(FitnessEnvironment<F> tfe, FitnessEnvironment<F> vfe){

		for(MPR prog : programs){
			prog.zeroFitness();
			//AzM Introduce dbg and register evaluator
			prog.markIntrons();
			//prog.markIntrons(re);
			/////////////////////
		}

		//AzM Introduce dbg and register evaluator
		if(re!=null){
			re.finishGen();
			if(h.showSingleBPRegs)re.printBPregs();
			if(h.showGenNR)re.printGenTotals();
		}
		///////////////////////////////////////
		partEval(tfe, true);
		partEval(vfe, false);

		for(int i = 0; i < size(); ++i) {
			MPR prog = programs.get(i);

			/**keeps track of the program which performs best on the validation set*/
			double temp = prog.validationFitness();
			if(best == null || temp < best.validationFitness()){
				System.out.println("best fitness: " + temp);
				best = prog.clone();
				if(config.printBest){
					System.out.println(best);
				}
			}
			// This program's fitness measure now has the correct fitness values:
			prog.setFitnessStatus(true);
		}
	}


	/** this mehod offers increased execution speed by evaluating all program parts,
	 * cacheing the results, then using the cached values to compute combinations. In order to 
	 * do this in a memory friendly mannor, we reverse the order of evaluation. In other words
	 * we evaluate all programs on each training example, rather than all training examples on
	 * each program.
	 */
	public <F extends IFitnessCase>void partEval(FitnessEnvironment<F> fe, boolean train){
		markSubIntrons();

		if (!fe.loadFirstCase()) {
			throw new RuntimeException("No fitness cases in fe to evaluate against");
		}

		int count = 0;
		do {

			//execute all parts on this training example
			evalParts(fe, count, train);

			//now use the parts to evaluate the blueprints
			evalBlueprints(fe, train);

			count++;
		} while (fe.loadNextCase());	
	}

	public <F extends IFitnessCase>void evalParts(FitnessEnvironment<F> fe, int caseNum, boolean train){

		RegisterCollection rc = null;

		for(IPopulation<?,IPR> ipop: sub_pops){
			for(IPR prog : ipop.programs){

				rc = train ? prog.trainFinalRegisterValues : prog.validFinalRegisterValues;

				//note: zeroed registers
				rc.zeroRegisters();
				fe.zeroRegisters();

				//results after execution will now be stored in the programs final register values
				prog.execute(fe, rc, caseNum);
			}
		}
	}

	public <F extends IFitnessCase>void evalBlueprints(FitnessEnvironment<F> fe ,boolean train){

		RegisterCollection blueRC = null, partRC = null;

		for(MPR blueprint: programs){

			//choose the correct register collection and zero registers
			blueRC = train ? blueprint.trainFinalRegisterValues : blueprint.validFinalRegisterValues;
			blueRC.zeroRegisters();

			//sum the parts into the registers
			for(int i = 0; i < blueprint.parts().length; i++){

				IPR part = blueprint.parts()[i];
				partRC = train ? part.trainFinalRegisterValues : part.validFinalRegisterValues;

				blueRC.add(partRC);
			}

			//find the correct fitness measure
			IFitnessMeasure ifm = train ? blueprint.trainFitnessMeasure : blueprint.validFitnessMeasure;

			//process results vector and update fitness
			ifm.updateError(blueRC, fe.currentCase());
		}
	}


	public IPopulation<?,IPR>[] getSub_Pops(){
		return sub_pops;
	}


}
