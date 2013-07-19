package core;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.enums.ValueType;

import dbg.RegisterEvaluator;

import execute.PME;
import execute.PME_cached;
import execute.PME_weighted_single;



public final class PMProgram extends APartsProgram<PMProgram, MultiClassProgram> {

	public Matrix weights = null;

	private static PME<MultiClassProgram> execute;
	private static PME<MultiClassProgram> weighted_execute;

	private static boolean weighted = false;

	/**normal constructor*/
	public PMProgram(int prog_size, IProgramFactory<MultiClassProgram> partFactory, int numPieces){
		super(new MultiClassFitnessMeasure(), new MultiClassFitnessMeasure(), numPieces, 
				Config.getInstance().numFeatures, Config.getInstance().numRegisters);

		for(int i = 0; i < config.numPieces; i++){
			parts[i] = partFactory.createProgram(prog_size);
		}

		if(weighted)
			weights = MatrixFactory.zeros(ValueType.DOUBLE, numPieces, 1);

		if(execute == null)
			execute = new PME_cached<MultiClassProgram>();
		//execute = new PME_weighted_single<MultiClassProgram>(numRegisters,numPieces);
		//execute = new PME_hybrid<MultiClassProgram>(numRegisters,numPieces);
		//execute = new PME_basic<MultiClassProgram>();
		if(weighted_execute == null && weighted){
			weighted_execute = new PME_weighted_single<MultiClassProgram>(numRegisters,numPieces);
		}
	}

	public PMProgram(PMProgram program){
		super(program);
		if(weighted)
			weights = program.weights.clone();//couldnt find a way to copy
	}

	/** this constructor is used to convert a multipopulation cooperative coevolution program
	 * into a parallel multiclw.gass program
	 * @param mp
	 * @param partFactory
	 */
	public PMProgram(ManyProgram<?, MultiClassProgram> mp, IProgramFactory<MultiClassProgram> partFactory){
		super(mp);

		if(weighted)
			weights = MatrixFactory.zeros(ValueType.DOUBLE, numPieces, 1);

	}

	@Override
	public void reinitialize(PMProgram cp){
		super.reinitialize(cp);
		
		for(int i = 0; i < cp.parts.length; i++){
			parts[i].reinitialize(cp.parts[i]); //always copy entirely. these ARE a deep clone
		}

		if(weighted)
			weights = cp.weights.clone();//couldnt find a way to copy
	}

	@Override
	public void markIntrons() {
		for(MultiClassProgram sp : parts){
			sp.markIntrons();
		}
	}

	@Override
	public int randomlyCullToSize(int maxLength) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public PMProgram clone() {
		return new PMProgram(this);
	}

	@Override
	public void execute(FitnessEnvironment<?> fe, RegisterCollection rc,
			int num) {
		if(weighted)
			weighted_execute.execute(fe, rc, num, this);	
		else
			execute.execute(fe, rc, num, this);

	}

	@Override
	protected MultiClassProgram[] createBackingArray(int length) {
		return new MultiClassProgram[length];
	}

	@Override
	public <F extends IFitnessCase> void updateFitness(FitnessEnvironment<F> tfe, FitnessEnvironment<F> vfe) {
		if(weighted)
			weighted_execute.updateFitness(tfe, vfe, this);
		else
			execute.updateFitness(tfe, vfe, this);
	}

	public static void alternate(){
		weighted = !weighted;
	}
	@Override
	public void markIntrons(RegisterEvaluator re) {
		//AzL Does nothing. Simply provides override for Slotted PLGP		
	}
}
