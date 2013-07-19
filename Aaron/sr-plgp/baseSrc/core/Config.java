package core;

import libCPS.CPSwrapper;
import misc.WeightedCollection;
import operators.OperationGenerator;
import arguments.ArgumentGenerator;

/** An instance of this class stores all of the configuration details of a single VUWLGP run.
 T should be the type of the features and registers, P should be whichever subclass of
 IProgram is being used.*/
public class Config {
	protected static String path, cwd;
	protected static String resultsPath;
	protected static String logPath;
	protected static String dataPath;
	public static String popLogFilePath;
	public static String statsLogFilePath;
	public static String runLogFilePath;
	public static String bestFitsFilePath;
	
	private static Config instance = null;
	
	public static  Config getInstance(){
		if(instance == null){
			instance = new Config();
		}
		return (Config)instance;
	}
	public static void setPaths(boolean useCwd){

//		dataPath = path+"datasets/";
		if(useCwd){
			cwd = System.getProperty("user.dir");
			resultsPath = cwd+"/";
			logPath = cwd;
			System.out.println("results "+resultsPath);
			System.out.println("log "+logPath);
		}else{
			resultsPath = path+"Results/";
			logPath = path+"Logs/";	
			System.out.println("results "+resultsPath);
			System.out.println("log "+logPath);
		}

		dataPath = path+"datasets/";
		popLogFilePath = logPath;
		statsLogFilePath = logPath;
		runLogFilePath = resultsPath;
		bestFitsFilePath = resultsPath;
	}
	
	
	/** Should not really be copy-constructed, so the cctor is private.
	     All data members are public as they're just properties.*/
	private Config(){
		instructionOperations = new WeightedCollection<OperationGenerator>();
		argumentGenerators = new WeightedCollection<ArgumentGenerator>();
	}

	// Program/Instruction configuration parameters:
	public int numRegisters;// = 10;
	public int numFeatures;
	public int numClasses;// = 0;
	public double epsilon = 0; // If a program's fitness is <= this it is considered a perfect solution
	public int constRange = 2;
	
	//genetic operator params
	public double mutProb=0.3;
	public double crossProb=0.6;
	public double proportionElitism  = 0;//0.1;
	public int tournamentSize = 5;
	// Stores pointers to the generators of instruction operations (e.g. +, -, etc.)
	public WeightedCollection<ArgumentGenerator> argumentGenerators; // can't spec on CL
	public WeightedCollection<OperationGenerator> instructionOperations; // can't spec on CL

	// Population configuration parameters
	public int initialMinLength;// = 15;//CHANGE
	public int numPieces;// = 10;//CHANGE
	public int initialMaxLength = initialMinLength;
	public int maxLength = initialMinLength;
	public int populationSize;// = 1000;

	// Evolutionary configuration - the maximum number of times the population will be 
	// changed and then re-evaluated, excluding the initial generation and evaluation of the 
	// full population. The 1'th is the result of 1 selection and iteration, the 2'th the result
	// of 2, the maxGeneration'th the result of maxGenerations. The 0'th is logged as 'initial'

	public int maxGenerations;// = 400;
	//the number of runs to perform
	public int runs;// = 5;
	
//	protected String path="";
//	protected String resultsPath = path+"Results/";
//	protected String logPath = path+"Logs/";
//	protected String dataPath = path+"datasets/";
	// Logging parameters - statistics log file, how often should the full pop be logged, etc.
	// .txt and generation information will be postpended to the paths given for the pop and
	// stats logs, but not to the run log file path, which should be a .csv file.
	public int popLogInterval = 100000;
	public int sysoutInterval = 1;


	// Random number generation parameters - the seed value and whether or not it was specified
	public int randSeed =12345;
	public boolean seedSpecified = false;
	
	//used for determining logging level
	public boolean log = true;//Was false 19/12/11
	
	//file names
	public String file;
	
	//number of examples in training, test, and validation sets TODO horrible hack
	public int numTrain;
	public int numTest;
	public int numValid;
	
	//Caching s
	public int numCache = 0;//6;
	public boolean CACHE = false;//multiclass ie LGP
	public boolean REG_CACHE = false;//Parllel multiclass ie PLGP
	
	//CC
	public int numBluePrints = 500;
	public double bpProportionElitism  = 0;//0.3;
	public int bpTournamentSize = 0;//2;
	public int best_n = numBluePrints;//the number of blueprints to use the fitness from

	//Hashing
	public int hashRange = 17;//499979;
	public int precision = 10;//100;
	
	//hyperGP
	//public int numLabelFunctions = 10;
	//public int subX = 10; //x size of substrate
	//public int subY = 4; //y size of substrate
	
	//Std out and Debugging
	public boolean printBest = false;
	
	//PM
	public boolean alternating = false;//should we alternate between weighted and not

	//MESP
	//public double mespMutProb = 0.3;
	//public int partTournamentSize = 2;
	
	//BS PLGP
	//PSO SPECIFIC PARAMETERS
	public int PSO_ITERATIONS = 10;
	public double vel_scale = 0.2;
	public int NUM_GROUPS = 25;
	
//	public double V_W = -0.3;
//	public double L_W = 0.3;
//	public double G_W = 0.3;
	public double V_W = -0.3;//velocity weight?
	public double L_W = 0.5;///Local best weight?
	public double G_W = 0.1;//Global best weight?
	
	//HCS
	public double proportion = 0.0;
	public boolean regLog =false;
	public double BPmutate=0.0;
	public double BPmutateScope=0.0;
	public boolean arrcache =false;
	public boolean debug =false;
	public int fitness =0;
	public int search =0;
	public double filter=0.0;
	
	//SRP
	public int aperture;
	public int features;
	
	//AI-SPAC
	public double addSubRangeH;
	public double divMultRangeH;
	
	public double addSubRangeVS;
	public double divMultRangeVS;
	public double randomThicknessRange;
	public double randomVelocityRange;
	
	public int zThreshold=0;
	public boolean useThreshold = false;
	
	public CPSwrapper cps = new CPSwrapper();
}
