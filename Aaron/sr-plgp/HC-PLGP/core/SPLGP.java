package core;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import misc.Stats;
import cache.ArrCache;
import cache.I_Cache;
import cache.ListCache;
import cache.Normaliser;
import dbg.DebugReader;
import dbg.RegisterEvaluator;

/**
 * 
 * This is the Slotted PLGP method.
 * The idea is to treat each factor in a Blueprint as a 'slot' which we can interchange
 * factors in and out of until we get the best blueprint.
 * 
 * 
 * @author AJ Scoble 
 *
 * @param <T> extends SPLGP
 * @param <BP> extends SPLGP Blueprint
 * @param <IPR> extends IProgram
 */
public class SPLGP<T extends SPLGP<T,BP,IPR>,BP extends SPLGP_Blueprint<BP,IPR>,IPR extends IProgram<IPR>> 
extends IPopulation<T,BP>{
	
	
	//Causes caching of instance class labels once only.
	boolean firstrun=true;
	Random generator = new Random( 19580427 );
	protected NumberFormat form = new DecimalFormat("#0.00");
	protected NumberFormat tab = new DecimalFormat("#000 ");

	protected I_Cache cached;
	//readability
	private int spSize;
	private int numSP;
	private int numBP;
	private static int gen=0;
	protected IPopulation<?,IPR>[] sub_pops;//each is a subpopulation
	private ArrayList<Integer> trainLabels, validLabels;
	boolean DEBUG;
	int SEARCH;
	final int FULL=0;
	final int FULL_RANDOM=1;
	final int RANDOM_SP=2;
	final int BPMUTATION=3;
	DebugReader h;
	RegisterEvaluator re;
	int [][] used ;
	public byte [][][] done;
	private TreeSet<Integer> toCheck;
	boolean labelsCaptured_T;
	boolean labelsCaptured_V;
	boolean arrcache;
	private int fitness;
	private int best_n;

	/** Uses the default cctor, dtor*/ 
	protected SPLGP(IPopulation<?,IPR>[] sub_pops, BlueprintFactory<BP,IPR> blueprintFactory,
			int numFeatures, int numRegisters) {
		super(blueprintFactory, Config.getInstance().numBluePrints, numFeatures, numRegisters);
		this.sub_pops = sub_pops;
		setup();
	}

	/**
	 * 
	 */
	private void setup(){
		this.DEBUG=config.debug;
		this.SEARCH=config.search;
		if(DEBUG){
			h = new DebugReader();	
			re = new RegisterEvaluator(numBP, numSP, config.maxGenerations, config.numRegisters);
		}		
		
		if(config.arrcache){
			cached = new ArrCache(config.numTrain, 	config.numValid);			
		}else{
			cached = new ListCache(config.path+"regLogs/"+config.file+"_"+config.numPieces+"_"+config.maxLength, config.regLog);
		}
		
		//Set up a few variables that are just to aid readability.
		spSize = config.populationSize / config.numPieces;
		numSP=config.numPieces;
		numBP=programs.size();
		//Initialise the cache.
		cached.initialiseCaches(programs.size(), numSP, spSize);
		trainLabels = cached.getClassNums_T();
		validLabels = cached.getClassNums_V();
		

		used = new int[numSP][spSize];
		done= new byte [numBP][numSP][spSize];
		toCheck = new TreeSet<Integer>();
		//Fitness algorithm to use.
		fitness=config.fitness;
		//number of fitnesses to check.
		best_n =(int) Math.round(numBP*config.filter);
		System.out.println("best_n "+best_n);
		
	}
	@Override
	/**
	 * Iterates through the subpopulations and performs evolutionary operations on them in turn.
	 * Once a subpop has evolved, the blueprints are iterated through and pointers to the correct
	 * factors in each subpopulation are updated.
	 * 
	 * A set has been introduced to catpure the indices of the factors that compose each blueprint.
	 * Get and set methods were necessitated by the inheritance structure from ipopulation,
	 * possible optimisation steps here.
	 */
	public Map<BP, List<BP>> iteratePopulation(double proportionElitism, int tournamentSize){
		for(int s = 0; s < sub_pops.length; s++){
			//Keep track of blueprint parts
			//Needs to be resolved before subpop is resolved
			toCheck.clear();
			setupToCheck(s);
			sub_pops[s].setUsed((Set<Integer>) toCheck.clone());
			if(DEBUG&&h.showUsed)sub_pops[s].showUsed(s);
			//Resolve the evolution of the subpopulation
			Map<IPR, List<IPR>> m2 = sub_pops[s].iteratePopulation(0, config.tournamentSize);
			//Update blueprint pointers to new generation
			for(SPLGP_Blueprint<BP,IPR> bp : programs){
				updatePointer(m2, bp.parts, sub_pops[s].programs, s);
			}
		}
		return null;
	}
	
	private void setupToCheck(int s){
		for(SPLGP_Blueprint<BP,IPR> bp : programs){
			toCheck.add(bp.selectedFactor(s));
		}
	}
	/**
	 * Updates the pointer in a blueprint to point to a new child.
	 * In SPLGP, this concept doesn't translate well, as we wish the pointers to remain
	 * pointing to the same element until they are changed.
	 * 
	 * @param mapping  Map of factors for this subpop. Elite factors have a corresponding 'list' which is their clone we will use.
	 * @param bp_parts Array of factors that make up this blueprint.
	 * @param subpop The child generation 
	 * @param s index to subpopulation number
	 * 070712 Removed int f from parameters - seemed redundant
	 */
	public static<IPR extends IProgram<IPR>> void updatePointer(Map<IPR, List<IPR>> mapping, IPR[] bp_parts, List<IPR> subpop, int s){
		List<IPR> res = mapping.get(bp_parts[s]);
		IPR prog = null;
		if(res.isEmpty()){//NOT ELITE
			//Prog is assigned a random member of the child subpopulation
			prog = subpop.get((int)(Math.random()*subpop.size()));
		}		
		else{//ELITE
			//prog is assigned the reinitialised (elite) copy to the subpopulation.
			prog = res.get(0);//size is always 1
		}
		bp_parts[s] =  prog;
	}
	/**
	 * Pretty much straight from IPopulation here
	 */
	private void checkBest(){	
		if(DEBUG&&h.traceFlow)System.out.println("Check Best---------------------------------");
		double thisGenBest = 999999;
		double thisGenBestV = 999999;
		for(int b = 0; b < size(); ++b) {
			BP bp = programs.get(b);	
			/**keeps track of the program which performs best on the validation set*/
			double tempV = bp.validationFitness();
			double tempT = bp.fitness();
			
			if(tempT<thisGenBest){
				thisGenBest=tempT;
			}
			if(tempV<thisGenBestV){
				thisGenBestV=tempV;
			}
			if(best == null || tempV < best.validationFitness()){
				System.out.println("best V fitness: " + tempV);
				best = bp.clone();
				if(config.printBest){
					System.out.println(best);
				}
			}
			bp.setFitnessStatus(true);
		}

		System.out.println("Gen "+(gen+1)+" best T :"+ thisGenBest);
		System.out.println("Gen "+(gen+1)+" best V :"+ thisGenBestV);
	}
	/** Updates the fitness of all programs which currently have false fitness-is-correct status
	 flags. After this method is called all programs in this population will have their
	 fitness values set correctly (via the checkbest method).*/
	@Override
	public <F extends IFitnessCase> void evaluateFlaggedPrograms(FitnessEnvironment<F> tfe, FitnessEnvironment<F> vfe){
		//Clear caches at beginning of each generation
		if(!firstrun)cached.initialiseCaches(numBP, sub_pops.length, spSize);
		if(DEBUG&&h.initialIndices)traceBlueprintIndices();
		for(BP bp: programs){
			bp.setFitnessStatus(false);
		}

		//Execute all factors on training set.
		long time = System.currentTimeMillis();
		twoStageEval(tfe, vfe);				
		System.out.println("Execute test TIME: " + (System.currentTimeMillis() - time)/1000.0);
		//Perform classification using difference caching
		doClassification();
		//Do the local search

		doBPSearch();

		boolean killCaches = false;//Not sure about the destruction/recreation of this, should save mem though
		if(killCaches)cached.killTrainCache();

		//Set blueprint fitness. //if(!firstrun) removed 070712
		fixBlueprintFitness();
		////////////////Validation///////////////////////////////////////////
		//Execute all factors on validation set.
		time = System.currentTimeMillis();

		partEval(vfe, false);
		//Add parts of a blueprint and check classification on validation set.
		//We only selectively execute on validation set if the factor is part of a blueprint.

		validate();
		if(killCaches)cached.killValidCache();
		System.out.println("Execute validation TIME: " + (System.currentTimeMillis() - time)/1000.0);
		/////////////////////////////////////////////////////////////////////////
		//This switch will conditionally execute a fitness function.
		//Allow comparison between them without having to hard code them
		switch(fitness){
		case 1:
			//Uses FC
			mapFitness1();
			break;
		case 2:
			//Uses FC and factor filtering on inactive factors
			mapFitness2();
			break;
		case 3:
			//not fully defined yet
			mapFitness3();
			break; 
		default:
			//Standard fitness function
			mapFitness();	
		}

		checkBest();
		if(re!=null){
			if(h.showSingleBPRegs||h.showGenNR)re.finishGen();//Increments generation counter			
		}
		gen++;
	}

	public <F extends IFitnessCase>void twoStageEval(FitnessEnvironment<F> tfe, FitnessEnvironment<F> vfe){
		for(BP prog : programs){
			prog.zeroFitness();
			/**
			 * This will get called for each blueprint.
			 * Each BP then calls it per subpop / slot 
			 * So it is inside that method that we need to cappture
			 * the figures, and add up the register access for each subpop.
			 */
			//AzM we will now have all registers, for all subpops for a single blueprint
			if(h!=null&&h.showGenNR){
				System.out.println("re introns");
				prog.markIntrons(re);
			}else{
				
				prog.markIntrons();
			}

			//re.printSingleBP();
			//We will add them across as blueprints.
		}
		//All BPs have been iterated through, now advance the gen counter
		if(re!=null){
			re.finishGen();
			if(h.showSingleBPRegs)re.printBPregs();//Broken counting
			if(h.showGenNR)re.printGenTotals();
			//re.setBlueprintRegs(new int[numSP][config.numRegisters]);
			re.clearStats();
		}
		partEval(tfe, true);
	}

	/** this method offers increased execution speed by evaluating all program parts,
	 * caching the results, then using the cached values to compute combinations. In order to 
	 * do this in a memory friendly manner, we reverse the order of evaluation. In other words
	 * we evaluate all programs on each training example, rather than all training examples on
	 * each program.
	 */
	public <F extends IFitnessCase>void partEval(FitnessEnvironment<F> fe, boolean train){	
		markSubIntrons();
		if (!fe.loadFirstCase()) {
			throw new RuntimeException("No fitness cases in fe to evaluate against");
		}
		int count = 0;
		do {
			//execute all parts on this training example
			evalParts(fe, count, train);
	
			count++;
		} while (fe.loadNextCase());
		//070712 No point using a call to cache for this
		if(train){
			labelsCaptured_T=true;//Need to capture class labels only once per experiment
			//cached.setLabelsCaptured_T(true);.			
		}else{
			labelsCaptured_V=true;//Need to capture class labels only once per experiment.
			//cached.setLabelsCaptured_V(true);
		}
	}

	/**
	 * Could extend this to check when we are doing validation.
	 * If we only cycle through the factors that have been selected, we may save a considerable amount of time.
	 * @param fe
	 * @param caseNum
	 * @param train
	 */
	public <F extends IFitnessCase>void evalParts(FitnessEnvironment<F> fe, int caseNum, boolean train){
		
		MultiClassFitnessCase mcfc = ((core.MultiClassFitnessCase)fe.getCases().get(caseNum));
		//Add instance to class cache. Do not perform if class labels are already captured.
		if(train&&!labelsCaptured_T){
				cached.addClassNum_T(mcfc.classNumber);	
		}else if (!train&&!labelsCaptured_V){//Validation
				cached.addClassNum_V(mcfc.classNumber);	
		}
		RegisterCollection rc = null;
		int s=0;//Logging ID		
		for(IPopulation<?,IPR> thisSubPop: sub_pops){
			//First time around, we need to set up the list of factors to check.
			if(firstrun)setupToCheck(s);
			int f=0;//Logging ID
			for(IPR factor : thisSubPop.programs){
				rc = train ? factor.trainFinalRegisterValues : factor.validFinalRegisterValues;
				//note: zeroed registers
				rc.zeroRegisters();
				fe.zeroRegisters();
				//results after execution will now be stored in the programs final register values
//AzMod conditional here				if(train||toCheck.contains(f)){//execute on training set, or on validation only if part of a bp
				factor.execute(fe, rc, caseNum);
//				}
				//Add execution results to cache - either to training or validation cache
				if(train){
					cached.addInstanceRC_T(rc, s, f, caseNum);
				}else if(!train){
					cached.addInstanceRC_V(rc, s, f, caseNum);
				}		
				f++;
			}//End iterate programs
			s++;
		}//END iterate subpops
	}
	/**
	 * Need to assign validation fitnesses.
	 * Simply add the bp registers and check against instance labels.
	 * We cycle through the blueprints that have already been constructed,
	 * so validation is simply running a complete program vs the instances
	 * and assigning the validation fitness to the whole BP.
	 */
	private void validate(){
		if(DEBUG&&h.traceFlow)System.out.println("Validate---------------------------------");
		BP thisBlueprint = null;
		RegisterCollection thisBpRC = null;	
		//Can iterate through blueprints on outer for this job
		for(int b=0; b < numBP; b++){//iterate through blueprints
			thisBlueprint = programs.get(b);
			thisBlueprint.validFitnessMeasure.zeroFitness();
			for(int i=0; i <config.numValid;i++){//go through all instances
				thisBpRC= getBPVector_V(thisBlueprint, i);
				int classif = thisBpRC.largestRegisterIndex();
				if(classif!= validLabels.get(i)){// incorrect classification
					thisBlueprint.validFitnessMeasure.fitness++;
				}
			}
		}
	}
	/**
	 * We need to get the register values for the blueprint against each instance
	 * separately, not just from the final values.
	 * @param bp
	 * @param instance
	 * COST?
	 */
	private RegisterCollection getBPVector_T(BP bp, int instance){
		//Commented out due to console spamming -> maybe need later
		//if(DEBUG&&h.traceFlow)System.out.println("Get BP Vector T---------------------------------");		
		RegisterCollection blueRC = bp.trainFinalRegisterValues;
		RegisterCollection	partRC = null;
		blueRC.zeroRegisters();
		for(int sp = 0; sp < numSP; sp++){
				int f = bp.selectedFactor(sp);
				partRC = cached.rc_T(sp, f, instance);
				blueRC.add(partRC);
		}//Now I have the total output vector for the blueprint.
		return blueRC;
	}
	
	/**
	 * We need to get the register values for the blueprint against each instance
	 * separately, not just from the final values.
	 * @param bp
	 * @param instance
	 * COST?
	 */
	private RegisterCollection getBPVector_V(BP bp, int instance){
		//Commented out due to console spamming -> maybe need later
		//if(DEBUG&&h.traceFlow)System.out.println("Get BP Vector V---------------------------------");
		RegisterCollection blueRC = bp.validFinalRegisterValues;
		RegisterCollection 		partRC = null;
		blueRC.zeroRegisters();
		for(int sp = 0; sp < numSP; sp++){
				int f = bp.selectedFactor(sp);
				partRC = cached.rc_v(sp, f, instance);
				blueRC.add(partRC);
			}//Now I have the total output vector for the blueprint.
		return blueRC;
	}
	/**
	 * This method will iterate through the blueprints.
	 * First we create a list of blueprints and their output registers.
	 * Then we iterate through every training instance.
	 * For each blueprint, we go through each 'slot'(subpopulation)
	 * Subtract existing factor from that location
	 * go through every other factor for that location, in turn adding that vector
	 * For each factor, we then check against the training example and increment the value for that combination
	 * if the result is incorrect.
	 * This method makes no attempt to evaluate any combinations, it only performs subtraction
	 * of one factor and addition of the other, compares highest register value against class label.
	 * For every instance, we iterate through blueprints -> subpops -> subtract old add new -> all factors
	 * The otside loop is instances, we could do the loop theother way, but figured this would be more efficient **check**
	 * 
	 * time  i*3b*5s*(f*proportion*9)+10 = 135*i*b*s*(f*proportion)+10 = 135*i*b*(population*proportion)+10
	 */
	private void doClassification(){
									if(DEBUG&&h.traceFlow)System.out.println("doClassification---------------------------------");
									if(DEBUG&&h.checkClassifCache)traceClassifCache("BEFORE");

		long time = System.currentTimeMillis();
									if(DEBUG&&h.showNumberOfInstanceRegisters){
										System.out.println("labels : "+trainLabels.size());	
									}
		//Initialise variables
		RegisterCollection thisBpRC = null, outputToReplace=null, outputToTry=null,tmpRC=null;
		BP thisBlueprint = null;

		boolean fRun=true;
		//************************START LOOP*****************************************************
		for(int i=0; i <trainLabels.size();i++){//go through all instances
			for(int b=0; b < numBP; b++){//iterate through blueprints
				thisBlueprint = programs.get(b);
				//We need an output RC for each instance here.
				
				thisBpRC= getBPVector_T(thisBlueprint, i);
									if(DEBUG&&h.showBlueprintComponentFitness2&&thisBpRC!=null)traceBlueprintComponentFitness(b, thisBpRC);//Verbose output	
				
				for(int s =0; s < numSP; s ++){//iterate through subpopulations	
					int factor = thisBlueprint.selectedFactor(s);
					outputToReplace=cached.rc_T(s, factor, i);
					for(int f=0; f < spSize; f++){//iterate through factors
								//Shortened output - assumed we don't need to confirm instance 0 only.
									if(DEBUG&&h.traceCacheIndices&&fRun)System.out.print("b "+b+" s "+s+" f "+f+" | ");
							outputToTry = cached.rc_T(s, f, i);//obtain it's fitness
							outputToTry.sub(outputToReplace);//Get new-old=difference
							tmpRC = thisBpRC.clone();//Copy original BP output vector				
							tmpRC.add(outputToTry);//Add the difference.
							int classif = tmpRC.largestRegisterIndex();
							if(classif!= trainLabels.get(i)){// incorrect classification
							cached.fitness[b][s][f]++;	
							cached.facFit[s][f]++;
							}else{
								//Correct classification
							}
							tmpRC=null;//Kill the reference - avoid clone
					}//END iteration through factors	
				}//end iteration through subpops
									if(DEBUG&&h.traceCacheIndices&&fRun)System.out.println();
			}//end iteration through blueprints	
			if(fRun)fRun=false;
		}//end iteration through instances	
		if(firstrun){
			firstrun=false;
		}else{
			fixBlueprintFitness();//iterate through blueprints, assigning fitness equal to number of mis-classifications of their components
		}
		System.out.println("BP CLASSIFICATION TIME: " + (System.currentTimeMillis() - time)/1000.0);
		//////////////////////Check cached fitness //////////////////////
									if(DEBUG&&h.checkClassifCache)traceClassifCache("AFTER");
	}
	
	/**
	 * Haven't really started to implement this yet,
	 * Is a copy of factor to search method.
	 * The idea is that maybe we can choose how many subpopulations we will
	 * search through, ie low fitness = lots of changes, high fitness = few.
	 * @param spts
	 * @return
	 */
	private int[] buildSubpopSearch(int spts){
		int[]tmp = new int[spts];
		ArrayList<Integer> chosen = new ArrayList<Integer>();

		if(spts==numSP){//Iterate through and add each factor
			for(int i=0; i<numSP; i++){
				tmp[i]=i;
			}
		}else{//Add random factors to fill the array we will examine
			for(int i=0; i<spts; i++){
				//Here we ensure that the same factor is not added to the array twice
				boolean dn = false;
				int pick =0;
				while(!dn){
					pick = (int) (generator.nextDouble()*numSP);
					if(!chosen.contains(pick))dn=true;
				}
				tmp[i]= pick;
				chosen.add(pick);
			}
		}
		return tmp;
	}
	/**
	 * Iterate through array and add all factors to it for full search,
	 * or add a number of random factors for proportional search.
	 * 
	 * Note that if we return to conditional classification, i.e. we don't need 
	 * that info for fitness information, we can use a global array here and define it at the beginning 
	 * of each generation for each blueprint.
	 * @return
	 */
	private int[] buildFactorSearch(int fts){
		int[]tmp = new int[fts];
		ArrayList<Integer> chosen = new ArrayList<Integer>();

		if(fts==spSize){//Iterate through and add each factor
			for(int i=0; i<spSize; i++){
				tmp[i]=i;
			}
		}else{//Add random factors to fill the array we will examine
			for(int i=0; i<fts; i++){
				//Here we ensure that the same factor is not added to the array twice
				boolean dn = false;
				int pick =0;
				while(!dn){
					pick = (int) (generator.nextDouble()*spSize);
					if(!chosen.contains(pick))dn=true;
				}
				tmp[i]= pick;
				chosen.add(pick);
			}
		}
		return tmp;
	}
	/**
	 * Here we create the sets of factors and subpopulations to search.
	 * We can move this call to be applied before the classification stage if
	 * we decide that we don't need the additional fitness information.
	 * This is a tradeoff between information and speed, and is best decided 
	 * by experimental means.
	 * 
	 * Maybe overkill but shouldn't use much time
	 */
	
	/*
	 * 	int SEARCH;
	final int FULL=0;
	final int FULL_RANDOM=1;
	final int RANDOM_SP=2;
	final int BPMUTATION=3;
	 */
	private SearchSet createSearchSet(){
		//////////////////////////////////////////////////////////////////////
		//Define the search we will use and initialise array variables.

		double prop = config.proportion;//proportional search
		double BPmutate = config.BPmutate;//chance of mutation
		double BPscope = config.BPmutateScope;//scope of mutation
		int THIS_SEARCH=0;//THis will be used to define search algorithm in BPMutation
//		int numSPtoSearch = 0;//Number of subpopulations to explore
		int numFactoSearch = 0;//Number of factors to explore
		
		//If we are using BPMUTATION, we need to define the search algorithm
		if(SEARCH==BPMUTATION){//FULL with BPmutation
			//1. Check whether mutation or full search
			int mutate = (int) (generator.nextDouble()*numSP);
			
			if(mutate<BPmutate){//1.a. mutation occurs
				//2. Check micro or macro mutation
				int scope = (int) (generator.nextDouble()*numSP);
				if (scope<BPscope) {//2.a. micro mutation -- RANDOM_SP
					//3. Assign THIS_SEARCH
					THIS_SEARCH=RANDOM_SP;
							if(DEBUG&&h.showsearchtype)System.out.println("RANDOM_SP");
				}else{//2.b. macro mutation -- FULL_RANDOM
					//3. Assign THIS_SEARCH
					THIS_SEARCH=FULL_RANDOM;
							if(DEBUG&&h.showsearchtype)System.out.println("FULL_RANDOM");
				}
			}else{//1.b. No mutation. FULL Search
				//3. Assign THIS_SEARCH
				THIS_SEARCH=FULL;
							if(DEBUG&&h.showsearchtype)System.out.println("FULL");
			}
		}else{//In all other cases, the search algorithm was passed in as a parameter.
			THIS_SEARCH=SEARCH;
		}

		//////////////Search algorithm has been defined here //////////////////////
		//Now set up the number of subpops and factors we will search.
		switch(THIS_SEARCH){
		case FULL_RANDOM://Random SP, random factor 1
//			numSPtoSearch =1;
			numFactoSearch=1;
			break;
		case RANDOM_SP://Random SP, search factor 2
//			numSPtoSearch =1;
			if(prop==1){//Not sure whether prop=1 would round up exactly
				numFactoSearch=spSize;
			}else{
				numFactoSearch = (int) Math.round(spSize*prop);
			}
			break;

		default://FULL 0
//			numSPtoSearch = numSP;
			if(prop==1){//Not sure whether prop=1 would round up exactly
				numFactoSearch=spSize;
			}else{
				numFactoSearch = (int) Math.round(spSize*prop);
			}
			break;
		}

//		SearchSet searchSet = new SearchSet(numSPtoSearch, numFactoSearch);
		SearchSet searchSet = new SearchSet(numSP, numFactoSearch);
		//This switch selects the subpopulations we will search
		switch(THIS_SEARCH){
		case FULL_RANDOM://Random SP, random factor
			//single randomly chosen subpop
			searchSet.subpopsToSearch[(int) (generator.nextDouble()*numSP)] = 1;
			break;
		case RANDOM_SP://Random SP, search factor
			//single randomly chosen subpop
			searchSet.subpopsToSearch[(int) (generator.nextDouble()*numSP)] = 1;
			break;
		case 3://Undefined
				System.out.println("error, this should not get called");
			break;
		default://FULL
			//All subpops
			for(int i=0; i<numSP; i++){
				searchSet.subpopsToSearch[i]=1;
			}
			break;
		}
		//Go through the subpopulations and create a set of factors to search for each
		for(int s=0; s< searchSet.subpopsToSearch.length; s++){
			if(THIS_SEARCH==FULL_RANDOM){//single randomly chosen subpop
				searchSet.factorsToSearch[s][0] = (int) (generator.nextDouble()*numSP);
			}else{//All factors	
				searchSet.factorsToSearch[s] =buildFactorSearch(numFactoSearch);
			}
		}
//		System.out.println("check array adds");
//		for(int s = 0; s< numSPtoSearch; s++){
//			for (int f=0; f< numFactoSearch; f++){
//				System.out.print(searchSet.factorsToSearch[s][f]+" ");
//			}
//			System.out.println();
//		}
//		System.out.println("________________");
		return searchSet;
	}
	 /**
	  * Search sets are mostly invalid now that we know that the FC is so useful
	  * @author az
	  *
	  */
	public class SearchSet{
		int[] subpopsToSearch;
		int[][] factorsToSearch;
		
		public SearchSet(int numSPtoSearch, int numFactoSearch){
			this.subpopsToSearch = new int[numSPtoSearch];
			this.factorsToSearch = new int[numSPtoSearch][numFactoSearch];
		}

	}
	/**
	 * This is the hill climbing part.
	 * All blueprints have been executed over all instances, giving a number of errors.
	 * Now we go through all the combinations, check their fitness values, and then select 
	 * the single best change to make to each blueprint.
	 * time 6b*4(8)s*(f*proportion)+1 = 24(48)*b*(population*proportion)+1
	 */
	private void doBPSearch(){

		int changes =0;
								if(DEBUG&&h.traceFlow)System.out.println("doBpSearch-------------------------");
		long time = System.currentTimeMillis();
		BP thisBluePrint = null;
		//Blueprints	
		for(int b=0; b<numBP ;b ++){//iterate through blueprints
								if(DEBUG&&h.showSearchHits)System.out.println("Blueprint "+b);
			boolean change = false;
			//Initialise
			thisBluePrint=programs.get(b);
			int overallBestS=-1, overallBestF=-1, subpopBestF =-1;//negative indices for tracing no change
			double bpOriginalFitness = thisBluePrint.fitness();//Blueprint fitness is sum of incorrect classifications of its components.
			double overallBestFitness = thisBluePrint.fitness();//Ensure only improvements are considered	
			if(DEBUG&&h.checkInitialBPfitness)traceInitialBPfitness(b,bpOriginalFitness,thisBluePrint);	
			double subpopOriginalFitness = bpOriginalFitness;
			double subpopBestFitness = bpOriginalFitness;//original fitness reset
				
			//Create a list of subpops to search.
			//This currently is defined as either all of them, 
			//or a single subpop.
			SearchSet search =createSearchSet();

//			for (int sIndex=0; sIndex<search.subpopsToSearch.length; sIndex++){//iterate through each subpop	
			for (int sIndex=0; sIndex<numSP; sIndex++){//iterate through each subpop	
	//			System.out.println("sp "+sIndex);
				/**
				 * TODO s is currently defined as sIndex.
				 * This works ok for now, when the set of subpopulations is either
				 * a single subpopulation or all of them. If we have a set of
				 * non-consecutive subpops we will need to revieew this indexing.
				 */			
				int execute=search.subpopsToSearch[sIndex];
				int s = sIndex;
				if(execute!=1)continue;
//				System.out.println("X"+execute+" "+s);


								if(DEBUG&&h.traceBestSearch)System.out.println("\nSubpop "+s);
				int fac_index = thisBluePrint.selectedFactor(s);
				double existingFactorFitness = cached.fitness[b][s][fac_index];
				double thisSubPopLockedFitnessValue = subpopOriginalFitness-existingFactorFitness;//Blueprint locked other factors
								if(DEBUG&&h.traceBestSearch)traceBestSearch(b, existingFactorFitness, thisSubPopLockedFitnessValue, subpopBestFitness);
				//Factors
								if(DEBUG&&h.showSearchHits)System.out.println("subpop "+s);
								if(DEBUG&&h.showSearchHits)System.out.print("factor ");
				for (int fIndex=0; fIndex<search.factorsToSearch[s].length; fIndex++){//iterate through each subpop
					int f = search.factorsToSearch[s][fIndex];
								if(DEBUG&&h.showSearchHits)System.out.print(f+" ");
								if(DEBUG&&h.traceCacheIndices)System.out.print(b+" "+s+" "+f+"_"+cached.fitness[b][s][f]+" | ");
					double thisFactorFitness = (cached.fitness[b][s][f]);//fitness by this blueprint
					double thisCombinationFitness=thisFactorFitness+thisSubPopLockedFitnessValue;//add this factor to locked subpops
								if(DEBUG&&h.traceBestSearch)System.out.println(b+" "+s+" "+f+" this "+
										thisFactorFitness+ " Comb "+thisCombinationFitness);
					if(thisCombinationFitness<subpopBestFitness){//best for this subpopulation
						subpopBestFitness=thisCombinationFitness;
						subpopBestF = f;
								if(DEBUG&&h.traceBestSearch)System.out.println("\nBest so far <<"+subpopBestFitness+">>"+" "+s+"("+f+")");
					}//ENDIF	
				}//END iterate through factors	
				if(subpopBestFitness<overallBestFitness){//record best overall - possibly no changes in this subpop are better
					overallBestS=s;
					overallBestF=subpopBestF;
					overallBestFitness=subpopBestFitness;
					change=true;
					changes++;
				}
								if(DEBUG&&h.traceBestSearch)System.out.print("SP Best Found "+overallBestS+"("+subpopBestF+") "+subpopBestFitness+" ");
								if(DEBUG&&h.showSearchHits)System.out.println();
			}//END iterate through subpops				
			//want to extract the best fitness here,and modify that factor (only) before we progress. 
								if(DEBUG&&h.checkInitialBPfitness){
				System.out.print("best change ("+overallBestS+") "+overallBestF+ " "+overallBestFitness);
				if(overallBestS>-1&&overallBestF>-1){
					System.out.println("("+ cached.fitness[b][overallBestS][overallBestF]+")");
				}else{
					System.out.println();
				}
			}
			if(change){//Only replace if it is better than existing solution
				//Eclipse warning null here is BS I've tested it.
				thisBluePrint.setFitness(overallBestFitness);
				thisBluePrint.parts[overallBestS]=sub_pops[overallBestS].get(overallBestF);	
				thisBluePrint.setSelectedFactor(overallBestS, overallBestF);
			}
			thisBluePrint = null;
								if(DEBUG&&h.traceCacheIndices)System.out.println();
		}//END iterate through blueprints.	
		System.out.println("BP SEARCH TIME: " + (System.currentTimeMillis() - time)/1000.0);
		//AzL Check convergence on factors///////////////////////////////////////////////////////////
								if(DEBUG&&h.consideredChanges)traceConvergence(changes);	
								if(DEBUG&&h.factorUsedCount){
			used = new int[numSP][spSize];
			//Count number of times each factor has been selected
			for(int b=0; b<numBP; b++){
				for(int s=0;s<numSP;s++){
					used[s][programs.get(b).selectedFactor(s)]++;
				}
			}
		}
								if(DEBUG&&h.factorUsedCount)traceFactorUsage();
	}

	/**
	 * Capture the total number of errors from the classification phase
	 * We iterate through each BP and also it's subpop, collecting the 
	 * fitness values of the component factors.
	 * time 4b*3s = 12*b*s
	 */
	private void fixBlueprintFitness(){
		if(DEBUG&&h.traceFlow)System.out.println("FBF--------------------------");
		for(int b=0; b<numBP;b++){//iterate through blueprints
			int bpFitness=0;
			BP thisBluePrint = programs.get(b);
			thisBluePrint.setFitness(0);
			for(int s=0; s< numSP;s++){//iterate through subpops
				int f= thisBluePrint.selectedFactor(s);
				if(DEBUG&&h.checkFBF)System.out.print("BP "+b+" sub "+s+" fac "+f+ " (Cached) "+
						cached.fitness[b][s][f]);
				int factorFitness = cached.fitness[b][s][f];
				bpFitness +=factorFitness;
				if(DEBUG&&h.checkFBF)System.out.println();
			}//END iterate through subpops
			thisBluePrint.setFitness(bpFitness/numSP);//Need to moderate fitness AzH
			if(DEBUG&&h.checkFBF)System.out.println("(Stored) "+thisBluePrint.fitness());
			thisBluePrint=null;//kill reference	
		}//END iterate through blueprints	
	}

	/**
	 * This is the inherited (original) fitness function.
	 * With a large number of factors that will not be included in a reduced 
	 * set of blueprints, the NFI fitness may not be very useful.
	 */
	private void mapFitness(){
		if(DEBUG)System.out.println("f0");
		if(DEBUG&&h.traceFlow)System.out.println("Map Fitness---------------------------------");
		int[] avg=new int [numSP];
		int[] numInactive=new int [numSP];
		Stats train_avg = new Stats();
		Stats valid_avg = new Stats();

		Stats train_avg_pop = new Stats();
		Stats valid_avg_pop = new Stats();
		
		Stats train_not_avg_pop = new Stats();
		Stats valid_not_avg_pop = new Stats();
		// set up the mapping
		Map<IPR,List<Double>> train_fits = new HashMap<IPR,List<Double>>();
		Map<IPR,List<Double>> valid_fits = new HashMap<IPR,List<Double>>();
		for(int i = 0; i < sub_pops.length; i++){
			for(int j = 0; j < sub_pops[i].size(); j++){
				train_fits.put(sub_pops[i].get(j), new ArrayList<Double>());
				valid_fits.put(sub_pops[i].get(j), new ArrayList<Double>());
			}
		}

		//store the appropriate values
		for(int b = 0; b < programs.size(); b++){
			SPLGP_Blueprint<BP,IPR> blueprint = programs.get(b);

			for(IPR factor: blueprint.parts){			
				train_fits.get(factor).add(blueprint.trainFitnessMeasure.fitness);				
				valid_fits.get(factor).add(blueprint.validFitnessMeasure.fitness);				
			}
			train_avg.update(blueprint.trainFitnessMeasure.fitness);
			valid_avg.update(blueprint.validFitnessMeasure.fitness);
		}
		
		
		for(int s = 0; s < sub_pops.length; s++){
			IPopulation<?,IPR> pop = sub_pops[s];
			for(int f = 0; f < pop.size(); f++){
				IPR thisFactor = pop.programs.get(f);
				List<Double> train_l = train_fits.get(thisFactor);
				List<Double> valid_l = valid_fits.get(thisFactor);

				Collections.sort(train_l);
				Collections.reverse(train_l);
				Collections.sort(valid_l);
				Collections.reverse(valid_l);				
				
				if(train_l.size() == 0 || valid_l.size() == 0){
					avg[s]++;
					thisFactor.trainFitnessMeasure.fitness = train_avg.mean;
					thisFactor.validFitnessMeasure.fitness = valid_avg.mean;
					train_avg_pop.update(thisFactor.trainFitnessMeasure.fitness);
					valid_avg_pop.update(thisFactor.validFitnessMeasure.fitness);
					numInactive[s]++;
				}
				else{
					Stats t = new Stats(), v = new Stats();
					for(int j = 0; j < this.best_n && j < train_l.size();j++){
						t.update(train_l.get(j));
					}for(int j = 0; j < this.best_n && j < valid_l.size();j++){//DO NOT assume same size of train and valid lists
						v.update(valid_l.get(j));
					}									
					thisFactor.trainFitnessMeasure.fitness = t.mean;
					thisFactor.validFitnessMeasure.fitness = v.mean;
					train_not_avg_pop.update(thisFactor.trainFitnessMeasure.fitness);
					valid_not_avg_pop.update(thisFactor.validFitnessMeasure.fitness);
				}
			}
		}
		if(DEBUG&&h.showFitnessAverages){
			System.out.println("train Avg "+train_avg.mean);
			System.out.println("valid avg "+valid_avg.mean);
			System.out.println("train Avg_pop "+train_avg_pop.mean);
			System.out.println("valid avg_pop "+valid_avg_pop.mean);
			System.out.println("train not_Avg_pop "+train_not_avg_pop.mean);
			System.out.println("valid not_avg_pop "+valid_not_avg_pop.mean);
			
			for(int i=0; i<numSP; i++){
				System.out.print("NFI subpop "+i+": ");
				System.out.print(avg[i]+" ");
			}
		System.out.println();
		}
		//Print count of factors assigned average fitness information
		if(DEBUG&&h.showInactive)traceInactive(numInactive);
		//Print factor fitness as they have been assigned.
		if(DEBUG&&h.showAllFitnesses)traceAllFitnesses();
	}
	/**
	 * Here we collect fitnesses from the FC.
	 * We could use factor filtering ie best n for active factors,
	 * and use all fitnesses for those that are not active.
	 * We have already performed validation at this point, 
	 * so we may not need to do any validation fitness work.
	 * The point being whether factor evolution relies on validation fitness at all,
	 * as we have now defined validation fitness as that of the whole blueprint.
	 * 
	 * The idea is that doBPsearch will assign fitness to BPS
	 * and this will assign fitness to factors
	 */
	private void mapFitness1(){
		if(DEBUG)System.out.println("f1");
		if(DEBUG&&h.traceFlow)System.out.println("Map Fitness 1---------------------------------");
		int[] numInactive=new int [numSP];
		Stats all_fits = new Stats();
		Stats allClassif = new Stats();

		Stats [] train_avg_spop = new Stats[numSP];
		for(int init=0; init <numSP; init++){
			train_avg_spop[init]= new Stats();
		}

		Stats inactive = new Stats();
		Stats active = new Stats();
		// set up the mapping
		Map<IPR,List<Double>> train_fits = new HashMap<IPR,List<Double>>();
		
		for(int s = 0; s < numSP; s++){
			for(int f = 0; f < spSize; f++){
				train_fits.put(sub_pops[s].get(f), new ArrayList<Double>());
			}
		}	
		/*
		 * Here we iterate through the factors and add all classifications they have made, ie
		 * they will have made one classification per blueprint per instance.
		 * We add these fitnesses to the ArrayList so we can filter the best n if we wish.
		 */
		for(int s = 0; s < sub_pops.length; s++){
			IPopulation<?,IPR> pop = sub_pops[s];

			for(int f = 0; f < pop.size(); f++){
				IPR thisFactor = pop.programs.get(f);
				for(int b=0; b<numBP; b++){
					train_fits.get(thisFactor).add((double) cached.fitness[b][s][f]);	
					all_fits.update(cached.fitness[b][s][f]);
				}
			}
		}
		
		for(int s = 0; s < sub_pops.length; s++){
			IPopulation<?,IPR> pop = sub_pops[s];
			for(int f = 0; f < spSize; f++){
				IPR thisFactor = pop.programs.get(f);
				List<Double> train_l = train_fits.get(thisFactor);

				Collections.sort(train_l);
				Collections.reverse(train_l);
				//This will never be empty

				if(pop.getUsed()!=null&&!pop.getUsed().contains((Integer)f)){
					//Average out all fitnesses
					for(int j = 0; j < train_l.size();j++){
						allClassif.update(train_l.get(j));
					}
					numInactive[s]++;	
					thisFactor.trainFitnessMeasure.fitness = allClassif.mean;	
					//update fitness for inactive factors
					inactive.update(thisFactor.trainFitnessMeasure.fitness);
					//Add fitness to subpopulation fitness
					train_avg_spop[s].update(thisFactor.trainFitnessMeasure.fitness);
				}
				//This factor has contributed to blueprints, use best fitnesses only
				else{
					Stats best_n = new Stats();
					//Add training fitnesses from blueprints it contributes to
					for(int j = 0; j < this.best_n && j < train_l.size();j++){
						best_n.update(train_l.get(j));
					}
					//Average of the top n fitnesses
					thisFactor.trainFitnessMeasure.fitness = best_n.mean;					
					//update fitness for active factors
					active.update(thisFactor.trainFitnessMeasure.fitness);
					//Add fitness to subpopulation fitness
					train_avg_spop[s].update(thisFactor.trainFitnessMeasure.fitness);
				}
			}
		}
		if(DEBUG&&h.showFitnessAverages)traceFitnessAverages(all_fits, inactive, active, train_avg_spop);
		//Print count of factors assigned average fitness information
		if(DEBUG&&h.showInactive)traceInactive(numInactive);
		//Print factor fitness as they have been assigned.
		if(DEBUG&&h.showAllFitnesses)traceAllFitnesses();

	}
	
	/**
	 *This FF uses factor filtering, but in the inactive population.
	 *Reasoning for this is that the best factors for cooperation have already been chosen
	 *for BPs, and with the current number of blueprints, they 'appear' (informal observation)
	 *to only contribute to 1-2 blueprints; whereas all factors have been executed as part
	 *of all blueprints.
	 *
	 *So now the FF is defined as follows:
	 *inactive factors; fitness is defined from the best_n combinations it has been evaluated on.
	 *active factors : fitness is defined from all combinations it has been evaluated on.
	 *
	 *The reason for this is that we wish to encourage inactive factors to be involved in 
	 *evolution, trying to gently move away from hurdle lock where all the best solutions stay
	 *as they are and no good new ones are developed.
	 *
	 *NOTE: print output and variable names may be a bit transposed here
	 */
	private void mapFitness2(){
		if(DEBUG)System.out.println("f2");
		if(DEBUG&&h.traceFlow)System.out.println("Map Fitness 2---------------------------------");
		int[] numInactive=new int [numSP];
		Stats all_fits = new Stats();
		Stats allClassif = new Stats();

		Stats [] train_avg_spop = new Stats[numSP];
		for(int init=0; init <numSP; init++){
			train_avg_spop[init]= new Stats();
		}

		Stats inactive = new Stats();
		Stats active = new Stats();
		// set up the mapping
		Map<IPR,List<Double>> train_fits = new HashMap<IPR,List<Double>>();
		
		for(int s = 0; s < numSP; s++){
			for(int f = 0; f < spSize; f++){
				train_fits.put(sub_pops[s].get(f), new ArrayList<Double>());
			}
		}	
		/*
		 * Here we iterate through the factors and add all classifications they have made, ie
		 * they will have made one classification per blueprint per instance.
		 * We add these fitnesses to the ArrayList so we can filter the best n if we wish.
		 */
		for(int s = 0; s < sub_pops.length; s++){
			IPopulation<?,IPR> pop = sub_pops[s];
			for(int f = 0; f < pop.size(); f++){
				IPR thisFactor = pop.programs.get(f);
				for(int b=0; b<numBP; b++){
					train_fits.get(thisFactor).add((double) cached.fitness[b][s][f]);	
					all_fits.update(cached.fitness[b][s][f]);
				}
			}
		}
		
		for(int s = 0; s < sub_pops.length; s++){
			IPopulation<?,IPR> pop = sub_pops[s];
			for(int f = 0; f < spSize; f++){
				IPR thisFactor = pop.programs.get(f);
				List<Double> train_l = train_fits.get(thisFactor);

				Collections.sort(train_l);
				Collections.reverse(train_l);
				//This will never be empty
				if(pop.getUsed()!=null&&!pop.getUsed().contains((Integer)f)){
					//Average out all fitnesses
					for(int j = 0;j < this.best_n &&  j < train_l.size();j++){
						allClassif.update(train_l.get(j));
					}
					numInactive[s]++;	
					thisFactor.trainFitnessMeasure.fitness = allClassif.mean;	
					//update fitness for inactive factors
					inactive.update(thisFactor.trainFitnessMeasure.fitness);
					//Add fitness to subpopulation fitness
					train_avg_spop[s].update(thisFactor.trainFitnessMeasure.fitness);
				}
				//This factor has contributed to blueprints, use best fitnesses only
				else{
					Stats best_n = new Stats();
					//Add training fitnesses from blueprints it contributes to
					for(int j = 0; j < train_l.size();j++){
						best_n.update(train_l.get(j));
					}
					//Average of the top n fitnesses
					thisFactor.trainFitnessMeasure.fitness = best_n.mean;					
					//update fitness for active factors
					active.update(thisFactor.trainFitnessMeasure.fitness);
					//Add fitness to subpopulation fitness
					train_avg_spop[s].update(thisFactor.trainFitnessMeasure.fitness);
				}
			}
		}
		if(DEBUG&&h.showFitnessAverages)traceFitnessAverages(all_fits, inactive, active, train_avg_spop);
		//Print count of factors assigned average fitness information
		if(DEBUG&&h.showInactive)traceInactive(numInactive);
		//Print factor fitness as they have been assigned.
		if(DEBUG&&h.showAllFitnesses)traceAllFitnesses();

	}
	
	/**
	 *This FF uses factor filtering, but in the inactive population.
	 *Reasoning for this is that the best factors for cooperation have already been chosen
	 *for BPs, and with the current number of blueprints, they 'appear' (informal observation)
	 *to only contribute to 1-2 blueprints; whereas all factors have been executed as part
	 *of all blueprints.
	 *
	 *So now the FF is defined as follows:
	 *All factors; fitness is defined from the best_n blueprints it has been evaluated on.
	 *active factors : fitness is defined from the blueprints it contributes to.
	 *
	 *NOTE: print output and variable names may be a bit transposed here
	 */
	private void mapFitness3(){
		if(DEBUG)System.out.println("f3");
		if(DEBUG&&h.traceFlow)System.out.println("Map Fitness 3---------------------------------");
	//	int[] numInactive=new int [numSP];
		Stats all_fits = new Stats();
	
		Stats [] train_avg_spop = new Stats[numSP];
		for(int init=0; init <numSP; init++){
			train_avg_spop[init]= new Stats();
		}

		Stats inactive = new Stats();
		Stats active = new Stats();
		// set up the mapping
		Map<IPR,List<Double>> train_fits = new HashMap<IPR,List<Double>>();
		
		for(int s = 0; s < numSP; s++){
			for(int f = 0; f < spSize; f++){
				train_fits.put(sub_pops[s].get(f), new ArrayList<Double>());
			}
		}	
		/*
		 * Here we iterate through the factors and add all classifications they have made, ie
		 * they will have made one classification per blueprint per instance.
		 * We add these fitnesses to the ArrayList so we can filter the best n if we wish.
		 */
		
		for(int s = 0; s < sub_pops.length; s++){
			IPopulation<?,IPR> pop = sub_pops[s];//iterate subpops
			
			for(int f = 0; f < pop.size(); f++){
				IPR thisFactor = pop.programs.get(f);//iterate factors
				for(int b=0; b<numBP; b++){
					train_fits.get(thisFactor).add((double) cached.fitness[b][s][f]);//add fitness from each BP
					all_fits.update(cached.fitness[b][s][f]);
				}
				//At this point, The factor has it's fitness defined.
			}
		}
		
		for(int s = 0; s < sub_pops.length; s++){
			IPopulation<?,IPR> pop = sub_pops[s];
			for(int f = 0; f < spSize; f++){
				IPR thisFactor = pop.programs.get(f);
				List<Double> train_l = train_fits.get(thisFactor);

				Collections.sort(train_l);
				Collections.reverse(train_l);
				//train_l now has all fitnesses ready for filtering

				Stats thisInactiveFitness = new Stats();
				//Filter abest fitnesses
				for(int j = 0;j < this.best_n &&  j < train_l.size();j++){
					thisInactiveFitness.update(train_l.get(j));
				}
				//Average of the top n fitnesses
				thisFactor.trainFitnessMeasure.fitness = thisInactiveFitness.mean;	
				//update fitness for inactive factors
				inactive.update(thisFactor.trainFitnessMeasure.fitness);
				//Add fitness to subpopulation fitness
				train_avg_spop[s].update(thisFactor.trainFitnessMeasure.fitness);

			}
		}
		if(DEBUG&&h.showFitnessAverages)traceFitnessAverages(all_fits, inactive, active, train_avg_spop);
		//Print count of factors assigned average fitness information
//		if(DEBUG&&h.showInactive)traceInactive(numInactive);
		//Print factor fitness as they have been assigned.
		if(DEBUG&&h.showAllFitnesses)traceAllFitnesses();

	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public IPopulation<?,IPR>[] getSub_Pops(){
		return sub_pops;
	}
	
	@Override
	public void markSubIntrons(){
		for(IProgram<BP> ip: programs){
			ip.markIntrons();
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//		
	//		TRACE METHODS AND DEBUG OUTPUTS
	//
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/*
	 * Traces various aggregation stats 
	 */
	private void traceFitnessAverages(Stats all_fits, Stats inactive, Stats active, Stats[] train_avg_spop){
		System.out.println();			
		System.out.println("Avg overall "+form.format(all_fits.mean));
		System.out.println("Best_n Inactive avg: "+(form.format(inactive.mean)));//ZERO HERE???	
		System.out.println("Active avg: "+(form.format(active.mean)));
		System.out.println();
		for(int s=0; s<numSP; s++){
			System.out.println("Subpop "+s+" Average fitness: "+(form.format(train_avg_spop[s].mean)));
		}
	}
	
	/**
	 * Shows how many factors will recieve the NFI value.
	 */
	private void traceInactive(int[] numInactive){
		System.out.print("Inactive : ");
		for(int i=0; i<numSP; i++){
			System.out.print("("+i+") ");
			System.out.print(numInactive[i]+" | ");
		}	
		System.out.println();
	}
	
	/**
	 * Displays all fitness values assigned.
	 */
	private void traceAllFitnesses(){
		System.out.println("New assigned fitnesses for factor evolution");
		for(int b=0; b<numBP;b++){
			System.out.print ("original "+b+":");
			for (int s=0; s<numSP; s++){
				int f = programs.get(b).selectedFactor(s);
				System.out.print("("+s+")"+ f+" = "+cached.fitness[b][s][f]+" : ");
			}	
			System.out.println();
		}		
	}
	
	
	/**
	 * Checks convergence on factors
	 */
	private void traceConvergence(int changes){
		System.out.print("Changes : "+changes);
		System.out.println(" :: Considered changes ::");
		for(int b=0; b<numBP; b++){
			System.out.print("bp("+b+") ");
			for(int s=0; s< numSP; s++){
				int f= programs.get(b).selectedFactor(s);	
				System.out.print("\tsp "+s+"  ("+f+"):   "+cached.fitness[b][s][f]+"\t");
			}
			System.out.println();
		}	
	}
	/**
	 * Print out the number of times each factor is used
	 */
	private void traceFactorUsage(){
		//Print counts - either in full or shortened.
		for(int s=0; s<numSP;s++){
			System.out.println("Subpop "+s);
			for(int f=0; f<spSize; f++){
				int num = used[s][f];
				if(h.factorUsedCountShorten){
					if (num>0)System.out.print(f+"("+num+")   ");
				}else{
					System.out.print(tab.format(num));
				}
			}
			System.out.println();
		}	
	}
	/**
	 * Prints out cache values, can be run before and after classification
	 */
	private void traceClassifCache(String when){
		for(int b=0; b < numBP; b++){
			BP thisBluePrint = programs.get(b);
			int fit=0;
			for(int s=0; s< numSP;s++){
				int f = thisBluePrint.selectedFactor(s);
				fit += cached.fitness[b][s][f];
			}
		System.out.println("fitness "+when+" classif(cache) b: "+b +" F= "+fit);
		}	
	}
	/**
	 * Cycles through the blueprints, obtaining the program index for each factor
	 * that is part of the blueprint.
	 * This is run on the 0th generation only, after which control passes
	 * through to this SPLGP class and we use a more effective index.
	 * This approach is hacky and needs replacing.
	 */
	private void traceBlueprintIndices(){
		System.out.println("--<<--<<--<<-->>-->>-->>--");
		for(int b=0; b<numBP;b++){//iterate through blueprints
			//int fitness=0;
			BP blueprint = programs.get(b);
			for(int s=0; s< numSP;s++){//iterate through subpops
				System.out.println("b "+b+" s "+s+" f "+blueprint.selectedFactor(s));
			}//END iterate through subpops
			System.out.println("----------");
		}//END iterate through blueprints		
	}	
	
	/**
	 * Prints to stdout the fitnesses of the component factors.
	 *
	 * @param b The index of the blueprint we are going to examine
	 * @param blueprintOriginalFitness Overall fitness of the blueprint
	 * @param thisBluePrint The blueprint itself
	 */
	private void traceInitialBPfitness(int b, double blueprintOriginalFitness, BP thisBluePrint){
		System.out.print ("original "+b+":"+blueprintOriginalFitness+" ");
		for (int s=0; s<numSP; s++){
			int f = thisBluePrint.selectedFactor(s);
			System.out.print("("+s+")"+ f+" = "+cached.fitness[b][s][f]+" : ");
		}			
}
	/**
	 * Prints to stdout the register values for a blueprint 
	 * @param bp_index The index of the blueprint
	 * @param blueRC The register collection we are examining
	 */
	private void traceBlueprintComponentFitness(int bp_index, RegisterCollection blueRC){
		System.out.print(bp_index + " : ");
		System.out.println(blueRC.toStringShort());  
		System.out.print(bp_index + " : ");		
	}

	/**
	 * Prints to stdout the fitness details of the search aspect
	 * @param b Index of the blueprint we are examining
	 * @param existingFactorFitness 
	 * @param subPopLockedFitnessValue
	 * @param subpopOriginalFitnessValue
	 */
private void traceBestSearch(int b, double existingFactorFitness, double subPopLockedFitnessValue, double subpopOriginalFitnessValue){		
	System.out.println("\nsubpopOriginalFitnessValue ("+b+")"+subpopOriginalFitnessValue);
	System.out.println("existingFactorFitness "+existingFactorFitness);		
	System.out.println("BP minus this factor "+subPopLockedFitnessValue);
}
}
