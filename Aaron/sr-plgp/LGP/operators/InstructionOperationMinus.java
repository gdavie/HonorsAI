package operators;

import arguments.IInstructionArgument;
import arguments.InstructionArgumentRegister;
import core.FitnessEnvironment;

/**Represents an addition operation*/
public class InstructionOperationMinus extends IInstructionOperation{

	/** Adds first to second and writes the result to dest. Returns true to indicate that
	 execution should proceed to the next instruction.*/
	public boolean execute(InstructionArgumentRegister dest, IInstructionArgument first,
			IInstructionArgument second, FitnessEnvironment<?> fe){

		fe.writeRegister(dest.index(), first.value(fe) - second.value(fe));
		return true;
	}

	public IInstructionOperation clone(){
		return (IInstructionOperation)(new InstructionOperationMinus());
	}

	public String toString() { 
		return "-";
	}

	public static InstructionOperationMinus generate(){
		return new InstructionOperationMinus();
	}

	@Override
	public int hashCode(InstructionArgumentRegister dest,
			IInstructionArgument first, IInstructionArgument second) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int index() {
		return 2;
	}

}
