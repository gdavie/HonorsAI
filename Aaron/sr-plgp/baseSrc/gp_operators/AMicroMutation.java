package gp_operators;

import misc.Rand;
import operators.IInstructionOperation;
import arguments.IInstructionArgument;
import arguments.InstructionArgumentRegister;
import core.Config;
import core.Instruction;
import core.InstructionProgram;

public abstract class AMicroMutation<INSP extends InstructionProgram<INSP>> extends GP_Operator<INSP> {

	private int numRegisters;
	private int numFeatures;
	
	public AMicroMutation(int numFeatures, int numRegisters){
		this.numFeatures = numFeatures;
		this.numRegisters = numRegisters;
	}
	
	/** Performs a Micromutation on this instruction*/
	protected void microMutation(Instruction ins){

		// Select one of the four parts:
		int part = Rand.Int(4);
		String currentString;
		
		// Then convert it ToString, delete it and regenerate it until its string rep changes:
		switch(part) {

		case 0: // Change the operation
			currentString = ins.getOp().toString();
			IInstructionOperation op = null;
			do {
				op = Config.getInstance().instructionOperations.getRandomElement().genOp();
			} while(currentString.equals(op.toString()));
			ins.setOp(op);
			break;

		case 1: // Change the destination register
			currentString = ins.getDestination().toString();
			InstructionArgumentRegister dest = null;
			do {
				dest = (InstructionArgumentRegister)(InstructionArgumentRegister.generate(numRegisters));
			} while(currentString.equals( dest.toString()));
			ins.setDestination(dest);
			break;

		case 2: // Change the first argument
			currentString = ins.getFirstArgument().toString();
			IInstructionArgument firstArg = null;
			do {
				firstArg = Config.getInstance().argumentGenerators.getRandomElement().genArg(numFeatures, numRegisters);
			} while(currentString.equals(firstArg.toString()));
			ins.setFirstArgument(firstArg);
			break;

		case 3: // Change the second argument
			currentString = ins.getSecondArgument().toString();
			IInstructionArgument secondArg = null;
			do {
				secondArg = Config.getInstance().argumentGenerators.getRandomElement().genArg(numFeatures, numRegisters);    
			} while(currentString.equals(secondArg.toString()));
			ins.setSecondArgument(secondArg);
			break;
		}

	}


}
