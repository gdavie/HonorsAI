package core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import modelling.LayerGenerator;
import Fitness.SRPE;
import Fitness.SRP_FitnessEnvironment;

/**
 * This is an attempt to reduce the annoying pass-through context of 
 * dealing with multiple interfaces.
 * @author az
 *
 */
//public class SProgram<IPR extends IProgram<IPR>> {
public class SProgram{
	protected Config config;
	
	private boolean SHOWINITIALPROGS = true;
	// true if this program's fitness is correct, false otherwise
	protected boolean fitnessStatus;
	private double fitness;
	// For the purposes of collecting statistics

	public int genCreated;

	public double timeCreated;
	public Layer[] layers;
	protected int numLayers;

	public MultiClassProgram[] parts;//TODO Was generic as IPR
	private static SRPE execute;
	
	private double[] freq;
	private double[] coh;
	private double[] ellip;
	private int site;
	
	public SProgram(int numLayers, Config config){
		
		/*
		 * model consists of one or more parts,or layers.
		 */
		
		config = Config.getInstance();
		layers = new Layer[numLayers];
		for(int i = 0; i < numLayers; i++){
			layers[i] = LayerGenerator.createLayer(config.randomThicknessRange, config.randomVelocityRange);
		}

		if(execute == null)
			execute = new SRPE();
		if(SHOWINITIALPROGS)System.out.println(toString());
	}
	
	/**
	 * Quick constructor to build a test program that has same characteristics
	 * as Bill's profile model
	 * @param numLayers
	 * @param config
	 * @param test
	 */
	public SProgram(int numLayers, Config config, boolean test){
		
		config = Config.getInstance();
		layers = new Layer[numLayers];
		/*
		 * Bill's model
		 * 8.6m  @	123.5m/s over 136.5m @  278.7m/s
		 * 0.0086 	0.1235
		 * 0.1365	0.2787
		 * model consists of one or more parts,or layers.
best :1.6135777991498714
H : 0.8618400029877489 V_s : 0.2753
H : 0.01569026725838626 V_s : 0.11103551386090817




		 */
		//These are Bill's profiles, use these for testing.
			layers[0] = LayerGenerator.createTestLayer(0.0086 ,	0.1235);
			layers[1] = LayerGenerator.createTestLayer(0.1365,	0.2787);
		
				
		if(execute == null)
			execute = new SRPE();
		if(SHOWINITIALPROGS)System.out.println(toString());
	}
	
	public SProgram(IFitnessMeasure tfm, int numFeatures, int numLayers) {
		config = Config.getInstance();
		this.numLayers = numLayers;
		fitnessStatus = false;

		// for later statistics
		genCreated = IPopulation.gen;
		timeCreated = System.currentTimeMillis() - IPopulation.time;
	}


	public SProgram(SProgram rhs) {
		fitnessStatus = rhs.fitnessStatus;
		config = rhs.config;

		//BUG for some reason this number isn't being passed in
		//numLayers = rhs.numLayers;
		numLayers = rhs.layers.length;
		//	layers=rhs.layers.clone();
		for(int i=0; i<numLayers; i++){
			layers[i]=rhs.layers[i].clone();
		}
		// for statistics
		genCreated = IPopulation.gen;
		timeCreated = System.currentTimeMillis() - IPopulation.time;
	}

	public void reinitialize(SProgram rhs) {
		fitnessStatus = rhs.fitnessStatus;
		fitness=rhs.fitness;
		config = rhs.config;

		//BUG for some reason this number isn't being passed in
		//numLayers = rhs.numLayers;
		numLayers = rhs.layers.length;
		//	layers=rhs.layers.clone();
		for(int i=0; i<numLayers; i++){
			layers[i]=rhs.layers[i].clone();
		}
		// for statistics
		genCreated = IPopulation.gen;
		timeCreated = System.currentTimeMillis() - IPopulation.time;
		
	}

	public SProgram clone() {
		SProgram s = new SProgram(this);
		return s;
	}

	/**
	 * Returns a program's overall fitness level according to its fitness
	 * measure
	 */
	public double fitness() {
		return fitness;
	}
	
	public void setFitness(double fitness){
		this.fitness=fitness;
	}

	/**
	 * Returns a reference to the correctness-status of this programs
	 * FitnessMeasure - true if it is accurate, false if it is not or is unknown
	 * (the latter is more frequent/likely).
	 */
	public boolean fitnessStatusReference() {
		return fitnessStatus;
	}


	public int randomlyCullToSize(int maxLength) {
		// TODO Auto-generated method stub
		return 1;
	}

	public void setFitnessStatus(boolean fs) {
		fitnessStatus = fs;
	}

	public int size() {
		int sum = 0;
		for(int i = 0; i < parts.length; i++){
			sum += parts[i].size();
		}
		return sum;
	}

	public String toString() {
		String s = "";
		for(int i = 0; i < layers.length; i++){
			s+= layers[i].printLayer()+"\n";
		}
		return s;
	}

	/**
	 * Simplified by pulling out the pass-through methods
	 * @param tfe
	 */
	public <F extends IFitnessCase> void updateFitness(SRP_FitnessEnvironment tfe) {
		fitnessStatus = false;
		execute.execute(tfe, this);

	}

	public double getFitness() {
		return fitness;
	}
	
	public Layer[] layers(){
		return layers;
	}

	public double[] getFreq() {
		return freq;
	}

	public double[] getCoh() {
		return coh;
	}

	public double[] getEllip() {
		return ellip;
	}

	public void setFreq(double[] freq) {
		this.freq = freq;
	}

	public void setCoh(double[] coh) {
		this.coh = coh;
	}

	public void setEllip(double[] ellip) {
		this.ellip = ellip;
	}
	
	public void writeCurve(){
		//This will be to write the best one to file
		try {
			FileWriter fstream = new FileWriter("plot."+site+".txt",false);
			BufferedWriter w = new BufferedWriter(fstream);
			w.write("Freq, Coherency\n");
			 for(int i = 0; i< freq.length-1; i++){
				 if(!Double.isNaN(coh[i])){
					 w.write(freq[i]+","+coh[i]+"\n");
				 }				 
		        }
			 w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setSite(int site) {
		this.site = site;
	}
	
	
}
