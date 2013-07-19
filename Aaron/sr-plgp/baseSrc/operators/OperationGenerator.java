package operators;


// An operation generator function generates an IInstructionOperation for use as, e.g., the
// operation of an instruction.
public abstract class OperationGenerator{
	public abstract IInstructionOperation genOp();
}