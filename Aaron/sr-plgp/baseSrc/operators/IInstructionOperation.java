package operators;

import core.FitnessEnvironment;
import arguments.IInstructionArgument;
import arguments.InstructionArgumentRegister;


public abstract class IInstructionOperation {

	 /** Performs the operation defined by this class using the registers specified in the
     first through third arguments of the function call. Returns true if the next
     instruction should be executed and false if we should skip to the next assignment*/
	 public abstract boolean execute(InstructionArgumentRegister dest,
			 IInstructionArgument first,
			 IInstructionArgument second,
			 FitnessEnvironment<?> fe);
	 
	 /** Clones a copy of this InstructionOperation. Caller is responsible for deallocating it.*/
	 public abstract IInstructionOperation clone();
	 
	 // Returns a new InstructionOperation of whichever class implements this function
	 // The Config object currently is used, but who knows if it'll be needed in the future...
	 // C++ doesn't allow abstract statics (for good reason, but it would be nice here if 
	 // they could be virtual on the type) so every InstructionOperation should implement
	 // a method that looks like this.
	 // static InstructionOperation<T>* Generate(Config<T>* conf) = 0;

	 /** Returns a string representation of the operation. If the instruction is a conditional
	 then the caller of this function needs to wrap, e.g. if(...) around the pieces.*/
	 @Override
	 public abstract String toString();
	 
	 public abstract int hashCode(InstructionArgumentRegister dest, IInstructionArgument first,
				IInstructionArgument second);
	 
	 public abstract int index();

	
}
