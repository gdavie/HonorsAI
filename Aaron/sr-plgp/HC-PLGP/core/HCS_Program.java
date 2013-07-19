package core;


public class HCS_Program extends SPLGP_Blueprint<HCS_Program, SPLGP_Program> {

	public HCS_Program(int size, IPopulation<?, SPLGP_Program>[] sub_pops, int numPieces, int numFeatures, int numRegisters) {
		super(size, sub_pops, numPieces, numFeatures, numRegisters);
	}

	public HCS_Program(HCS_Program prog){
		super(prog);
	}
	
	@Override
	public void execute(FitnessEnvironment<?> fe, RegisterCollection rc,
			int num) {

			RegisterCollection temp_rc = new RegisterCollection(rc.size());
			temp_rc.zeroRegisters();
			for(int i = 0; i < parts.length; i++){
				fe.zeroRegisters();
				parts[i].execute(fe, null, num);
				temp_rc.add(fe.getRegisters());
			}
			rc.copyRegisters(temp_rc);	
	}

	@Override
	public HCS_Program clone(){
		return new HCS_Program(this);
	}

	@Override
	protected SPLGP_Program[] createBackingArray(int length) {
		return new SPLGP_Program[length];
	}


}
