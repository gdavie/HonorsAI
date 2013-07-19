package core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import misc.Rand;

import operators.IInstructionOperation;
import operators.IInstructionOperationConditional;
import arguments.ArgumentType;
import arguments.IInstructionArgument;
import arguments.InstructionArgumentRegister;


//An instance of this class represents a single register machine instruction.
public final class Instruction {

	// Marked true if this instruction is a structural intron and does not need to be
	// executed.
	private boolean isIntron;

	//registers + operation

	private IInstructionOperation op;
	private InstructionArgumentRegister destination;
	private IInstructionArgument firstArgument;
	private IInstructionArgument secondArgument;
	
	/**constructor*/
	public Instruction(int numFeatures, int numRegisters){
		
		op = Config.getInstance().instructionOperations.getRandomElement().genOp();

		destination = (InstructionArgumentRegister)( InstructionArgumentRegister.generate(numRegisters));

		firstArgument = Config.getInstance().argumentGenerators.getRandomElement().genArg(numFeatures, numRegisters);
		secondArgument = Config.getInstance().argumentGenerators.getRandomElement().genArg(numFeatures, numRegisters);

		isIntron = false;
	}

	/**copy constructor*/
	public Instruction(Instruction rhs){
		copy(rhs);
	}

	/** Copys the Instruction, caller must manage the memory returned.*/
	public Instruction clone(){
		
		return new Instruction(this);//TODO THIS DOES HAVE BUGS
	}
	
	public void copy(Instruction rhs){
		
		op = rhs.op;//TODO may need to fix this
		destination = rhs.destination;
		firstArgument = rhs.firstArgument;
		secondArgument = rhs.secondArgument;
		
		isIntron = rhs.isIntron;
	}

	/** Executes this instruction (via op). Writes its result, if any, to r and returns
	     true unless it was a conditional that was false.*/
	public boolean execute(FitnessEnvironment<?> fe){
		return op.execute(destination, firstArgument, secondArgument, fe);
	}

	/** Returns true if this instruction's InstructionOperation is a sub-class of 
	 ConditionalInstructionOperation*/
	public boolean isConditional(){
		return op instanceof IInstructionOperationConditional;
	}
	
	public boolean isIntron(){
		return isIntron;
	}
	
	public void setIntron(boolean value){
		isIntron = value;
	}

	public String toString(){
		return toString(false);
	}

	public String toString(boolean commentIntrons ){
		  String buffer = "";

		  if(commentIntrons && isIntron) {
		    buffer += "//";
		  }
		  
		  if(isConditional()) {
		    buffer += "if(" + firstArgument.toString() + " " + op.toString() + " " 
			   + secondArgument.toString() + ")";
		  }
		  else {
		    buffer += destination.toString() + " = " + firstArgument.toString() + " "
			   + op.toString() + " " + secondArgument.toString() + ";";
		  }
		  
		  return buffer;
	}
	
	public void setDest(int index){
		destination = new InstructionArgumentRegister(index);
	}

	// Used in the IProgram<T>::MarkIntrons method - they return only the register index and
	// register type used in each register referred to:
	public int destinationIndex(){ return destination.argumentIndex(); }
	public int firstArgumentType(){ return firstArgument.argumentType(); }
	public int firstArgumentIndex(){ return firstArgument.argumentIndex(); }
	public int secondArgumentType(){ return secondArgument.argumentType(); }
	public int secondArgumentIndex(){ return secondArgument.argumentIndex(); }

	public IInstructionOperation getOp() {
		return op;
	}
	public void setOp(IInstructionOperation op) {
		this.op = op;
	}
	public InstructionArgumentRegister getDestination() {
		return destination;
	}
	public void setDestination(InstructionArgumentRegister destination) {
		this.destination = destination;
	}
	public IInstructionArgument getFirstArgument() {
		return firstArgument;
	}
	public void setFirstArgument(IInstructionArgument firstArgument) {
		this.firstArgument = firstArgument;
	}
	public IInstructionArgument getSecondArgument() {
		return secondArgument;
	}
	public void setSecondArgument(IInstructionArgument secondArgument) {
		this.secondArgument = secondArgument;
	}
	
	public int hashCode(){
		return op.hashCode(destination, firstArgument, secondArgument);
	}
	
/** ============ static operators on lists of instructions ==============================*/
	
	public static int randomlyCullToSize(ArrayList<Instruction> ins, int cullToThisSize){
		int min = ins.size()+1;
		while(ins.size() > cullToThisSize) {
			int temp = removeRandomInstruction(ins);
			if(temp < min){
				min = temp;
			}
		}
		return min;
	}
	
	
	public static int removeRandomInstruction(ArrayList<Instruction> instructions){
		int modPos = Rand.Int(instructions.size());
		instructions.remove(modPos);
		return modPos;
	}
	
	public static void MarkIntrons(ArrayList<Instruction> instructions, int numUsedRegisters){
		/*
	    Source: Brameier, M 2004  On Linear Genetic Programming (thesis)

	    Algorithm 3.1 (detection of structural introns)
	    1. Let set R_eff always contain all registers that are effective at the current program
	       position. R_eff := { r | r is output register }.
	       Start at the last program instruction and move backwards.
	    2. Mark the next preceding operation in program with:
	        destination register r_dest element-of R_eff.
	       If such an instruction is not found then go to 5.
	    3. If the operation directly follows a branch or a sequence of branches then mark these
	       instructions too. Otherwise remove r_dest from R_eff .
	    4. Insert each source (operand) register r_op of newly marked instructions in R_eff
	       if not already contained. Go to 2.
	    5. Stop. All unmarked instructions are introns.
		 */

		// Assume all instructions are introns and mark as such. We do it backwards because we need
		// a reverse iterator later on and hell, why allocate two iterators - clutters the code and 
		// the memory.
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
				// Then: If the next instruction is not an intron and if this instruction is not the last
				// one in the program then this instruction is also not an intron, otherwise it is:
				if(/**ri != instructions.get(instructions.size()-1)*/i != instructions.size()-1 && !instructions.get(i+1).isIntron()) {
					ri.setIntron(false);
				}
			}
			else { // Instruction ri is an assignment instruction
				if(usedRegisters.contains(ri.destinationIndex())) {
					ri.setIntron(false);
					usedRegisters.remove(ri.destinationIndex());

					if(ri.firstArgumentType() == ArgumentType.register) {
						usedRegisters.add(ri.firstArgumentIndex());
					}

					if(ri.secondArgumentType() == ArgumentType.register) {
						usedRegisters.add(ri.secondArgumentIndex());
					}
				}
			}
		}
	}

}
