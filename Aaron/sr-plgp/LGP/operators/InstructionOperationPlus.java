package operators;

import arguments.IInstructionArgument;
import arguments.InstructionArgumentRegister;
import core.FitnessEnvironment;

/** Represents an addition operation*/
public class InstructionOperationPlus extends IInstructionOperation {


	/** adds the values in first and second and write result to dest*/
	public boolean execute(InstructionArgumentRegister dest, IInstructionArgument  first,
			IInstructionArgument  second, FitnessEnvironment<?> fe){
		
		fe.writeRegister(dest.index(), first.value(fe) + second.value(fe));
		return true;
	}
 
	public IInstructionOperation clone(){
		return (IInstructionOperation)(new InstructionOperationPlus());
	}

	public String toString() { 
		return "+";
	}

	public static IInstructionOperation generate(){
		return new InstructionOperationPlus();
	}

	@Override
	public int hashCode(InstructionArgumentRegister dest,
			IInstructionArgument first, IInstructionArgument second) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int index() {
		return 4;
	}

}
