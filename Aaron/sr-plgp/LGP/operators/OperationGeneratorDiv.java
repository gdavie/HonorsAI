package operators;


public class OperationGeneratorDiv extends OperationGenerator{

	@Override
	public IInstructionOperation genOp() {
		return InstructionOperationDiv.generate();
	}
}
  