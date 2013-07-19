package arguments;

import core.Config;
import core.FitnessEnvironment;
import misc.Rand;


/**Represents an argument for an instruction that is a feature*/
public class InstructionArgumentFeature extends IInstructionArgument {

	private final int featureIndex;

	private InstructionArgumentFeature(int numFeatures){
		featureIndex = Rand.Int(numFeatures);
	}

	/**copy constructor*/
	public InstructionArgumentFeature(InstructionArgumentFeature iaf){
		featureIndex = iaf.featureIndex;
	}

	/** Returns the value of the register in the fitness environment fe this instance specifies*/
	public double value(FitnessEnvironment<?> fe){
		return fe.readFeature(featureIndex); 
	}

	// Copies this InstructionArgument in every respect. Caller is responsible for the memory.
	public IInstructionArgument clone(){
		return (IInstructionArgument)(new InstructionArgumentFeature(this));
	}

	// Caller is responsible for deallocating the memory.
	public static IInstructionArgument generate(int numFeatures){
		return (IInstructionArgument)(new InstructionArgumentFeature(numFeatures));
	}


	// Stringify's this InstructionArgumentConstant
	public String toString(){
		String buffer = "";
		buffer += "cf[" + featureIndex + "]";
		return buffer;
	}

	// Used indirectly in IProgram::MarkIntrons - returns the type of the argument and its
	// index. Types are coded for by the enum defined in IInstructionArgument.h.
	public int argumentType() { 
		return ArgumentType.feature;
	}

	public int argumentIndex() { 
		return featureIndex; 
	}

	@Override
	public int hashCode() {
		return argumentIndex();
	}

}
