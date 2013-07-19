package Killmes;

public class SRP_ModelFactory extends IProgramFactory<SRP_Model> {

	public SRP_ModelFactory(){
		super(Config.getInstance().numFeatures, Config.getInstance().numRegisters);
		
	}
	
	/*
	 * We will want to change this parameter so it relates to the number of layers?
	 * @see core.IProgramFactory#createProgram(int)
	 */
	public SRP_Model createProgram(int prog_size) {
//		return new SRP_Model(prog_size, new SRP_ModelFactory(), Config.getInstance().numPieces);
		return new SRP_Model(prog_size, new SRP_ModelFactory());
	}

	public SRP_Model createProgram(SRP_Model prog) {
		return new SRP_Model(prog);
	}
	
	public Layer createLayer(){
		//TODO make sensible randomness here
		return new Layer(1.0, 9.9);
	}
}
