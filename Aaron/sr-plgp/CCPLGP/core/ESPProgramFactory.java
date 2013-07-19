package core;

public class ESPProgramFactory extends ManyProgramFactory<ESPProgram,MultiClassProgram> {
	
	public ESPProgramFactory(MultiClassPopulation[] sub_pops, int numFeatures, int numRegisters){
		super(sub_pops, numFeatures, numRegisters);
	}

	public ESPProgram createProgram(int prog_size) {
		return new ESPProgram(prog_size, sub_pops,Config.getInstance().numPieces, numFeatures, numRegisters);
	}

	public ESPProgram createProgram(ESPProgram prog) {
		return new ESPProgram(prog);
	}
}
