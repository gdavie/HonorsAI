package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import misc.FileReader;
import misc.Rand;
import misc.Run_Stats;
import operators.OperationGeneratorDiv;
import operators.OperationGeneratorIflt;
import operators.OperationGeneratorMinus;
import operators.OperationGeneratorMult;
import operators.OperationGeneratorPlus;
import arguments.ArgumentGeneratorConstant;
import arguments.ArgumentGeneratorFeature;
import arguments.ArgumentGeneratorRegister;
import arguments.ConstantFactoryDouble;

public abstract class Main {
	
	public static final int TRAIN_FILE=0;
	public static final int TEST_FILE=1;
	public static final int VALID_FILE=2;
	protected String trainFile;
	protected String testFile;
	protected String validFile;
	protected FitnessEnvironment<IFitnessCase> train;
	protected FitnessEnvironment<IFitnessCase> test;
	protected FitnessEnvironment<IFitnessCase> valid;
	private boolean DEBUG;
	//the statistics collected from the runs
	protected Run_Stats stats;
	String description = null;
	private IPopulation<?,?> pop;

	//the configuration
	protected Config c = Config.getInstance();
	/**
	 * This section has been simplified to allow a properties file to read in the configuration.
	 * Simplifies the parameters that need to be passed in, as well as mitigating the hard-coding problem.
	 */	
	public Main(String [] args){
		//Read properties first, it will include the base filenames we will be using.
		readProperties(args[6]);
		//Read in the command line args.
		//Would like to eventually remove the need for any parameter passing, and instead use
		//the batch file's config directly.
		description = args[0];
		c.numPieces = Integer.parseInt(args[1]);
		c.initialMinLength = Integer.parseInt(args[2]);
		c.initialMaxLength = Integer.parseInt(args[2]);
		c.maxLength = Integer.parseInt(args[2]);
		c.numRegisters = Integer.parseInt(args[3]);
		c.runs = Integer.parseInt(args[4]);			
		c.file = args[5];

		c.bestFitsFilePath += description+"_"+ c.file+"_convergence_" + c.numPieces + "_" + c.initialMinLength;
		c.runLogFilePath += description+"_" + c.file+"_accuracy_" + c.numPieces + "_" + c.initialMinLength +".csv";

		c.bpProportionElitism = 0.3;
		c.bpTournamentSize = 5;
		c.proportionElitism = 0.1;
		c.best_n = 1000;

		doSetup();
		
		for(int i = 0; i < Config.getInstance().runs; i++){
			System.out.println("RUN: " + i);
			System.gc();
			System.out.println("super.run");
			run(args);
		}
		
		Logger.logBestFits();
		printOverallStatistics();
	}
	
	public abstract void doSetup();
	
	public void doSetup(FileReader<?> fr, String fName){
		
		//SET CONFIG AND STATS
		c = Config.getInstance();
		stats = new Run_Stats(c);
		
		// FILE PATHS
		trainFile = c.dataPath+"TRAIN"+c.file+".data";
		testFile = c.dataPath+"TEST"+c.file+".data";
		validFile = c.dataPath+"VALID"+c.file+".data";
		c.popLogFilePath+=description+"_pop_"+c.numPieces+"_"+c.maxLength;
		c.statsLogFilePath+=description+"_"+c.file+"_"+c.numPieces+"_"+c.maxLength+"_log";

		// BUILD TRAIN, VALID, AND TEST ENVIRONMENTS
		train = new FitnessEnvironment<IFitnessCase>(FitnessEnvironment.TRAIN, c.numRegisters);
		train.addCasesFromFile(trainFile, fr);

		System.out.println("files have " + c.numFeatures + " features");

		test = new FitnessEnvironment<IFitnessCase>(FitnessEnvironment.TEST, c.numRegisters);
		test.addCasesFromFile(testFile, fr);

		valid = new FitnessEnvironment<IFitnessCase>(FitnessEnvironment.VALID, c.numRegisters);
		valid.addCasesFromFile(validFile, fr);

		System.out.println("Loaded " + train.numberOfCases() + " training cases and "
				+ test.numberOfCases() + " test cases and " + valid.numberOfCases() +" validation cases" +
		". Beginning evolution.");

		//UPDATE INFORMATION BASED ON FILES READ IN
		c.numTrain = train.numberOfCases();
		c.numValid = valid.numberOfCases();
		c.numTest = test.numberOfCases();
		
		System.out.println(c.numRegisters);
		//set up the stats array for logging best fits
		//Get these outputs running to the right place.
		Logger.initialize(c.logPath+description+"_"+ c.file+"_"+ c.numPieces + "_" + c.initialMinLength+"_");

		//NOTE: all of these have the same weight = 1 so all are equally likely to be generated;
		c.argumentGenerators.addElement(new ArgumentGeneratorConstant(new ConstantFactoryDouble()));
		c.argumentGenerators.addElement(new ArgumentGeneratorFeature());
		c.argumentGenerators.addElement(new ArgumentGeneratorRegister());

		c.instructionOperations.addElement(new OperationGeneratorPlus());
		c.instructionOperations.addElement(new OperationGeneratorMinus());
		c.instructionOperations.addElement(new OperationGeneratorIflt());
		c.instructionOperations.addElement(new OperationGeneratorMult());
		c.instructionOperations.addElement(new OperationGeneratorDiv());
		
	}
	
	public abstract IPopulation<?,?> create();
	
	public void run(String [] args){

		
		// Initialise the RNG:
		if(c.seedSpecified) {   Rand.Init(c.randSeed);   }
		else {   Rand.Init();   }

		pop = null;
		System.gc();//ensure we clear the space for the next one
		pop = create();

		//MAIN EVOLUTION
		long startMillis = System.currentTimeMillis();
		int generationsUsedOrNoSolution = pop.evolve(train, valid);
		long t = System.currentTimeMillis() - startMillis;
		double time = (double)t/1000;

		if(generationsUsedOrNoSolution <= c.maxGenerations) {
			System.out.println("Solution found.\nSolution is:");
		}
		else {
			System.out.println("No solution found.\nBest program:");
		}
		
		printRunStatistics(pop, time);		
	}
	
	/**
	 * Read the properties file for configuration parameters,
	 * I'm pretty sure there's a better way to parse it than a big munty switch
	 * But that's left for now
	 */
	public void readProperties(String propFilename){
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

		int bp=0;
		double prop=0.0, BPmutate=0.0, BPmutateScope=0.0, filter = 0.0;
		String path="Not set";
		int popSize=0;
		int numCache=0;
		int best_n=0;
		int fitness=0;
		int search=0;
		int maxGenerations=0;
		boolean cache=false;
		boolean reg_cache=false;
		boolean regLog=false;
		boolean log=false;
		boolean debug=false;
		boolean printbest=false;
		boolean cwd=false;
		boolean arrcache=false;
		
	if(properties.containsKey("blueprints")){
		bp = Integer.parseInt((String)properties.get("blueprints"));
		c.numBluePrints=bp;
	}if(properties.containsKey("proportion")){
		prop = Double.parseDouble((String)properties.get("proportion"));
		c.proportion=prop;
	}if(properties.containsKey("path")){
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
	}if(properties.containsKey("numCache")){
		numCache = Integer.parseInt((String)properties.get("numCache"));
		c.numCache=numCache;
	}if(properties.containsKey("CACHE")){
		cache = Boolean.parseBoolean((String)properties.get("CACHE"));
		c.CACHE=cache;
	}if(properties.containsKey("REG_CACHE")){
		reg_cache = Boolean.parseBoolean((String)properties.get("REG_CACHE"));
		c.REG_CACHE=reg_cache;
	}if(properties.containsKey("regLog")){
		regLog = Boolean.parseBoolean((String)properties.get("regLog"));
		c.regLog=regLog;
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
	}if(properties.containsKey("best_n")){
		best_n = Integer.parseInt((String)properties.get("best_n"));
		c.best_n=best_n;
	}if(properties.containsKey("BPmutate")){
		BPmutate = Double.parseDouble((String)properties.get("BPmutate"));
		c.BPmutate=BPmutate;
	}if(properties.containsKey("BPmutateScope")){
		BPmutateScope = Double.parseDouble((String)properties.get("BPmutateScope"));
		c.BPmutateScope=BPmutateScope;
	}if(properties.containsKey("filter")){
		filter = Double.parseDouble((String)properties.get("filter"));
		c.filter=filter;
	}

	if(properties.containsKey("arrcache")){
		arrcache = Boolean.parseBoolean((String)properties.get("arrcache"));
		c.arrcache=arrcache;
	}if(properties.containsKey("fitness")){
		fitness = Integer.parseInt((String)properties.get("fitness"));
		c.fitness=fitness;
	}if(properties.containsKey("search")){
		search = Integer.parseInt((String)properties.get("search"));
		c.search=search;
	}
			
	System.out.println("Pop: "+c.populationSize+"| Blueprints: "+
			c.numBluePrints+"| filter: "+c.filter+"\nSearch Proportion: "+c.proportion+"| Array cache "+arrcache+
			"\nPath: "+c.path);
	System.out.println("NumCache: "+c.numCache+" | Cache: "+
			c.CACHE+" | reg_cache: "+c.REG_CACHE+"| Debug? "+debug);
	
	System.out.println("Search: "+c.search+" | BPmutate: "+
			c.BPmutate+" | BPmutateScope: "+c.BPmutateScope+"| Fitness "+fitness);
	
	}
	
	
	
	public void printRunStatistics(IPopulation<?,?> pop, double time){
		IProgram<?> best = pop.best;
		System.out.println(best.validationFitness());
		if(Config.getInstance().maxLength < 100){
			System.out.println(best);
		}

		System.out.println("VALIDATION FITNESS: " + best.validationFitness());

		// Time is already set, generations is set as it is because if no solution is found it comes
		// back set to maxGen + 1 (to distinguish the return value from "solution found in last gen").
		// Test fitness still needs to be calculated for the best program.

		System.out.println("Calculating final statistics");

		double bestTrainingFitness = best.fitness();
		double bestVaildFitness = best.validationFitness();

		best.updateFitness(test, valid);
		double bestTestFitness = best.fitness();

		stats.updateSum(Run_Stats.bestGen, best.genCreated);
		stats.updateSum(Run_Stats.bestGenTime, best.timeCreated/1000);
		stats.updateSum(Run_Stats.trainAcc, 1-bestTrainingFitness/train.size());
		stats.updateSum(Run_Stats.testAcc, 1-bestTestFitness/test.size());
		stats.updateSum(Run_Stats.totalTime, time);
		stats.updateSum(Run_Stats.validAcc, 1-bestVaildFitness/valid.size());

		System.out.println(best.validationFitness());
	}

	public void printOverallStatistics(){
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
