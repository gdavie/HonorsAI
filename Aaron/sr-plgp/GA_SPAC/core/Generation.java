package core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Generation {

	private ArrayList<Individual> inds;

	private Settings settings;

	private boolean test = false;

	public Generation(Settings set) {
		settings = set;
		inds = new ArrayList<Individual>();
		for(int i = 0; i < settings.getPopulation(); i++){
			inds.add(randomInd());
		}
	}

	public void newGeneration(ArrayList<Individual> partialInds) {
		inds.clear();
		inds = (ArrayList<Individual>) partialInds.clone();
		if(test) System.out.println("DEBUG - inds Size = "+inds.size());
		for(int i = 0; i < (settings.getPopulation()-partialInds.size()); i++){
			if(test) System.out.println("DEBUG - Adding new ind");
			inds.add(randomInd());
		}
		if(test) System.out.println("DEBUG - Completed  adding all inds - inds Size = "+inds.size());
	}



	private Individual randomInd() {
		double[] layers = new double[settings.getLayers()*2];
		for(int i = 0; i < settings.getLayers()*2; i+=2){
			//set T
			layers[i] = (settings.gettLow() + Math.random() * (settings.gettHigh()-settings.gettLow()));
			//set SV
			layers[i+1] = (settings.getSvLow() + Math.random() * (settings.getSvHigh()-settings.getSvLow()));
		}
		return new Individual(layers);
	}

	public ArrayList<Individual> getInds() {
		return inds;
	}

	public void sort(){
		Collections.sort(inds);
	}

}
