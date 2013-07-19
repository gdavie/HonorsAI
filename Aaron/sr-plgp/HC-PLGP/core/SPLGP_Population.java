package core;

import gp_operators.GP_Operator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import misc.Rand;


public class SPLGP_Population extends InstructionPopulation<SPLGP_Population,SPLGP_Program>{
	public Set<Integer> used;
	/** Uses the default cctor, dtor*/
	public SPLGP_Population(int pop_size, int numFeatures, int numRegisters) {
		super(new SPLGP_Pfactory(), pop_size, numFeatures, numRegisters);
		
	}	
	
	/** Carries out a size GenerationalTournamentPopulation::tournamentSize tournament:
	const IProgram* SelectProgByFitness() const;*/
	public SPLGP_Program selectProgByEstimate(SPLGP_Program other, int num){
		if(size() == 1) {
			return programs.get(0);
		}

		//current best program
		int bestProgIndex = Rand.Int(programs.size());
		//estimated amount we can improve by swapping with current best program
		double bestDiff = 0;
		
		double[] diff = other.diff(programs.get(bestProgIndex));
		double sum = 0;
		for(int j = 0; j < diff.length; j++){
			if(diff[j] > 0)
				sum+= diff[j];
		}
		bestDiff = sum;
		
		int candidateProgIndex;

		for(int i = 1; i < num && i < size(); ++i) {
			candidateProgIndex = Rand.Int(size());

			if(bestProgIndex == candidateProgIndex) {
				--i; // Select another, different program
			}
			/**Normal*/
			
			diff = other.diff(programs.get(candidateProgIndex));
			
			sum = 0;
			for(int j = 0; j < diff.length; j++){
				if(diff[j] > 0)
					sum+= diff[j];
			}

			if(sum > bestDiff) {
				bestProgIndex = candidateProgIndex;
			}
		}
		return programs.get(bestProgIndex);
	}

	protected ArrayList<SPLGP_Program> getEliteByClass(int number){
		ArrayList<SPLGP_Program> elite = new ArrayList<SPLGP_Program>();
		System.out.println("***GETELITEBYCLASS***");
		for(int c = 0; c < config.numClasses; c++){
			sortByClass(c);
			for(int i = 0; i < (((double)number/config.numClasses)*(double)4)/5; ++i) {
				SPLGP_Program temp = programs.get(i);
				
				//create program and restore gen and time from cached values
				SPLGP_Program temp2 = garbage.poll();
				temp2.reinitialize(temp);
				temp2.genCreated = temp.genCreated;
				temp2.timeCreated = temp.timeCreated;
				elite.add(temp2);
			}
		}
		sortFittestFirst();
		for(int i = 0; elite.size() < number; i++){
			SPLGP_Program temp = programs.get(i);
			
			//create program and restore gen and time from cached values
			SPLGP_Program temp2 = garbage.poll();
			temp2.reinitialize(temp);
			temp2.genCreated = temp.genCreated;
			temp2.timeCreated = temp.timeCreated;
			elite.add(temp2);
		}
		return elite;
	}

	public void sortByClass(int classNum){
		Collections.sort(programs, new IProgramClassSort(classNum));
	}
	
	public class IProgramClassSort implements Comparator<SPLGP_Program> {

		private int classNum;

		public IProgramClassSort(int classNum){
			this.classNum = classNum;
		}

		public int compare(SPLGP_Program o1, SPLGP_Program o2) {
			if(o1.resps[classNum] == o2.resps[classNum]){
				if(o1.fitness() == o2.fitness()){
					return 0;
				}
				else
					return o1.fitness() < o2.fitness()?-1:1;
			}
			else 
				return o1.resps[classNum] < o2.resps[classNum]?-1:1;
		}
	}
	public void setUsed(Set<Integer> used){
		this.used=used;
//		System.out.println(used.toString());
	}
	
	public Set<Integer> getUsed(){
		return used;
	}
	/**
	 * Shows the factors that have been used by blueprints from this
	 * subpopulation. The int S is purely for output, the subpopulation does not
	 * know it own index
	 */
	public void showUsed(int s){
		System.out.print("Used factors ("+s+"): ");
		for(Integer i :used){
			System.out.print(i + " ");
		}
		System.out.println();
	}
	/**
	 * The 'list' in mapping is always 0 or 1 programs, but we are constrained by IPopulation's design
	 * to continue to use it for now.
	 */
	public Map<SPLGP_Program, List<SPLGP_Program>> iteratePopulation(double proportionElitism, int tournamentSize){
		// Store the next population of programs into another vector temporarily:
		ArrayList<SPLGP_Program> nextGen = new ArrayList<SPLGP_Program>();
		Map<SPLGP_Program, List<SPLGP_Program>> mapping = new HashMap<SPLGP_Program, List<SPLGP_Program>>();
		for(int i = 0; i < programs.size();i++){
			mapping.put(programs.get(i), new ArrayList<SPLGP_Program>());
		}
		//Get a map of elite factors and the index they are expected to appear at.
		Map<Integer, SPLGP_Program> elite = fixElite(mapping);
		
		//initialises roulette wheel selection
		refreshRoulette();
		//Calculate how many child programs we need for the new population
		int popToEvolve = (size()-elite.size());
		//Evolve the childrens
		while(nextGen.size() <popToEvolve) {

			GP_Operator<SPLGP_Program> gpo = gp_ops.getRandomElement();
			SPLGP_Program p1 = programs.get(tournamentSelect(tournamentSize)); 
			SPLGP_Program p2 = programs.get(tournamentSelect(tournamentSize));//TODO THIS IS WRONG FOR SELECTIVE CROSSOVER
			SPLGP_Program res = garbage.poll();
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
		//Finally, add elite solutions at correct index location.
		//The TreeSet is sorted because we need to add in incrementing order
		//to accomodate for the shifting up of following elements
		for(Integer e : used){
			nextGen.add(e, elite.get(e));
		}
		

		garbage.clear();//to account for the few elite programs copied over (otherwise size of garbage increases)
		garbage.addAll(programs);
		programs = nextGen;
		
		return mapping;
	}
	/**
	 * We need to define elitism differently here.
	 * Rather than taking a percentage off the top, we need to
	 * lock the factors that are part of blueprints
	 * 
	 * This raises the following issues:
	 * How to map them back so that they either have the same index
	 * Actually this may be the only problem we are facing : prehaps re-sorting by 
	 * fitness or whatever reshuffliing is happening is just switching blueprints around
	 * 
	 * The other concern would be whether we prevent all factor evolution by locking them
	 * with 500 bp, any subpop of 500 or less ( 2 parts) would prevent any evolution
	 * (potentially)
	 */
	
	/**
	 * Iterates through the TreeSet of used factors and does two things:
	 * Adds them to the elite map <index, clone>
	 * Adds the clone to the mapping
	 * 
	 * @param  mapping maps from programs to their clones, if they are elite.
	 */
	public Map<Integer, SPLGP_Program> fixElite(Map<SPLGP_Program, List<SPLGP_Program>> mapping){
		Map<Integer, SPLGP_Program> elite= new HashMap<Integer,SPLGP_Program>();
		for(Integer i : used) {
			SPLGP_Program temp = programs.get(i);

			//create program and restore gen and time from cached values
			SPLGP_Program temp2 = garbage.poll();
			temp2.reinitialize(temp);
			temp2.genCreated = temp.genCreated;
			temp2.timeCreated = temp.timeCreated;

			//Add the clone to mappings.
			elite.put(i, temp2);
			mapping.get(temp).add(temp2);
		}
		return elite;
	}
}
