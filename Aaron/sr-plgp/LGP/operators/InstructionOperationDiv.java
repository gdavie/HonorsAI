package operators;

import arguments.IInstructionArgument;
import arguments.InstructionArgumentRegister;
import core.FitnessEnvironment;

public class InstructionOperationDiv extends IInstructionOperation {

	/** Adds first to second and writes the result to dest. Returns true to indicate that
	 execution should proceed to the next instruction.*/
	public boolean execute(InstructionArgumentRegister dest, IInstructionArgument first,
			IInstructionArgument second, FitnessEnvironment<?> fe){
		
		double value;
		if(second.value(fe) == 0)
			value = 0;
		else{
			try{
				value = first.value(fe) / second.value(fe);

			}catch(ArithmeticException e){
				value = 0;
			}
		}
		fe.writeRegister(dest.index(), value);
		return true;
	}

	public IInstructionOperation clone(){
		return (IInstructionOperation)(new InstructionOperationDiv());
	}

	public String toString(){ 
		return "/";
	}

	public static IInstructionOperation generate(){
		return new InstructionOperationDiv();
	}
	
	public int hashCode(InstructionArgumentRegister dest, IInstructionArgument first,
			IInstructionArgument second){
		if(second.hashCode() == 0)
			return 0;
		else
			return first.hashCode() / second.hashCode();
	}

	@Override
	public int index() {
		return 0;
	}

}
