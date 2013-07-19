package core;


public class ESPProgram extends ManyProgram<ESPProgram, MultiClassProgram> {

	public ESPProgram(int size, IPopulation<?, MultiClassProgram>[] sub_pops, int numPieces, int numFeatures, int numRegisters) {
		super(size, sub_pops, numPieces, numFeatures, numRegisters);
	}

	public ESPProgram(ESPProgram prog){
		super(prog);
	}
	
	@Override
	public void execute(FitnessEnvironment<?> fe, RegisterCollection rc,
			int num) {
		boolean pm = true;//sets whether PM is used or not
		if(!pm){
			for(int i = 0; i < parts.length; i++){
				parts[i].execute(fe, rc, num);
			}
			rc.copyRegisters(fe.getRegisters());
		}else{
			RegisterCollection temp_rc = new RegisterCollection(rc.size());
			temp_rc.zeroRegisters();
			for(int i = 0; i < parts.length; i++){
				fe.zeroRegisters();
				parts[i].execute(fe, null, num);
				temp_rc.add(fe.getRegisters());
			}
			rc.copyRegisters(temp_rc);
		}
		
	}

	@Override
	public ESPProgram clone(){
		return new ESPProgram(this);
	}

	@Override
	protected MultiClassProgram[] createBackingArray(int length) {
		return new MultiClassProgram[length];
	}


}
