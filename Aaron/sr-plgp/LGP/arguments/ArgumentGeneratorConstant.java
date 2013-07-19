package arguments;


public class ArgumentGeneratorConstant extends ArgumentGenerator {

	public ConstantFactory cf;
	
	public ArgumentGeneratorConstant(ConstantFactory cf){
		this.cf = cf;
	}
	
	@Override
	public IInstructionArgument genArg(int numFeature, int numRegisters) {
		return InstructionArgumentConstant.generate(new ConstantFactoryDouble());
	}

}
