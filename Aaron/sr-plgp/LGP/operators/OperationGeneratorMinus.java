package operators;


public class OperationGeneratorMinus extends OperationGenerator{

	@Override
	public IInstructionOperation genOp() {
		return InstructionOperationMinus.generate();
	}
} 