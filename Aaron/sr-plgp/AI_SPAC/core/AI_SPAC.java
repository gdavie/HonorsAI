package core;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import libCPS.CPSwrapper;
import misc.Rand;
import misc.Run_Stats;
import Fitness.SRP_FitnessEnvironment;

public class AI_SPAC {

	public static final int TRAIN_FILE=0;
	public static final int TEST_FILE=1;
	public static final int VALID_FILE=2;

	protected static String cohFile;
	protected static SRP_FitnessEnvironment coherencies;
	private static boolean DEBUG;
	//the statistics collected from the runs
	protected static Run_Stats stats;
	static String description = null;
	private static SRP_Population pop;

	//the configuration
	protected static Config c = Config.getInstance();

	public static void main(String [] args) {
		//Read properties first, it will include the base filenames we will be using.
		readProperties(args[6]);//properties file - SR_props.txt - need to ensure each machine has this loaclly
		//Read in the command line args.
		//Would like to eventually remove the need for any parameter passing, and instead use
		//the batch file's config directly.
		description = args[0];
		c.numPieces = Integer.parseInt(args[1]);//number o layers in each model
		//System.out.println("Layers "+c.numPieces);
		c.maxLength = Integer.parseInt(args[2]);//max number of layers per model - Aaron - bit of a hack
		c.numRegisters = Integer.parseInt(args[3]);//Need to find setting from Aaron
		c.runs = Integer.parseInt(args[4]);//Number of reps - 1
		c.file = args[5];//Data file inputs - coherency file
		c.aperture = Integer.parseInt(args[7]);//related to coherency file distance between points - equaltril triangle
		System.out.println("aperture distance : "+c.aperture);
		c.bestFitsFilePath += description+"_"+ c.file+"_convergence_" + c.numPieces + "_" + c.initialMinLength;
		c.runLogFilePath += description+"_" + c.file+"_accuracy_" + c.numPieces + "_" + c.initialMinLength +".csv";

		c.bpProportionElitism = 0.3;
		c.bpTournamentSize = 5;
		c.proportionElitism = 0.1;
		c.best_n = 1000;

		doSetup(null);

		for(int i = 0; i < Config.getInstance().runs; i++){
			System.out.println("RUN: " + i);
			System.gc();
			run(args);
		}

		Logger.logBestFits();
		printOverallStatistics();
		pop.best.writeCurve();
//		pop.programs.get(0).writeCurve();
//		System.out.println("\n\n\n\n********"+pop.programs.get(0).toString());
		System.out.println("8.6m  @	123.5m/s over 136.5m @  278.7m/s	(20m)");
	}
	/**
	 * This section has been simplified to allow a properties file to read in the configuration.
	 * Simplifies the parameters that need to be passed in, as well as mitigating the hard-coding problem.
	 */
	public AI_SPAC(String [] args){

	}

	//public abstract void doSetup();

	public static void doSetup(String fName){

		//SET CONFIG AND STATS
		c = Config.getInstance();
		stats = new Run_Stats(c);

		// FILE PATHS
		cohFile = c.dataPath+c.file;
		c.popLogFilePath+=description+"_pop_"+c.numPieces+"_"+c.maxLength;
		c.statsLogFilePath+=description+"_"+c.file+"_"+c.numPieces+"_"+c.maxLength+"_log";


		coherencies = new SRP_FitnessEnvironment(c.useThreshold, c.zThreshold);
		coherencies.addCasesFromFile(cohFile);
		CPSwrapper.setMAXSIZE(coherencies.getLstFreq().size());
		System.out.println(CPSwrapper.getMAXSIZE());
		System.out.println("Loaded "+ (coherencies.getLstCoh().size()+1) + " Frequency points. Beginning evolution.");

		//UPDATE INFORMATION BASED ON FILES READ IN
		//TODO invalidate these variables
		c.numTrain = 9999;//train.numberOfCases();
		c.numValid = 9999;//valid.numberOfCases();
		c.numTest = coherencies.getLstCoh().size();

		//set up the stats array for logging best fits
		//Get these outputs running to the right place.
		Logger.initialize(c.logPath+description+"_"+ c.file+"_"+ c.numPieces + "_" + c.initialMinLength+"_");
	}

	public static void run(String [] args){

		// Initialise the RNG:
		if(c.seedSpecified) {   Rand.Init(c.randSeed);   System.out.println("SEED STATIC "+c.randSeed);}
		else {   Rand.Init();   }//TODO hard coded seed for testing

		pop = null;
		System.gc();//ensure we clear the space for the next one
		//Calls the constructor, and adds the genetic ops
		pop = new SRP_Population();

		//MAIN EVOLUTION
		long startMillis = System.currentTimeMillis();
		int generationsUsedOrNoSolution = pop.evolve(coherencies);
		long t = System.currentTimeMillis() - startMillis;
		double time = (double)t/1000;

		if(generationsUsedOrNoSolution <= c.maxGenerations) {
			System.out.println("Solution found.\nSolution is:");
		}
		else {
			System.out.println("No solution found.\nBest program:");
		}
		System.out.println("best model : "+pop.best+"\n"+pop.best.fitness()+"\n"+time);


//		printRunStatistics(pop, time);
	}

	/**
	 * Read the properties file for configuration parameters,
	 * I'm pretty sure there's a better way to parse it than a big munty switch
	 * But that's left for now
	 */
	public static void readProperties(String propFilename){
		String propertiesFilename = propFilename;
		Properties properties = new Properties();

		FileInputStream in;
		try {
			in = new FileInputStream(propertiesFilename);
			properties.load(in);
			in.close();
		} catch (Exception e) {
			System.out.println("Error reading properties "+propertiesFilename);
			System.exit(1);
			e.printStackTrace();
		}

		String path="Not set";
		int popSize=0;
		int features=0;
		int maxGenerations=0;
		boolean log=false;
		boolean debug=false;
		boolean printbest=false;
		boolean cwd=false;
		boolean arrcache=false;
		double addSubRangeH;
		double divMultRangeH;
		double randomThicknessRange;
		double randomVelocityRange;

		double addSubRangeVS;
		double divMultRangeVS;

		int zThreshold=0;
		boolean useThreshold = true;

	if(properties.containsKey("path")){
		path = ((String)properties.get("path"));
		if(properties.containsKey("cwd")){
			cwd = Boolean.parseBoolean((String)properties.get("cwd"));
		}
		c.path=path;
		c.setPaths(cwd);
	}else{
		System.out.println("Path : "+path);
		System.exit(1);
	}if(properties.containsKey("populationSize")){
		popSize = Integer.parseInt((String)properties.get("populationSize"));
		c.populationSize =popSize;
	}if(properties.containsKey("printBest")){
		printbest = Boolean.parseBoolean((String)properties.get("printBest"));
		c.printBest=printbest;
	}if(properties.containsKey("maxGenerations")){
		maxGenerations = Integer.parseInt((String)properties.get("maxGenerations"));
		c.maxGenerations=maxGenerations;
	}if(properties.containsKey("log")){
		log = Boolean.parseBoolean((String)properties.get("log"));
		c.log=log;
	}if(properties.containsKey("debug")){
		debug = Boolean.parseBoolean((String)properties.get("debug"));
		c.debug=debug;
	}if(properties.containsKey("features")){
		features = Integer.parseInt((String)properties.get("features"));
		c.features=features;
	}if(properties.containsKey("addSubRangeH")){
		addSubRangeH = Double.parseDouble((String)properties.get("addSubRangeH"));
		c.addSubRangeH=addSubRangeH;
	}if(properties.containsKey("addSubRangeVS")){
		addSubRangeVS = Double.parseDouble((String)properties.get("addSubRangeVS"));
		c.addSubRangeVS=addSubRangeVS;
	}if(properties.containsKey("divMultRangeH")){
		divMultRangeH = Double.parseDouble((String)properties.get("divMultRangeH"));
		c.divMultRangeH=divMultRangeH;
	}if(properties.containsKey("divMultRangeVS")){
		divMultRangeVS = Double.parseDouble((String)properties.get("divMultRangeVS"));
		c.divMultRangeVS=divMultRangeVS;
	}if(properties.containsKey("randomThicknessRange")){
		randomThicknessRange = Double.parseDouble((String)properties.get("randomThicknessRange"));
		c.randomThicknessRange=randomThicknessRange;
	}if(properties.containsKey("randomVelocityRange")){
		randomVelocityRange = Double.parseDouble((String)properties.get("randomVelocityRange"));
		c.randomVelocityRange=randomVelocityRange;
	}if(properties.containsKey("zThreshold")){
		zThreshold = Integer.parseInt((String)properties.get("zThreshold"));
		c.zThreshold=zThreshold;
	}if(properties.containsKey("useThreshold")){
		useThreshold = Boolean.parseBoolean((String)properties.get("useThreshold"));
		c.useThreshold=useThreshold;
	}

	System.out.println(" Debug? "+debug + " | Log? "+ log + " | printBest? "+ printbest);
	System.out.println("Pop: "+c.populationSize+" | Max Gens: "+ maxGenerations+ "\nPath: "+c.path);
	System.out.println("randomThicknessRange :"+c.randomThicknessRange+" randomVelocityRange :"+c.randomVelocityRange);
	System.out.println("AddSubRange H :"+c.addSubRangeH+" VS :"+c.addSubRangeVS);
	System.out.println("divMultRange H :"+c.divMultRangeH+" VS :"+c.divMultRangeVS);
	System.out.println("Threshold ? "+c.useThreshold+" : "+c.zThreshold);
	}



	public static void printRunStatistics(IPopulation<?,?> pop, double time){
		IProgram<?> best = pop.best;
	//	System.out.println(best.validationFitness());
		if(Config.getInstance().maxLength < 100){
			System.out.println(best);
		}
		// Time is already set, generations is set as it is because if no solution is found it comes
		// back set to maxGen + 1 (to distinguish the return value from "solution found in last gen").
		// Test fitness still needs to be calculated for the best program.

		System.out.println("Calculating final statistics");

//		double bestTrainingFitness = best.fitness();
//		double bestVaildFitness = best.validationFitness();

//		best.updateFitness(test, test);
		double bestTestFitness = best.fitness();

		stats.updateSum(Run_Stats.bestGen, best.genCreated);
		stats.updateSum(Run_Stats.bestGenTime, best.timeCreated/1000);
	//	stats.updateSum(Run_Stats.trainAcc, 1-bestTrainingFitness/train.size());
		stats.updateSum(Run_Stats.testAcc, 1-bestTestFitness/coherencies.size());
		stats.updateSum(Run_Stats.totalTime, time);
	//	stats.updateSum(Run_Stats.validAcc, 1-bestVaildFitness/valid.size());

		System.out.println(best.validationFitness());
	}

	public static void printOverallStatistics(){
		System.out.println("Writing run statistics to: " + c.runLogFilePath);

		try {

			FileWriter fstream = new FileWriter(c.runLogFilePath,true);
			BufferedWriter fout = new BufferedWriter(fstream);

			fout.write(stats.toString());
			System.out.println(stats.header());
			System.out.println(stats.toString());
			fout.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isDEBUG() {
		return DEBUG;
	}



}
