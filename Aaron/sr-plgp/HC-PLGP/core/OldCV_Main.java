package core;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import misc.FileReader;
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

public class OldCV_Main extends Main{

String dataFile;
Random rnd;
static final int K=10;
	public OldCV_Main(String[] args) {
		super(args);
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

		doSetup();//Set up the folds here
		
		for(int i = 0; i < Config.getInstance().runs; i++){
			System.out.println("RUN: " + i);
			System.gc();
			//Now iterate through each fold
			
			doRun(args);
		
		}
		
		Logger.logBestFits();
		printOverallStatistics();
	}
	public void doRun(String[] args){
		System.out.println("Loaded " + train.numberOfCases() + " training cases and "
				+ test.numberOfCases() + " test cases and " + valid.numberOfCases() +" validation cases" +
		". Beginning evolution.");
		System.out.println("cv.run");
		//UPDATE INFORMATION BASED ON FILES READ IN
		c.numTrain = train.numberOfCases();
		c.numValid = valid.numberOfCases();
		c.numTest = test.numberOfCases();
		run(args);
	}
	
	@Override
	public void doSetup(FileReader<?> fr, String fName){
		
		//SET CONFIG AND STATS
		c = Config.getInstance();
		stats = new Run_Stats(c);
		
		// FILE PATHS
		
		dataFile = c.dataPath+c.file+"S.data";
		
		c.popLogFilePath+=description+"_pop_"+c.numPieces+"_"+c.maxLength;
		c.statsLogFilePath+=description+"_"+c.file+"_"+c.numPieces+"_"+c.maxLength+"_log";

		///////////////////////////////Cross-Validation implementation ////////////////////////////////////////
		
		//Load all cases into a master environment
		FitnessEnvironment<IFitnessCase> allFitness = new FitnessEnvironment<IFitnessCase>(FitnessEnvironment.TRAIN, c.numRegisters);
		allFitness.addCasesFromFile(dataFile, fr);
		//Set up TRAIN, VALID, AND TEST ENVIRONMENTS
		train = new FitnessEnvironment<IFitnessCase>(FitnessEnvironment.TRAIN, c.numRegisters);
		test = new FitnessEnvironment<IFitnessCase>(FitnessEnvironment.TEST, c.numRegisters);
		valid = new FitnessEnvironment<IFitnessCase>(FitnessEnvironment.VALID, c.numRegisters);
		
		/**
		 * Now I need to determine 10 folds, and they need to be deterministic.
		 * They can be shuffled before they are passesd to the fitness environments
		 */
		ArrayList<IFitnessCase>[] folds = new ArrayList[K];
		for(int i=0; i<K; i++){
			folds[i]=new ArrayList<IFitnessCase>();
		}
		
		ArrayList<IFitnessCase> lstAll = (ArrayList<IFitnessCase>) allFitness.getCases();
		int index=0;
		//Distribute the instances into the folds.
		//As they are sorted by class, this will give reasonably even distribution.
		for(IFitnessCase f : lstAll){
			test.addCase(f);
			train.addCase(f);
			valid.addCase(f);//TODO just use one case here to tick things over.
			folds[index%K].add(f);
		index++;
		}

//		test.addCase(f);
//		train.addCase(f);
//		valid.addCase(f);//TODO just use one case here to tick things over.
		//Now, shuffle the instances.
		Collections.shuffle(test.getCases());
		Collections.shuffle(train.getCases());
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		
		System.out.println("files have " + c.numFeatures + " features");


		
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

	@Override
	public IPopulation<?, ?> create() {
		// AzMod Auto-generated method stub
		return null;
	}

	@Override
	public void doSetup() {
		doSetup(new MultiClassFileReader(), null);	
	}

}
