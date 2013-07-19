package operators;


public class OperationGeneratorPlus extends OperationGenerator{

	@Override
	public IInstructionOperation genOp() {
		return InstructionOperationPlus.generate();
	}
} 