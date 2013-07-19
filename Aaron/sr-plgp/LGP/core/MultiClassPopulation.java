package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import misc.Rand;


public class MultiClassPopulation extends InstructionPopulation<MultiClassPopulation,MultiClassProgram>{

	/** Uses the default cctor, dtor*/
	public MultiClassPopulation(int pop_size, int numFeatures, int numRegisters) {
		super(new MultiClassProgramFactory(), pop_size, numFeatures, numRegisters);
	}	
	
	/** Carries out a size GenerationalTournamentPopulation::tournamentSize tournament:
	const IProgram* SelectProgByFitness() const;*/
	public MultiClassProgram selectProgByEstimate(MultiClassProgram other, int num){
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

	protected ArrayList<MultiClassProgram> getEliteByClass(int number){
		ArrayList<MultiClassProgram> elite = new ArrayList<MultiClassProgram>();

		for(int c = 0; c < config.numClasses; c++){
			sortByClass(c);
			for(int i = 0; i < (((double)number/config.numClasses)*(double)4)/5; ++i) {
				MultiClassProgram temp = programs.get(i);
				
				//create program and restore gen and time from cached values
				MultiClassProgram temp2 = garbage.poll();
				temp2.reinitialize(temp);
				temp2.genCreated = temp.genCreated;
				temp2.timeCreated = temp.timeCreated;
				elite.add(temp2);
			}
		}
		sortFittestFirst();
		for(int i = 0; elite.size() < number; i++){
			MultiClassProgram temp = programs.get(i);
			
			//create program and restore gen and time from cached values
			MultiClassProgram temp2 = garbage.poll();
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
	
	public class IProgramClassSort implements Comparator<MultiClassProgram> {

		private int classNum;

		public IProgramClassSort(int classNum){
			this.classNum = classNum;
		}

		public int compare(MultiClassProgram o1, MultiClassProgram o2) {
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
	
}
