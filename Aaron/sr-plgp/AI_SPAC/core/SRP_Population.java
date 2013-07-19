package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import misc.Rand;
import misc.WeightedCollection;
import operators.SRP_Crossover;
import operators.SRP_Modify_add;
import operators.SRP_Modify_div;
import operators.SRP_Modify_mul;
import operators.SRP_Modify_sub;
import operators.SRP_Mutation;
import operators.SRP_Operator;
import Fitness.SRP_FitnessEnvironment;


public final class SRP_Population {
	
	private final boolean SHOWMODIFY=false;
	private final boolean TESTMODEL=false;
	protected WeightedCollection<SRP_Operator> gp_ops = new WeightedCollection<SRP_Operator>();

	private WeightedCollection<SProgram> roulette = new WeightedCollection<SProgram>();
	private boolean eliteFactors[];
//	private int subpopindex;

	protected Config config;

	/**used for collecting statistics TODO shoudl change these into private and pass*/
	public static int gen = 0;//Changed to zero to allow an initial unmodified population that
							//is evaluated before any genetic ops occur
	public static double time = System.currentTimeMillis();
	public SProgram best;
	//protected IProgramFactory<SProgram> factory;
	protected ArrayList<SProgram> programs = new ArrayList<SProgram>();
	protected Queue<SProgram> garbage = new LinkedList<SProgram>();
	protected int numFeatures;
	
	public void reinitialize(SRP_Population pop){
		best = pop.best.clone();
		gen = IPopulation.gen;//TODO worry about these later
		time = IPopulation.time;//TODO worry about these later
		numFeatures = pop.numFeatures;
	}
	
	protected SRP_Population() {
		config = Config.getInstance();
		eliteFactors= new boolean[config.populationSize];
		int pop_size=config.populationSize;
		// Now construct the programs, uniformly distributed in size over the range of sizes specified
		// in the config object:
		int prog_size = config.numPieces;
		for(int i = 0; i < pop_size; ++i) {
			if(i==0&&TESTMODEL){//create test program
				programs.add(new SProgram(prog_size, config, true));
				garbage.offer(new SProgram(prog_size, config, true));
			}else{
				programs.add(new SProgram(prog_size, config));
				garbage.offer(new SProgram(prog_size, config));
			}
		}
		setup();
	}

	/** Builds the next generation from the current one, based on program's assigned fitness.*/
	public Map<SProgram, List<SProgram>> iteratePopulation(double proportionElitism, int tournamentSize){

		// Store the next population of programs into another vector temporarily:
				ArrayList<SProgram> nextGen = new ArrayList<SProgram>();
				Map<SProgram, List<SProgram>> mapping = new HashMap<SProgram, List<SProgram>>();
				//Programs is the population here.
				//We are mapping an empty arraylist to each program in the population. 
				for(int i = 0; i < programs.size();i++){
					mapping.put(programs.get(i), new ArrayList<SProgram>());
				}

				// Carry out the elitism, rounding down the number of individuals we create according to the
				// proportion.	
				if(gen>0){//Prevent ANY changes to initial population -- we want to evaluate the random programs
					//before modifying them. This also allows the insertion of a TEST MODEL ie the model that
					//human expert has provided; as a benchmark best model.
					ArrayList<SProgram> elite = getElite((int)(proportionElitism * size()), mapping);
					nextGen.addAll(elite);
				}else{
					ArrayList<SProgram> elite = getElite((int)(size()), mapping);
					nextGen.addAll(elite);
				}


				//initialises roulette wheel selection
//				refreshRoulette();
				
				while(nextGen.size() < size()) {
					//Weighted collection contains all the operators. This selects an operation to apply,
					//By weighted probability
					SRP_Operator gpo = gp_ops.getRandomElement();
					//Use tournament select to choose two programs to modify.
					SProgram p1 = programs.get(tournamentSelect(tournamentSize)); 
					SProgram p2 = programs.get(tournamentSelect(tournamentSize));
					SProgram res = garbage.poll();
					//this next line copies p1 into res
					res.reinitialize(p1);

					gpo.execute(p1, p2, res);
					if(SHOWMODIFY){
						System.out.println("p1\n"+p1.toString());
						System.out.println("p2\n"+p2.toString());
						System.out.println("res\n"+res.toString());
						System.out.println("------------------------------");						
					}

					//updates fitness status to force reevaluation
					res.setFitnessStatus(false);

					nextGen.add(res);
				}

				garbage.clear();//to account for the few elite programs copied over (otherwise size of garbage increases)
				garbage.addAll(programs);
				programs = nextGen;
				
				return mapping;
	}

	private void setup(){
		//Add the genetic operators, and weighting
		gp_ops.addElement(new SRP_Crossover(),100); //Need a reasonable weight for crossover
		gp_ops.addElement(new SRP_Mutation(),20);//Low weight, just enough to prevent local trapping
		//These are to encourage 'fine tuning' of good models
		gp_ops.addElement(new SRP_Modify_add(),50);
		gp_ops.addElement(new SRP_Modify_sub(),50);
		gp_ops.addElement(new SRP_Modify_mul(),20);
		gp_ops.addElement(new SRP_Modify_div(),20);
	}

	/** Updates the fitness of all programs which currently have false fitness-is-correct status
	 flags. After this method is called all programs in this population will have their
	 fitness values set correctly.*/

	public <F extends IFitnessCase> void evaluateFlaggedPrograms(SRP_FitnessEnvironment tfe){
		for(int i = 0; i < size(); ++i) {
			System.out.println("Model "+i+" ====================");
			//Iterate through the whole population
			SProgram prog = programs.get(i);
			if(!prog.fitnessStatusReference()) {

				//This is where the actual call to execute is.
				prog.updateFitness(tfe);
				double temp = prog.fitness();
				if(best == null || temp < best.fitness()){
					System.out.println("best fitness: " + temp);
					//TODO do I need clone here?
				//	best = prog.clone();
					best = prog;
					if(config.printBest){
						System.out.println(best);
					}
				}
				System.out.println("best :"+best.getFitness()+"\n"+best);
				// This program's fitness measure now has the correct fitness values:
				prog.setFitnessStatus(true);

			}else{
				System.out.println(prog.fitness());
			}
		}
	}

	/** Evolves the population for up to the maximum number of generations against the fitness
	 cases in the FitnessEnvironment passed to it as a parameter. Returns true if a perfect
	 solution (fitness < epsilon) is found, otherwise false.*/
	public <F extends IFitnessCase> int evolve(SRP_FitnessEnvironment tfe){
		time = System.currentTimeMillis();
		long cacheTime = System.currentTimeMillis();
		for(gen = 1; gen <= config.maxGenerations; ++gen) {
			long gen2 = doEvolve(tfe, cacheTime);

			//times how long the entire generation took
			long tempTime = System.currentTimeMillis();
			time = ((double)tempTime-cacheTime)/1000;
			System.out.println("generation "+gen +" time: " + time +" seconds ^v^v^v^v^v^v^v^v^v^v^v^v\n");
			cacheTime = tempTime;
		}

		//printCacheStats();

		return config.maxGenerations + 1;
	}
	protected <F extends IFitnessCase>int doEvolve(SRP_FitnessEnvironment tfe, long cacheTime){
		long ipTime = System.currentTimeMillis();

		//creates the next generation, GP operations only
		iteratePopulation(config.proportionElitism, config.tournamentSize);
		
		System.gc();

		long between = System.currentTimeMillis();
		double time = ((double)between-ipTime)/1000;

		System.out.println("evolution time: " + time);
		


		//evaluates the programs
		//Here we will perform the libCPS calculation
		//and then evaluate the fitness
		evaluateFlaggedPrograms(tfe);
		
		time = ((double)System.currentTimeMillis()-between)/1000;
		System.out.println("evaluation time: " + time);

		//for logging the individuals in a population
		if(config.log){  SRP_Logger.log(gen, this);  }

		//for keeping track of average best fitness per generation
		Logger.updateAvgBestFit(gen-1, best.fitness());

		//check to see if we have found a solution
		if(solutionExists())   
			return gen;  
		else
			return -1;
	}
	
	public boolean solutionExists(){
		for(int i = 0; i < size(); ++i) {
			if(programs.get(i).fitness() <= config.epsilon) {
				SProgram temp = programs.get(i);
				if(config.printBest)System.out.println(temp);
				return true;
			} 
		}
		return false;
	}
	/*Carries out a size tournamentSize tournament and returns the fittest individual;*/
	protected int tournamentSelect(int tournamentSize){
		if(programs.size() == 1) {
			return 0;
		}

		int bestProgIndex = (int)(Math.random()*programs.size());
		int candidateProgIndex;

		for(int i = 1; i < tournamentSize && i < programs.size(); ++i) {
			candidateProgIndex = Rand.Int(programs.size());

			// If the best program has a higher (worse) fitness than the candidate then the candidate is
			// the new best:
			if(bestProgIndex == candidateProgIndex) {
				--i; // Select another, different program
			}
			/**Normal*/

			else if(programs.get(bestProgIndex).fitness() > programs.get(candidateProgIndex).fitness()) {
				bestProgIndex = candidateProgIndex;
			}
		}
		return bestProgIndex;
	}

	/**
	Carries out a size tournamentSize tournament and returns the fittest individual;*/
	public int rouletteSelect(){

		return roulette.getRandomIndex();
	}

	/**this method sets up the weighted collection we are using as a "roulette wheel" for roulette selection.
	 * this should be called once each generation immediately before any calls to select programs for reproduction.
	 * @param list
	 */
	public void refreshRoulette(){
		roulette.clear();
		for(SProgram prog:programs){
			double weight = prog.fitness();
			roulette.addElement(prog, weight);
		}
	}


	/** Returns the number of programs in this population:*/
	public int size() { 
		return programs.size(); 
	}

	/** Sorts the population in ascending order of fitness, so that the fittest programs are 
	 first in the internal member (meaning they will be printed first).*/
	public void sortFittestFirst(){
		Collections.sort(programs, new IProgramAscendingSort());
	}

	/** Sorts the population in descending order of fitness, so that the least fit programs are 
	 first in the internal member (meaning they will be printed first, also useful in 
	 steady-state population iterations). Method is considered const even though it changes
	 the order of the instructions as this is not a substantial change.*/
	public void sortFittestLast(){
		Collections.sort(programs, new IProgramDescendingSort());
	}

	/** Returns a string representation of each program in the population. The fitness of each
	 program can optionally be printed, and the instructions which are currently marked as 
	 introns in each program will be commented out if the second parameter is true.*/
	public String toString(){
		return toString(true, false);
	}

	public String toString(boolean printFitness, boolean commentIntrons){
		String buffer = "";

		for( int i = 0; i < size(); ++i) {
			buffer += "Program " + i + ":\n" + programs.get(i).toString() 
			+ "\n" + "\n";
		} 

		return buffer;
	}

	public ArrayList<SProgram> getPrograms(){
		return programs;
	}

///////////////////////////////// Population Sorting Functors  ///////////////////////////////
	/** Ascending IProgram sorting function - for std::sort. This will put the fittest programs at 
	 the beginning of the container.*/
	protected class IProgramAscendingSort implements Comparator<SProgram> {

		public int compare(SProgram o1, SProgram o2) {
			if(o1.fitness() == o2.fitness())
				return 0;
			else 
				return o1.fitness() < o2.fitness()?-1:1;
		}
	}

	/** Descending IProgram sorting function - for std::sort. This will put the fittest programs 
	 at the beginning of the container.*/
	protected class IProgramDescendingSort implements Comparator<SProgram> {

		public int compare(SProgram o1, SProgram o2) {
			if(o1.fitness() == o2.fitness())
				return 0;
			else 
				return o1.fitness() < o2.fitness()?1:-1;
		}
	}
///////////////////////////////// END Population Sorting Functors  ///////////////////////////////
	
	public ArrayList<SProgram> getElite(int number, Map<SProgram, List<SProgram>> mapping){
		sortFittestFirst();
		ArrayList<SProgram> elite = new ArrayList<SProgram>();
		for(int i = 0; i < number; ++i) {
			SProgram temp = programs.get(i);
			//create program and restore gen and time from cached values
			SProgram temp2 = garbage.poll();
			temp2.reinitialize(temp);
			temp2.genCreated = temp.genCreated;
			temp2.timeCreated = temp.timeCreated;
			elite.add(temp2);
			
			mapping.get(temp).add(temp2);
		}
		return elite;
	}

	public void setElite(int factor){
		eliteFactors[factor]=true;
	}
	protected SProgram get(int i){
		return programs.get(i);
	}

	/**updates a single pointer*/
	//(m2 := mapping, bp.parts := pointers, sub_pops[s].programs := all, s:= index)
	public static<X extends IProgram<X>> void updatePointer(Map<X, List<X>> mapping, X[] pointers, List<X> all, int index){
		//It seems this 'list' consists only of one X; which is the newly reinitialised copy from elitism.
		List<X> res = mapping.get(pointers[index]);
		X prog = null;
		//This is the case where res is not flagged as elite.
		//Prog is assigned a random member of the child subpopulation
		if(res.isEmpty()){//AzMod null check here res==null||
			prog = all.get((int)(Math.random()*all.size()));
		}
		//This is the case where res is flagged as elite.
		//prog is assigned the reinitialised (elite) copy to the subpopulation.
		else{
			prog = res.get((int)(Math.random()*res.size()));//size is always 1
		}
		//The program at the index passed in is assigned prog.
		pointers[index] =  prog;

	}

	/**updates all pointers*/
	public static<X extends IProgram<X>> void updatePointer(Map<X, List<X>> mapping, X[] parts, List<X> programs){
		for(int index = 0; index < parts.length; index++){
			List<X> res = mapping.get(parts[index]);
			X prog = null;
			if(res.isEmpty()){//AzMod null check here res==null||
				prog = programs.get((int)(Math.random()*programs.size()));
			}
			else{
				prog = res.get((int)(Math.random()*res.size()));//size is always 1
			}
			parts[index] =  prog;

		}
	}

	public int getNumFeatures() {
		return numFeatures;
	}

}
