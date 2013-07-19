package core;


import gp_operators.GP_Operator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import misc.Rand;
import misc.Stats;
import misc.WeightedCollection;

/** Represents a population of programs. The template parameter IP should be some subclass of
 IProgram, otherwise compilation errors will result. Program classes should have a 
 constructor which takes an unsigned int (the program size/length) and a Config<T> object, 
 in that order.*/
public abstract class IPopulation<IPOP extends IPopulation<IPOP,IPR>,IPR extends IProgram<IPR>>{

	protected WeightedCollection<GP_Operator<IPR>> gp_ops = new WeightedCollection<GP_Operator<IPR>>();

	private WeightedCollection<IPR> roulette = new WeightedCollection<IPR>();
	private boolean eliteFactors[];
	private int subpopindex;

	/**
	 * the config file
	 */
	protected Config config;

	/**used for collecting statistics TODO shoudl change these into private and pass*/
	public static int gen = 1;
	public static double time = System.currentTimeMillis();

	/**
	 * the best individual so far according to validation set
	 */
	public IPR best;

	/**
	 * For Caching"
	 */
	protected Stats cacheTimer[] = new Stats[3];

	/**
	 * a factory for producing new IProgram's, necessary because java is not C++
	 */
	protected IProgramFactory<IPR> factory;

	/**
	 * The bool in each program indicates whether or not the associated program has been changed and therefore needs to have its fitness updated - false means its fitness is correct.
	 */
	protected ArrayList<IPR> programs = new ArrayList<IPR>();

	/**
	 * this is a "dead" population present for the purpose of being recycled. This is faster than repeatedly declaring and deleting programs, and thus forcing both memory allocation and garbage collection. Or so  I speculate
	 */
	protected Queue<IPR> garbage = new LinkedList<IPR>();

	/**copies information, but not programs or the mechanism to create them 
	 * such as factories.
	 * @param pop
	 */

	/**
	 * information about the programs which make up this population. All programs in a population should be homogenous, i.e. have the same number of features and the same number of registers
	 */
	//for the purposes of having programs with a variable number of features
	protected int numFeatures;
	//for the purposes of having programs with a variable number of registers

	protected int numRegisters;

	public void reinitialize(IPOP pop){
		best = pop.best.clone();
		gen = IPopulation.gen;//TODO worry about these later
		time = IPopulation.time;//TODO worry about these later
		for(int i = 0; i < cacheTimer.length; i++){
			cacheTimer[i] = cacheTimer[i].clone();
		}

		numFeatures = pop.numFeatures;
		numRegisters = pop.numRegisters;
	}
	public IPopulation(){
		
	}
	
	
	public IPopulation(IProgramFactory<IPR> programFactory, int pop_size, int numFeatures, int numRegisters){


		config = Config.getInstance();
		factory = programFactory;
		//System.out.println(config.numPieces+" : "+pop_size);
		eliteFactors= new boolean[config.populationSize];
		
		// Now construct the programs, uniformly distributed in size over the range of sizes specified
		// in the config object:
		int prog_size = config.initialMinLength;
		int numEachSize = pop_size / (config.initialMaxLength - prog_size + 1);
		for(int i = 0; i < pop_size; ++i) {
			programs.add(programFactory.createProgram(prog_size));//note i had to use the factory pattern here
			garbage.offer(programFactory.createProgram(prog_size));
			if(i != 0 && i % numEachSize == 0) {
				++prog_size;
			}
		}

		//for cache timing
		for(int i = 0; i < cacheTimer.length; i++){
			cacheTimer[i] = new Stats();
		}

		this.numFeatures = numFeatures;
		this.numRegisters = numRegisters;

	}

	/** Evolves the population for up to the maximum number of generations against the fitness
	 cases in the FitnessEnvironment passed to it as a parameter. Returns true if a perfect
	 solution (fitness < epsilon) is found, otherwise false.*/
	public <F extends IFitnessCase> int evolve(FitnessEnvironment<F> tfe, FitnessEnvironment<F> vfe){
		evaluateFlaggedPrograms(tfe, vfe);
		if(config.log)
			Logger.log("initial", this);
		/* Do not end search on generation zero HACK*/
//		if(solutionExists()) {
//			return 0; 
//		}

		time = System.currentTimeMillis();
		long cacheTime = System.currentTimeMillis();
		for(gen = 1; gen <= config.maxGenerations; ++gen) {
			long gen2 = doEvolve(tfe, vfe, cacheTime);

			//times how long the entire generation took
			long tempTime = System.currentTimeMillis();
			time = ((double)tempTime-cacheTime)/1000;
			if(gen > 3)
				cacheTimer[2].update(time);
			System.out.println("generation time: " + time +"\n");
			cacheTime = tempTime;
		}

		printCacheStats();

		return config.maxGenerations + 1;
	}

	protected <F extends IFitnessCase>int doEvolve(FitnessEnvironment<F> tfe, FitnessEnvironment<F> vfe, long cacheTime){
		//prints status information to the standard out
		if(gen % config.sysoutInterval == 0){
			double fitness = best.fitness();
			double valid = best.validationFitness();
			System.out.print("Generation: " + gen + " Best Individual = (f)"
					+ fitness + " (v)" +valid);
			//TODO numbers off here due to HCS
			System.out.print(" Training Acc: " + (1-fitness/tfe.size()));//original line
			System.out.println(" Validation Acc: " + (1-valid/tfe.size()));//original line
		}
		long ipTime = System.currentTimeMillis();

		//creates the next generation
		iteratePopulation(config.proportionElitism, config.tournamentSize);
		
		System.gc();

		long between = System.currentTimeMillis();
		double time = ((double)between-ipTime)/1000;

		//times how long the evolution took
		if(gen > 3){   cacheTimer[0].update(time);   }
		System.out.println("evolution time: " + time);
		


		//evaluates the programs
		evaluateFlaggedPrograms(tfe, vfe);

		time = ((double)System.currentTimeMillis()-between)/1000;
		System.out.println("evaluation time: " + time);

		//times how long the evaluation took
		if(gen > 3){   cacheTimer[1].update(time);   }

		//for logging the individuals in a population
		if(config.log){  Logger.log(gen, this);  }

		//for keeping track of average best fitness per generation
		Logger.updateAvgBestFit(gen-1, best.fitness());

		//check to see if we have found a solution
		if(solutionExists())   
			return gen;  
		else
			return -1;
	}

	/** Updates the fitness of all programs which currently have false fitness-is-correct status
	 flags. After this method is called all programs in this population will have their
	 fitness values set correctly.*/
	public <F extends IFitnessCase>void evaluateFlaggedPrograms(FitnessEnvironment<F> tfe, FitnessEnvironment<F> vfe){
		markSubIntrons();

		for(int i = 0; i < size(); ++i) {
			IPR prog = programs.get(i);
			if(!prog.fitnessStatusReference()) {

				prog.updateFitness(tfe, vfe);

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

	}

//	public void markSubIntrons(){
//		for(IProgram<IPR> ip: programs){
//			ip.markIntrons();
//		}
//	}

	public void markSubIntrons(){
		int i=0;
		for(IProgram<IPR> ip: programs){
			//AzH This is where I can hook in for PM program checking
			i++;
			ip.markIntrons();
		}
//		System.out.println("****************"+i);
	}
	/** Returns true if a program with fitness <= epsilon exists.*/
	public boolean solutionExists(){
		for(int i = 0; i < size(); ++i) {
			if(programs.get(i).fitness() <= config.epsilon) {
				IPR temp = programs.get(i);
				if(config.printBest)System.out.println(temp);
				return true;
			} 
		}
		return false;
	}

	public void printTest(String s){
		System.out.println("-------------------------------------" + s + "--------------------------------------");
		System.out.println("programs:");
		for(int i = 0; i < programs.size(); i++){
			System.out.println(programs.get(i));
//			System.out.println(((PLGP_Blueprint)programs.get(i)).getReg_cache());
		}
		System.out.println("garbage:");
		int x = garbage.size();
		for(int i = 0; i < x; i++){
			IPR g = garbage.poll();
			System.out.println(g);
//			System.out.println(((PLGP_Blueprint)g).getReg_cache());
			garbage.offer(g);
		}
	}

	/** This function builds the next generation from the current one (e.g. steady-state,
	 generational + hill climbing, whatever the function implements) according to the current
	 fitness of each program.*/
	/** Builds the next generation from the current one, based on program's assigned fitness.*/
	public Map<IPR, List<IPR>> iteratePopulation(double proportionElitism, int tournamentSize){
		// Store the next population of programs into another vector temporarily:
		ArrayList<IPR> nextGen = new ArrayList<IPR>();
		Map<IPR, List<IPR>> mapping = new HashMap<IPR, List<IPR>>();
		for(int i = 0; i < programs.size();i++){
			mapping.put(programs.get(i), new ArrayList<IPR>());
		}

		// Carry out the elitism, rounding down the number of individuals we create according to the
		// proportion.
		//ArrayList<E> elite = getEliteByClass((int)(config.proportionElitism * size()));
		//TODO we need to deefine elitism here as factors that are involved in blueprints.
		
		ArrayList<IPR> elite = getElite((int)(proportionElitism * size()), mapping);
		nextGen.addAll(elite);

		//initialises roulette wheel selection
		refreshRoulette();
		
		while(nextGen.size() < size()) {

			GP_Operator<IPR> gpo = gp_ops.getRandomElement();
			IPR p1 = programs.get(tournamentSelect(tournamentSize)); 
			IPR p2 = programs.get(tournamentSelect(tournamentSize));//TODO THIS IS WRONG FOR SELECTIVE CROSSOVER
			IPR res = garbage.poll();
			//this next line copies p1 into res
			res.reinitialize(p1);

			//stores the position where the modification took place for later use with caching
			int modPos = gpo.execute(p1, p2, res);
			int modPos2 = res.randomlyCullToSize(config.maxLength);
			res.setModPos(Math.min(modPos, modPos2));

			//updates fitness status to force reevaluation
			res.setFitnessStatus(false);

			nextGen.add(res);
		}

		garbage.clear();//to account for the few elite programs copied over (otherwise size of garbage increases)
		garbage.addAll(programs);
		programs = nextGen;
		
		return mapping;
	}

	/**this method selects some individuals using roulette wheel, some using tournament. This aims to 
	 * benefit from the diversification of roulette wheel combined with the powerful driving force of 
	 * tournament. 
	 * 
	 * This method was written because roulette wheel fails when individuals have a large value for 
	 * fitness, and tournament fails when the population has difficulty with maintaining diversity.
	 * 
	 * @param list
	 * @param tournamentSize
	 * @param ratio
	 * @return
	 */
//	private int hybridSelect(int tournamentSize, double fracTournament){
//		if(Math.random() > fracTournament){
//			return tournamentSelect(tournamentSize);
//		}
//		else{
//			return rouletteSelect();
//		}
//	}

	/**
	Carries out a size tournamentSize tournament and returns the fittest individual;*/
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

		//System.out.println(bestProgIndex);
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
		for(IPR prog:programs){
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
		Collections.sort(programs, new IProgramAscendingSort<IPR>());
	}

	/** Sorts the population in descending order of fitness, so that the least fit programs are 
	 first in the internal member (meaning they will be printed first, also useful in 
	 steady-state population iterations). Method is considered const even though it changes
	 the order of the instructions as this is not a substantial change.*/
	public void sortFittestLast(){
		Collections.sort(programs, new IProgramDescendingSort<IPR>());
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
			buffer += "Program " + i + ":\n" + programs.get(i).toString(printFitness, commentIntrons) 
			+ "\n" + "\n";
		} 

		return buffer;
	}

	public ArrayList<IPR> getPrograms(){
		return programs;
	}

///////////////////////////////// Population Sorting Functors  ///////////////////////////////
	/** Ascending IProgram sorting function - for std::sort. This will put the fittest programs at 
	 the beginning of the container.*/
	protected class IProgramAscendingSort<A extends IProgram<A>> implements Comparator<A> {

		public int compare(A o1, A o2) {
			if(o1.fitness() == o2.fitness())
				return 0;
			else 
				return o1.fitness() < o2.fitness()?-1:1;
		}
	}

	/** Descending IProgram sorting function - for std::sort. This will put the fittest programs 
	 at the beginning of the container.*/
	protected class IProgramDescendingSort<A extends IProgram<A>> implements Comparator<A> {

		public int compare(A o1, A o2) {
			if(o1.fitness() == o2.fitness())
				return 0;
			else 
				return o1.fitness() < o2.fitness()?1:-1;
		}
	}
///////////////////////////////// END Population Sorting Functors  ///////////////////////////////
	
	public ArrayList<IPR> getElite(int number, Map<IPR, List<IPR>> mapping){
		//System.out.println("OriginalGetElite "+ number);
		sortFittestFirst();
		ArrayList<IPR> elite = new ArrayList<IPR>();
		for(int i = 0; i < number; ++i) {
			IPR temp = programs.get(i);

			//create program and restore gen and time from cached values
			IPR temp2 = garbage.poll();
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
	protected IPR get(int i){
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

	public void printCacheStats(){
		//timing for evaluating effectiveness of caching
		System.out.println("CACHE STATS: ");
		System.out.println("operator time: " + cacheTimer[0].mean);
		System.out.println("evaluation time: " + cacheTimer[1].mean);
		System.out.println("generation time: " + cacheTimer[2].mean);
	}



	public int getNumFeatures() {
		return numFeatures;
	}


	public int getNumRegisters() {
		return numRegisters;
	}
	public void setUsed(Set<Integer> used){
		//Passthrough for SPLGP_pop
	}
	public void showUsed(int s){
		//Passthrough for SPLGP_pop
	}
	/**
	 * Returns a set of integers that represent which factors
	 * in this subpopulation have been asigned to blueprints.
	 * @return Set-Integer
	 */
	public Set<Integer> getUsed(){
		return null;
		//Passthrough for SPLGP_pop
	}
}
