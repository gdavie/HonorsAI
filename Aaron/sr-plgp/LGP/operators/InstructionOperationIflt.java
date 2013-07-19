package operators;

import arguments.IInstructionArgument;
import arguments.InstructionArgumentRegister;
import core.FitnessEnvironment;

/**Represents an if< operation*/
public class InstructionOperationIflt extends IInstructionOperationConditional{

	/** Returns true if the first argument is less than the second*/
	public boolean execute(InstructionArgumentRegister dest, IInstructionArgument  first,
			IInstructionArgument  second, FitnessEnvironment<?> fe){

		return first.value(fe) < second.value(fe);
	}

	public IInstructionOperation clone(){
		return (IInstructionOperation)(new InstructionOperationIflt());
	}

	public String toString()  { 
		return "<";
	}

	public static IInstructionOperation generate(){
		return new InstructionOperationIflt();
	}

	@Override
	public int hashCode(InstructionArgumentRegister dest,
			IInstructionArgument first, IInstructionArgument second) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int index() {
		return 1;
	}



}
