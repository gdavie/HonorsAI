package arguments;


public class ArgumentGeneratorRegister extends ArgumentGenerator {

	@Override
	public IInstructionArgument genArg(int numFeature, int numRegisters) {
		return InstructionArgumentRegister.generate(numRegisters);
	}

}
