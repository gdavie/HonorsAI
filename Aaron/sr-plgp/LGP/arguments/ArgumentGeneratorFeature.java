package arguments;



public class ArgumentGeneratorFeature extends ArgumentGenerator {

	@Override
	public IInstructionArgument genArg(int numFeature, int numRegisters) {
		return InstructionArgumentFeature.generate(numFeature);
	}

}
