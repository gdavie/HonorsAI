package core;

public class Settings {

	//number of layers the model should have
	private int layers = 2;

	//The range of values the genes of an individual can be created with
	//t is thickness
	private double tLow = 0.001;
	private double tHigh = 0.2;
	//sv is shear velocity
	private double svLow = 0.1;
	private double svHigh = 0.4;

	private int population = 1000;
	private int generations = 50;

	private int numberOfElites = 200;
	private int numberToCrossover = 200;
	private double chanceToCrossover = 0.5;

	private double chanceToMutate = 0.25;
	private double mutationVarianceT = 0.001;
	private double mutationVarianceSV = 0.01;

	private String hardCodedFile = "/u/students/daviegeor/4year/489/datasets/site01_20m.dat";//hardcoded value
	private int aperture = 20; //hardcoded value - distance between points - equaltril triangle


	public Settings(String[] arg){
		hardCodedFile = arg[0];
		population = Integer.parseInt(arg[1]);
		generations = Integer.parseInt(arg[2]);
		numberOfElites = Integer.parseInt(arg[3]);
		numberToCrossover = Integer.parseInt(arg[4]);
	}

	public Settings(){

	}

	public String gethardCodedFile(){
		return hardCodedFile;
	}

	public int getaperture(){
		return aperture;
	}

	public int getLayers() {
		return layers;
	}

	public double gettLow() {
		return tLow;
	}

	public double gettHigh() {
		return tHigh;
	}

	public double getSvLow() {
		return svLow;
	}

	public double getSvHigh() {
		return svHigh;
	}

	public int getPopulation() {
		return population;
	}

	public int getGenerations() {
		return generations;
	}

	public int getnumberOfElites() {
		return numberOfElites;
	}

	public int getnumberToCrossover() {
		return numberToCrossover;
	}

	public double getchanceToCrossover() {
		return chanceToCrossover;
	}


	public double getchanceToMutate(){
		return chanceToMutate;
	}

	public double getmutationVarianceT(){
		return mutationVarianceT;
	}

	public double getmutationVarianceSV(){
		return mutationVarianceSV;
	}



}
