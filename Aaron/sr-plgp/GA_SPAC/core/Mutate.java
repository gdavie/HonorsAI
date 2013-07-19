package core;

import java.util.ArrayList;
import java.util.Random;

public class Mutate {

	private Settings settings;

	private boolean test = false;

	public Mutate(Settings set){
		settings = set;
	}

	public ArrayList<Individual> produce(ArrayList<Individual> tempInds) {
		Individual ind1;
		ArrayList<Individual> mutants = new ArrayList<Individual>();
		double[] layers  = new double[settings.getLayers()*2];
		for(int i = 0; i < settings.getnumberOfElites(); i++){
			ind1 = tempInds.get((int)(Math.random()*tempInds.size()));

			//if(Math.random() < settings.getchanceToMutate()){
				layers  = new double[settings.getLayers()*2];
				for(int j = 0; j < settings.getLayers()*2; j++){
					if(test) System.out.println("DEBUG - Mutating");
					if(Math.random() < settings.getchanceToMutate()){
						if(j%2==0){
							if(test) System.out.println("DEBUG - No Remainder");
							if(Math.random() < 0.5){
								if(test) System.out.println("DEBUG - Less than 0.5");
								layers[j] = ind1.getGenes()[j]+(settings.getmutationVarianceT()*Math.random());
							}
							else{
								if(test) System.out.println("DEBUG - greater than 0.5");
								layers[j] = ind1.getGenes()[j]-(settings.getmutationVarianceT()*Math.random());
							}
						}
						else{
							if(test) System.out.println("DEBUG - Yes Remainder");
							if(Math.random() < 0.5){
								if(test) System.out.println("DEBUG - Less than 0.5");
								layers[j] = ind1.getGenes()[j]+(settings.getmutationVarianceSV()*Math.random());
							}
							else{
								if(test) System.out.println("DEBUG - greater than 0.5");
								layers[j] = ind1.getGenes()[j]-(settings.getmutationVarianceSV()*Math.random());
							}
						}
					}
					else{
						layers[j] = ind1.getGenes()[j];
					}
				}
				boolean newInd = false;
				for(int j = 0; j < settings.getLayers()*2; j++){
					if(layers[j]!=ind1.getGenes()[j]){
						newInd = true;
					}
				}
				if(newInd){
					mutants.add(new Individual(layers));
				}
			//}
		}
		if(test) {
			 System.out.println("DEBUG - Printing Mutants");
			for(Individual i: mutants){
				System.out.println("Individual "+(mutants.indexOf(i)));
				double[] l = i.getGenes();
				for(int j = 0; j < l.length; j++){
					System.out.print(l[j]+"  ");
				}
				System.out.println();
				System.out.println("Fitness = "+i.getFitness());
				System.out.println();
			}
		}
		return mutants;
	}

}
