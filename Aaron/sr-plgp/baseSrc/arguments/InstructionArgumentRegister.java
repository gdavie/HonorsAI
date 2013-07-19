package arguments;

import core.Config;
import core.FitnessEnvironment;
import misc.Rand;


/**Represents an argument to an instruction that is a read-write register*/
public class InstructionArgumentRegister extends IInstructionArgument{

	public final int registerIndex;
	
	/**Constructor. ONLY USE THIS ONE*/
	public InstructionArgumentRegister(int numRegisters){
		registerIndex = Rand.Int(numRegisters);
	}
	
	/**copy constructor*/
	public InstructionArgumentRegister(InstructionArgumentRegister iar){
		registerIndex = iar.registerIndex;
	}
	
	/** Returns the value of the register in the fitness environment fe this instance specifies*/
	public double value( FitnessEnvironment<?> fe)  { 
		return fe.readRegister(registerIndex);
	}
	
	public int index()  {
		return registerIndex; 
	}

	// Copies this InstructionArgument in every respect. Caller is responsible for the memory.
	public IInstructionArgument clone(){
		return  (IInstructionArgument)(new InstructionArgumentRegister(this));
	}

	// Caller is responsible for deallocating the memory.
	public static IInstructionArgument generate(int numRegisters){
		return (IInstructionArgument)(new InstructionArgumentRegister(numRegisters));
	}

	// Stringify's this InstructionArgumentConstant
	public String toString(){
		String buffer = "";
		  buffer  += "r[" + registerIndex + "]";
		  return buffer;
	}

	// Used indirectly in IProgram<T>::MarkIntrons - returns the type of the argument and its
	// index. Types are coded for by the enum defined in IInstructionArgument.h.
	public int argumentType()  { return ArgumentType.register; }
	public int argumentIndex()  { return registerIndex; }//TODO note this is a duplicate of index()...

	@Override
	public int hashCode() {
		return argumentIndex();
	}



}
