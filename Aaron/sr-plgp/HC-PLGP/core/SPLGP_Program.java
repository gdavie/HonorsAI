package core;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import cache.Normaliser;

import misc.WeightedCollection;
import arguments.ArgumentType;
import dbg.RegisterEvaluator;

/** The interface/abstract definition of a program - this can be specialised, e.g. for hill-
 climbing programs. T is the type of the features and registers. Sub classes must only
 give the constructors, although they can override other methods too if need be. They should
 also assign an IFitnessMeasure of some type (the right type whatever that is) to the 
 member "fitnessMeasure" in their constructors.*/
public final class SPLGP_Program extends InstructionProgram<SPLGP_Program>{

	//CC marker
	public boolean inElite = false;
	//end CC marker
	
	//clustering marker
	public boolean active = false;
	//end clustering marker
	
	//MUTATIONJ
	protected WeightedCollection<Integer> distribution;
	protected double[] resps;
	//END MUTATIONJ

	//Normalisation
	private static Normaliser norm = new Normaliser();
	boolean normalise = true;
	int FUNCTION = 2;
	//End normalisation
	
	/**normal constructor*/
	public SPLGP_Program(int programSize){
		super(new MultiClassFitnessMeasure(), new MultiClassFitnessMeasure(), programSize, 
				Config.getInstance().numFeatures, Config.getInstance().numRegisters);
		//MUTATIONJ
		distribution = new WeightedCollection<Integer>();
		resps = new double[config.numClasses];
		//END MUTATIONJ
	}

	/**copy constructor*/
	public SPLGP_Program(SPLGP_Program rhs){
		super(rhs);

		// The fitnessMeasure should be copied in the sub-class which uses IProgram; 
		// fitnessStatus, finalRegisterValues and config are copied in the initialisation list.

		// Copy the fitness measure, using its defaultly defined cctor
		trainFitnessMeasure = new MultiClassFitnessMeasure((MultiClassFitnessMeasure)(rhs.trainFitnessMeasure));
		validFitnessMeasure = new MultiClassFitnessMeasure((MultiClassFitnessMeasure)(rhs.validFitnessMeasure));

		//MUTATIONJ
		distribution = new WeightedCollection<Integer>(rhs.distribution);
		resps = new double[config.numClasses];
		for(int i = 0; i < config.numClasses; i++){
			resps[i] = rhs.resps[i];
		}
		//END MUTATIONJ
	}


	public SPLGP_Program clone(){
		return new SPLGP_Program(this);
	}

	public void reinitialize(SPLGP_Program rhs){
		// The fitnessMeasure should be copied in the sub-class which uses IProgram; 
		super.reinitialize(rhs);

		//MUTATIONJ
		distribution = new WeightedCollection<Integer>(((SPLGP_Program)rhs).distribution);
		for(int i = 0; i < config.numClasses; i++){
			resps[i] = ((SPLGP_Program)rhs).resps[i];
		}
		//END MUTATIONJ
	}

	public ArrayList<Integer> alleleInstructions(Set<Integer> cls){
		ArrayList<Integer> usedInstrucs = new ArrayList<Integer>();
		Set<Integer> usedRegs = new HashSet<Integer>();

		usedRegs.addAll(cls);

		/**main control loop*/
		for(int i = instructions.size()-1; i >= 0; i--){
			Instruction ins = instructions.get(i);

			if(!ins.isIntron()){

				boolean flag = false;

				if(ins.isConditional()){
					if(usedInstrucs.contains(i+1)){
						usedInstrucs.add(i);
						flag = true;
					}
				}
				else{
					int dest = ins.destinationIndex();
					if(usedRegs.contains(dest)){
						usedInstrucs.add(i);
						usedRegs.remove((Object)dest);
						flag = true;
					}
				}
				if(flag){
					if(ins.firstArgumentType() == ArgumentType.register){
						int index = ins.firstArgumentIndex();
						if(!usedRegs.contains(index))
							usedRegs.add(index);
					}
					if(ins.secondArgumentType() == ArgumentType.register){
						int index = ins.secondArgumentIndex();
						if(!usedRegs.contains(index))
							usedRegs.add(index);
					}
				}
			}
		}
		Collections.reverse(usedInstrucs);
		return usedInstrucs;
	}

	/**if registers is empty, returns the baseline*/
	public double average(ArrayList<Double>[] vals, int index, double def){
		if(vals[index].size() == 0)
			return def;
		double sum = 0;
		for(int i = 0; i < vals[index].size(); i++){
			sum += vals[index].get(i);
		}
		return sum/vals[index].size();
	}

	/**MUTATIONJ
	 * Uses final register values to calculate the responsibilities of instructions in the
	 * program. These are then used to stocastically influence which instructions are selected
	 * to be mutated.
	 */
	@SuppressWarnings("unchecked")
	public void calcDist(){
		try {
			if(fitnessStatus == false)
				throw new Exception("Tried to calculate distribtuion before calculating fitness");
		} catch (Exception e) {e.printStackTrace();}

		distribution.clear();

		double[] probs = new double[instructions.size()];

		ArrayList<Double>[] registers = new ArrayList[config.numRegisters];

		double average = 0;

		//set up registers with initial output values and determine average resp.
		for(int i = 0; i < config.numClasses; i++){
			registers[i] = new ArrayList<Double>();
			registers[i].add(resps[i]);
			average += resps[i];
		}
		average = average/config.numClasses;
		for(int i = config.numClasses; i < registers.length; i++){
			registers[i] = new ArrayList<Double>();
			registers[i].add(average);
		}


		/**main loop:
		 * 1. select the next instruction
		 * IF EXON
		 * 2. assign to it prob equal to average of values associated with destination register
		 * 3. update destination register to have no associated values
		 * 4. add dest register value before updating to both register operands
		 * IF INTRON
		 * 5. assign average prob to instruction.
		 * 
		 */
		for(int i = instructions.size()-1; i >=0; i--){
			Instruction ins = instructions.get(i);

			if(ins.isIntron()){
				probs[i] = average;
			}
			else{
				if(ins.isConditional()){
					//we know it is not the last instruction as it woiuld then be an intron
					probs[i] = probs[i+1];
				}
				else{
					double destVal = min(registers, ins.destinationIndex(), average);
					probs[i] = destVal;
					registers[ins.destinationIndex()].clear();
					if(ins.firstArgumentType() == ArgumentType.register){
						registers[ins.firstArgumentIndex()].add(destVal);
					}
					if(ins.secondArgumentType() == ArgumentType.register){
						registers[ins.secondArgumentIndex()].add(destVal);
					}
				}
			}
		}
		for(int i = 0; i < probs.length; i++){
			distribution.addElement(i, probs[i]);
		}
	}


	/** Returns an array equal to resps - other.resps. So positive number means that
	 * the other program performs better, and negative number means that this program performs better
	 * We desire positive numbers as they indicate we can make our current program better
	 * @param prog
	 * @return
	 */
	public double[] diff(SPLGP_Program prog){
		double[] ret = new double[config.numClasses];
		for(int i = 0; i < config.numClasses; i++){
			ret[i] = resps[i] - prog.resps[i]; 
		}
		return ret;
	}


	/**
	 * Executes the environment on the registers and current fitness case in fe. Assumes the
	 registers start with the correct values (i.e. 0 or whatever is relevant). Caches the
	 final register values into the member IProgram::finalRegisterValues.*/
	@Override 
	public void execute(FitnessEnvironment<?> fe, RegisterCollection rc, int num){
		boolean executeNextAssignment = true;
		int pos, cachePos = 0, cacheNum = 0;

			pos = 0;
			cachePos = 0;


		for(int i = cachePos, var = pos; i < config.numCache+1; i++){//looping through all the instructions between cache points
			int stop = config.maxLength*(i+1)/(config.numCache+1);//execute up to the next cache point
			if(stop >= instructions.size())
				stop = instructions.size()-1;//dont go past the end

			for(; var <= stop; ++var) {
				Instruction ins = instructions.get(var);

				if(executeNextAssignment) { 
					// Then we are not in the middle of nor have just finished a conjunction of conditionals
					// w/ a false in them, so exec it  (unless is a structural intron, when we just carry on)
					//if(!ins.isIntron()) {//TODO took this out because it interferes with caching.
					executeNextAssignment = ins.execute(fe);
					//}
					// else executeNextAssignment instruction stays true - structural introns can't effect
					// code flow.
				}
				else {
					// If this is an assignment it is the one we should skip, so reset the execute flag (even
					// if its a structural intron, although if it is all the conditionals before it should
					// have been as well). Otherwise, if it's a conditional just skip it - same reasoning
					// about structural introns applys here as it does for assignment instructions.
					if(!ins.isConditional()) {
						executeNextAssignment = true;
					}
					// else leave it false as it should be (as described in previous comment block)
				}
			}
		}
		// Now store finalRegisterValues
		//We need to normalise the numbers in the fitness environment
		//Then pass them to the program rcs?
		
//		if(!normalise && rc !=null){ //normal execution
		
		//Write to program's register collection.
		for(int i = 0; i < config.numRegisters; ++i) {
			if(normalise){//Re-write the value in the fitness environment
				double tmp = norm.normalise(fe.readRegister(i), FUNCTION);
				fe.writeRegister(i, tmp);
			}
			if(rc!=null){//Test sends a null RC here
				rc.write(i,fe.readRegister(i));
			}
				
			}
//		}else if(rc != null){
//			for(int i = 0; i < config.numRegisters; ++i) {
//				rc.write(i, 
//				norm.normalise(fe.readRegister(i), FUNCTION));
//			}
//		}
	}

	public double[] getResps(){
		return resps;
	}

	public int JrandomInstruction(){
		return distribution.getRandomElement();
	}

	/** Sets the IsIntron flag to true for each instruction in the program if that instruction is
	 a structural intron, false otherwise. Marking structural introns can massively speed
	 up execution on long programs.*/
	@Override
	public void markIntrons() {
		Instruction.MarkIntrons(instructions, config.numClasses);
	}

	public double min(ArrayList<Double>[] vals, int index, double def){
		if(vals[index].size() == 0)
			return def;
		double min = Double.MAX_VALUE;
		for(int i = 0; i < vals[index].size(); i++){
			double val = vals[index].get(i);
			if(val <= min)
				min = val;
		}
		return min;
	}

	public void printDistribution(){
		for(int i = 0; i < distribution.NumberOfElements(); i++){
			Instruction ins = instructions.get(i);
			double weight = distribution.getWeight(i);
			System.out.printf("W: %6.1f    %s\n",weight, ins.toString(true));
		}
	}

	public double[] resps(){
		return resps;
	}

	/** Returns a C++-style function as a string representation of this program.*/
	public String toString(){
		return toString(true, false);
	}

	/** Returns a C++-style function as a string representation of this program.*/
	public String toString(boolean printFitness, boolean commentIntrons){

		String buffer = "";

		if(printFitness) {
			//buffer += "// Fitness Information: " + trainFitnessMeasure.toString() + "\n";
		}

		buffer += "{\n";

		for(int i = 0; i < instructions.size(); ++i) {
			buffer += "\t" + instructions.get(i).toString(commentIntrons) + "\n";
		}

		buffer += "}";

		return buffer;
	}

	/** Updates a program's fitness according to the fitness cases in the FitnessEnvironment
	 passed to it. Note that we override the original method and copy the code in order to insert 
	 some extra mutationJ code*/
	public <F extends IFitnessCase> void updateFitness(FitnessEnvironment<F> tfe, FitnessEnvironment<F> vfe){
		// Zero the fitness to a null state, mark the introns to optimise execution time:

		zeroFitness();
		for(int i = 0; i < resps.length; i++){
			resps[i] = 1;
		}
		System.out.println(":::");
		markIntrons();

		//calculate training fitness
		if(!tfe.loadFirstCase()) {
			throw new RuntimeException("No fitness cases in tfe to evaluate against");

		}
		int count = 0;
		do {
			tfe.zeroRegisters();

			execute(tfe, trainFinalRegisterValues, count++);
			double fChange = trainFitnessMeasure.updateError(trainFinalRegisterValues, tfe.currentCase());

			//MUTATIONJ: we got the training example wrong, so at least one register bigger.
			if(fChange > 0){
				int classNum = ((MultiClassFitnessCase)tfe.currentCase()).classNumber();
				resps[classNum]++;
			}
		} while(tfe.loadNextCase());

		//calculate validation fitness
		if(!vfe.loadFirstCase()) {
			throw new RuntimeException("No fitness cases in vfe to evaluate against");
		}
		count = 0;
		do {
			vfe.zeroRegisters();

			execute(vfe, validFinalRegisterValues, count);
			validFitnessMeasure.updateError(validFinalRegisterValues, vfe.currentCase());

			count++;
		} while(vfe.loadNextCase());

		setFitnessStatus(true);
		calcDist();
	}
	public void markIntrons(RegisterEvaluator re){
		/**
		 * Her I don't want to interfere with the marking of introns,
		 * but I would like to be able to capture untouched registers.
		 * This would normally be passed through to the Instruction
		 * class and done there.
		 */
		//Instruction.MarkIntrons(instructions, config.numClasses);
		int numUsedRegisters=config.numClasses;
		int[] nullReg=new int[numUsedRegisters];
		
		for(int i = instructions.size()-1; i >=0; i--) {
			Instruction ri = instructions.get(i);
			ri.setIntron(true);
		}

		// Assume all registers are involved in the output (base class can override this if need be)
		Set<Integer> usedRegisters = new HashSet<Integer>();

		for(int i = 0; i <= numUsedRegisters; ++i) {
			usedRegisters.add(i);
		}

		// Now go through steps 2-4 of the algorithm:
		for(int i = instructions.size()-1; i >= 0; --i) {
			Instruction ri = instructions.get(i);
			if(ri.isConditional()) {
				if(/**ri != instructions.get(instructions.size()-1)*/i != instructions.size()-1 && !instructions.get(i+1).isIntron()) {
					ri.setIntron(false);
				}
			}
			else { // Instruction ri is an assignment instruction
				if(usedRegisters.contains(ri.destinationIndex())) {
					ri.setIntron(false);
					usedRegisters.remove(ri.destinationIndex());
					/**Here we are keeping track of registers that have been
					* Assigned to
					* We will need to pass this number back out so we can
					* aggregate the scores and analyse the subpopulations
					*/
					nullReg[ri.destinationIndex()]++;
					if(ri.firstArgumentType() == ArgumentType.register) {
						usedRegisters.add(ri.firstArgumentIndex());
					}

					if(ri.secondArgumentType() == ArgumentType.register) {
						usedRegisters.add(ri.secondArgumentIndex());
					}
				}
			}
		}
		//This adds a set of registers for one part of a program 
		//ie one slot in a blueprint
		if(re!=null)re.addRegisters(nullReg);
		//re.printSingleSlot();
	}
	
	/////////////////////////End Mark Introns

}
