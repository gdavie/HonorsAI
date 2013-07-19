package arguments;

import misc.Rand;
import core.Config;
import core.FitnessEnvironment;


/**Represents an argument to an instruction that is a read-write register*/
public class InstructionArgumentConstant extends IInstructionArgument{

	private final double value;
	
	private static int p_inverse;

	/** default cctor, dtor*/
	public InstructionArgumentConstant(InstructionArgumentConstant iac){
		value = iac.value;
	}


	public InstructionArgumentConstant( ConstantFactory cf){
		value = Rand.generateRandomConstant(cf);
	}
	
	public InstructionArgumentConstant(double value){
		this.value = value;
	}

	/** Returns the value of the register in the fitness environment fe this instance specifies*/
	public double value(FitnessEnvironment<?> fe){ 
		return value; 
	}

	/** Copies this InstructionArgument in every respect. Caller is responsible for the memory.*/
	public IInstructionArgument clone(){
		return (IInstructionArgument)(new InstructionArgumentConstant(this));
	}

	/** Caller is responsible for deallocating the memory.*/
	public static IInstructionArgument generate(ConstantFactory cf){
		return (IInstructionArgument)(new InstructionArgumentConstant(cf));
	}

	/** Stringify's this InstructionArgumentConstant*/
	public String toString(){
		String buffer = "";
		buffer += value;
		return buffer;
	}

	/** Used indirectly in IProgram<T>::MarkIntrons - returns the type of the argument and its
	 index. Types are coded for by the enum defined in IInstructionArgument.h.*/
	public int argumentType() { 
		return ArgumentType.constant; 
	}

	public int argumentIndex() {
		try {
			throw new Exception("ArgumentConstant has no index.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}


	@Override
	public int hashCode() {
		double values = 10;
		double constRange = Config.getInstance().constRange;
		return (int)( (value + 0.5*constRange)*values/constRange);
	}


}
