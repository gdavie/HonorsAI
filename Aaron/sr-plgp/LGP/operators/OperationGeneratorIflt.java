package operators;


public class OperationGeneratorIflt extends OperationGenerator{

	@Override
	public IInstructionOperation genOp() {
		return InstructionOperationIflt.generate();
	}
} 