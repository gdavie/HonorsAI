package core;

/** this program is identical to an ESP program program*/
public class BSProgram extends ManyProgram<BSProgram, MultiClassProgram> {

	//THE BIG CHANGE: our program now keeps track of indices
	public int[] indices = new int[config.numPieces];
	
	public BSProgram(int size, IPopulation<?, MultiClassProgram>[] sub_pops, int numPieces, int numFeatures, int numRegisters) {
		super(size, sub_pops, numPieces, numFeatures, numRegisters);
	}

	public BSProgram(BSProgram prog){
		super(prog);
	}
	
	//Matches the indices to the programs
	public void matchIndices(){
		for(int i = 0; i < config.numPieces; i++){
			parts[i] = sub_pops[i].get(indices[i]);
		}
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
	public BSProgram clone(){
		return new BSProgram(this);
	}

	@Override
	protected MultiClassProgram[] createBackingArray(int length) {
		return new MultiClassProgram[length];
	}


}
