package core;

/**
 * Extends IProgramFactory<SPLGP_Program>
 * 
 * @author AJ Scoble
 *
 */
public class SPLGP_Pfactory extends IProgramFactory<SPLGP_Program> {
	
	public SPLGP_Pfactory(){
		super(Config.getInstance().numFeatures, Config.getInstance().numRegisters);
	}

	public SPLGP_Program createProgram(int size) {
		return new SPLGP_Program(size);
	}


	public SPLGP_Program createProgram(SPLGP_Program prog) {
		return new SPLGP_Program((SPLGP_Program)prog);
	}
}
 