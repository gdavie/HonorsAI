package core;


public class MultiClassProgramFactory extends IProgramFactory<MultiClassProgram> {
	
	public MultiClassProgramFactory(){
		super(Config.getInstance().numFeatures, Config.getInstance().numRegisters);
	}

	public MultiClassProgram createProgram(int size) {
		return new MultiClassProgram(size);
	}


	public MultiClassProgram createProgram(MultiClassProgram prog) {
		return new MultiClassProgram((MultiClassProgram)prog);
	}
}
 