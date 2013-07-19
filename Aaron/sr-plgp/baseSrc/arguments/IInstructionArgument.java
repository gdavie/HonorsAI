package arguments;

import core.FitnessEnvironment;

public abstract class IInstructionArgument {
	
	/** Returns the value of this register in the fitness environment fe*/
	public abstract double value(FitnessEnvironment<?> fe);
	
	/** Copies this InstructionArgument in every respect*/
	public abstract IInstructionArgument clone();
	
	/**Stringify's this InstructionArgument*/
	@Override
	public abstract String toString();
	
	/** Used when doing IProgram<T>::MarkIntrons - returns the type of the argument  and its
     index. Types are coded for by the enum defined above.*/
    public abstract int argumentType();//TODO note changed this to int because java enums not ints.
    public abstract int argumentIndex();
    
    public abstract int hashCode();

}

