package core;

public class HCS_ProgramFactory extends BlueprintFactory<HCS_Program,SPLGP_Program> {
	
	public HCS_ProgramFactory(SPLGP_Population[] sub_pops, int numFeatures, int numRegisters){
		super(sub_pops, numFeatures, numRegisters);
	}

	public HCS_Program createProgram(int prog_size) {
		return new HCS_Program(prog_size, sub_pops,Config.getInstance().numPieces, numFeatures, numRegisters);
	}

	public HCS_Program createProgram(HCS_Program prog) {
		return new HCS_Program(prog);
	}
}
