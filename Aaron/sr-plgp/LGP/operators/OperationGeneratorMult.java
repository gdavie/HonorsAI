package operators;


public class OperationGeneratorMult extends OperationGenerator{

	@Override
	public IInstructionOperation genOp() {
		return InstructionOperationMult.generate();
	}
} 