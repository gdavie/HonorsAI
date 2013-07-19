package core;

public class PMProgramFactory extends IProgramFactory<PMProgram> {

	public PMProgramFactory(){
		super(Config.getInstance().numFeatures, Config.getInstance().numRegisters);
	}
	

	public PMProgram createProgram(int prog_size) {
		return new PMProgram(prog_size, new MultiClassProgramFactory(), Config.getInstance().numPieces);
	}

	public PMProgram createProgram(PMProgram prog) {
		return new PMProgram(prog);
	}
}
