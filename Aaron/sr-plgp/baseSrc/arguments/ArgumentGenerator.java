package arguments;


// An operation generator function generates an IInstructionOperation for use as, e.g., the
// operation of an instruction.
public abstract class ArgumentGenerator{
	public abstract IInstructionArgument genArg(int numFeature, int numRegister);
}