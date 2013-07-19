package core;

import dbg.RegisterEvaluator;

public abstract class SRP_IProgram<IPR extends IProgram<IPR>> {


	protected Config config;

	//for use if you want to cache
	protected int modPos = 0;

	// Training Fitness
	protected RegisterCollection trainFinalRegisterValues;
	protected IFitnessMeasure trainFitnessMeasure;
	
	// Validation Fitness

	protected RegisterCollection validFinalRegisterValues;

	protected IFitnessMeasure validFitnessMeasure;

	// true if this program's fitness is correct, false otherwise
	protected boolean fitnessStatus;

	// For the purposes of collecting statistics

	public int genCreated;

	public double timeCreated;

	//for the purposes of having programs with a variable number of features

	protected int numFeatures;
	//for the purposes of having programs with a variable number of registers
	protected int numRegisters;

	//IFDEF REG_CACHE
	public IProgram<?> old_version;

	public SRP_IProgram(IFitnessMeasure tfm, IFitnessMeasure vfm, int numFeatures, int numRegisters) {
		config = Config.getInstance();

		//to keep track of properties programs
		this.numFeatures = numFeatures;
		this.numRegisters = numRegisters;

		trainFinalRegisterValues = new RegisterCollection(
				numRegisters);
		trainFitnessMeasure = tfm;
		validFinalRegisterValues = new RegisterCollection(
				numRegisters);
		validFitnessMeasure = vfm;
		fitnessStatus = false;

		// for later statistics
		genCreated = IPopulation.gen;
		timeCreated = System.currentTimeMillis() - IPopulation.time;

		//IFDEF REG_CACHE
		if(config.REG_CACHE){
			old_version = null;
		}
	}


	public <X extends IProgram<X>>SRP_IProgram(IProgram<X> rhs) {
		trainFinalRegisterValues = new RegisterCollection(
				rhs.trainFinalRegisterValues);
		validFinalRegisterValues = new RegisterCollection(
				rhs.validFinalRegisterValues);
		trainFitnessMeasure = rhs.trainFitnessMeasure.clone();
		validFitnessMeasure = rhs.validFitnessMeasure.clone();

		fitnessStatus = rhs.fitnessStatus;
		config = rhs.config;

		//to keep track of properties programs
		numFeatures = rhs.numFeatures;
		numRegisters = rhs.numRegisters;

		// for statistics
		genCreated = IPopulation.gen;
		timeCreated = System.currentTimeMillis() - IPopulation.time;

		//IFDEF REG_CACHE
		if(config.REG_CACHE){
			if(rhs.old_version != null){//many of these will  be null
				old_version = rhs.old_version.clone();
			}
		}
	}

	public void reinitialize(IPR rhs) {

		trainFinalRegisterValues.copyRegisters(rhs.trainFinalRegisterValues);
		validFinalRegisterValues.copyRegisters(rhs.validFinalRegisterValues);
		trainFitnessMeasure.copy(rhs.trainFitnessMeasure);
		validFitnessMeasure.copy(rhs.validFitnessMeasure);
		fitnessStatus = rhs.fitnessStatus;
		config = rhs.config;

		//to keep track of properties programs
		numFeatures = rhs.numFeatures;
		numRegisters = rhs.numRegisters;

		// for statistics
		genCreated = IPopulation.gen;
		timeCreated = System.currentTimeMillis() - IPopulation.time;
		
		old_version = null;//THIS SHOULD BE SET HIGHER UP IN THE GP OPERATORS

	}

	public abstract IPR clone();

	/**
	 * 
	 * @param fe
	 * @param rc
	 * @param caseNum the number of the training example we are currently on. Used only for cacheing.
	 */
	public abstract void execute(FitnessEnvironment<?> fe,
			RegisterCollection rc, int caseNum);

	/**
	 * Returns a program's overall fitness level according to its fitness
	 * measure
	 */
	public double fitness() {
		return trainFitnessMeasure.overallFitness();
	}
	
	public void setFitness(double fitness){
		trainFitnessMeasure.fitness = fitness;
	}

	/**
	 * Returns a reference to the correctness-status of this programs
	 * FitnessMeasure - true if it is accurate, false if it is not or is unknown
	 * (the latter is more frequent/likely).
	 */
	public boolean fitnessStatusReference() {
		return fitnessStatus;
	}

	public abstract void markIntrons();
	public abstract void markIntrons(RegisterEvaluator re);
	public abstract int randomlyCullToSize(int maxLength);

	public void setFitnessStatus(boolean fs) {
		fitnessStatus = fs;
	}

	public abstract int size();

	public abstract String toString(boolean printFitness, boolean commentIntrons);

	public <F extends IFitnessCase> void updateFitness(FitnessEnvironment<F> tfe, FitnessEnvironment<F> vfe) {
		// Zero the fitness to a null state, mark the introns to optimise
		// execution time:

		zeroFitness();
		markIntrons();

		updateType(tfe, trainFinalRegisterValues, trainFitnessMeasure);

		updateType(vfe, validFinalRegisterValues, validFitnessMeasure);

	}
	
	public <F extends IFitnessCase>void updateType(FitnessEnvironment<F> fe, RegisterCollection frv, IFitnessMeasure ifm){
		// calculate training fitness
		if (!fe.loadFirstCase()) {
			throw new RuntimeException("No fitness cases in fe to evaluate against");
		}
		int count = 0;
		do {
			fe.zeroRegisters();

			execute(fe, frv, count);
			ifm.updateError(frv, fe.currentCase());

			count++;
		} while (fe.loadNextCase());	
	}

	public double validationFitness() {
		return validFitnessMeasure.fitness;
	}

	/** Just a wrapper around the IFitnessMeasure method */
	public void zeroFitness() {
		fitnessStatus = false;
		trainFitnessMeasure.zeroFitness();
		validFitnessMeasure.zeroFitness();
	}


	public void setNumFeatures(int numFeatures){
		this.numFeatures = numFeatures;
	}


	public void setModPos(int modPos){
		this.modPos = modPos;
	}

	public int getModPos(){
		return modPos;
	}

	public Instruction instructionFactory() {
		return new Instruction(numFeatures,numRegisters);
	}
	
	public void SetOldVersion(IPR old){
		old_version = old;
	}

	public RegisterCollection getTrainFinalRegisterValues() {
		return trainFinalRegisterValues;
	}

	public IFitnessMeasure getTrainFitnessMeasure() {
		return trainFitnessMeasure;
	}


	public RegisterCollection getValidFinalRegisterValues() {
		return validFinalRegisterValues;
	}


	public IFitnessMeasure getValidFitnessMeasure() {
		return validFitnessMeasure;
	}


}
