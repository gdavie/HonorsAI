package core;

public class BSProgramFactory extends ManyProgramFactory<BSProgram,MultiClassProgram> {
	
	public BSProgramFactory(MultiClassPopulation[] sub_pops, int numFeatures, int numRegisters){
		super(sub_pops, numFeatures, numRegisters);
	}

	public BSProgram createProgram(int prog_size) {
		return new BSProgram(prog_size, sub_pops,Config.getInstance().numPieces, numFeatures, numRegisters);
	}

	public BSProgram createProgram(BSProgram prog) {
		return new BSProgram(prog);
	}
}
