package core;

import java.util.ArrayList;


public class Individual implements Comparable<Individual> {

	private double[] genes;
	private double fitness;
	private double[] coh;
	private double[] freq;



	public Individual(double[] g){
		genes = g.clone();
		fitness = Double.MAX_VALUE;
	}

	public double[] getGenes() {
		return genes;
	}

	public void setGenes(double[] genes) {
		this.genes = genes;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public int compareTo(Individual o) {
		if(this == o) return 0;
		else if(this.fitness > o.fitness) return 1;
		else return -1;
	}

	public double[] getCoh() {
		return coh;
	}

	public void setCoh(double[] coh) {
		this.coh = coh;
	}

	public double[] getFreq() {
		return freq;
	}

	public void setFreq(double[] freq) {
		this.freq = freq;
	}
}
