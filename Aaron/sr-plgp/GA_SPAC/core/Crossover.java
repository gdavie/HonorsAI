package core;

import java.util.ArrayList;

public class Crossover {

	private Settings settings;

	private boolean test = false;

	public Crossover(Settings set){
		settings = set;
	}

	public ArrayList<Individual> produce(ArrayList<Individual> tempInds) {

		Individual ind1;
		Individual ind2;

		ArrayList<Individual> children = new ArrayList<Individual>();
		double[] layers  = new double[settings.getLayers()*2];
		if(tempInds.size() <= 1){
			System.out.println("FATAL ERROR CANNOT CROSSOVER a sample sise of 1 or 0");
		}
		else{
			for(int i = 0; i < settings.getnumberToCrossover(); i++){

			ind1 = tempInds.get((int)(Math.random()*tempInds.size()));
			ind2 = null;
			double random = Math.random();
			while(ind2 == null || ind2 == ind1){
				ind2 = tempInds.get((int)(Math.random()*tempInds.size()));
			}
			if(test) System.out.println("DEBUG - ind1 = "+tempInds.lastIndexOf(ind1)+" ind2 = "+tempInds.lastIndexOf(ind2));
			for(int j = 0; j < settings.getLayers()*2; j+=2){
				//set T
				random = Math.random();
				if(test) System.out.println("DEBUG - T random = "+random);
				if(random <= settings.getchanceToCrossover()){
					layers[j] = ((ind1.getGenes()[j]+ind2.getGenes()[j])/2);
					if(test) System.out.println("DEBUG - T crossover layers[j] = "+layers[j]);
				}else if(random >= settings.getchanceToCrossover()+(settings.getchanceToCrossover()/2)){
					layers[j] = (ind1.getGenes()[j]);
					if(test) System.out.println("DEBUG - T ind1 layers[j] = "+layers[j]);
				}
				else{
					layers[j] = (ind2.getGenes()[j]);
					if(test) System.out.println("DEBUG - T ind2 layers[j] = "+layers[j]);
				}
				//set SV
				random = Math.random();
				if(test) System.out.println("DEBUG - SV random = "+random);
				if(random <= settings.getchanceToCrossover()){
					layers[j+1] = ((ind1.getGenes()[j+1]+ind2.getGenes()[j+1])/2);
					if(test) System.out.println("DEBUG - SV Crossover layers[j+1] = "+layers[j+1]);
				}else if(random >= settings.getchanceToCrossover()+(settings.getchanceToCrossover()/2)){
					layers[j+1] = (ind1.getGenes()[j+1]);
					if(test) System.out.println("DEBUG - SV ind1 layers[j+1] = "+layers[j+1]);
				}
				else{
					layers[j+1] = (ind2.getGenes()[j+1]);
					if(test) System.out.println("DEBUG - SV ind2 layers[j+1] = "+layers[j+1]);
				}
			}

			Individual ind3 = new Individual(layers);
			children.add(ind3);
		}
		}
		if(test) {
			 System.out.println("DEBUG - Printing Chilrdren");
			for(Individual i: children){
				System.out.println("Individual "+(children.indexOf(i)));
				double[] l = i.getGenes();
				for(int j = 0; j < l.length; j++){
					System.out.print(l[j]+"  ");
				}
				System.out.println();
				System.out.println("Fitness = "+i.getFitness());
				System.out.println();
			}
		}
		return children;
	}

}
