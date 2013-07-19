package Killmes;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.enums.ValueType;

import Fitness.SRPE;
import Fitness.SRP_FitnessEnvironment;
import Fitness.SRP_FitnessMeasure;
import dbg.RegisterEvaluator;

public final class SRP_Model extends SRP_APartsProgram<SRP_Model, MultiClassProgram> {

	public Matrix weights = null;

	private static SRPE execute;

	private static boolean weighted = false;
	public Layer[] layers;

	/**normal constructor*/
	public SRP_Model(int prog_size, SRP_ModelFactory modelFactory){
		
		/*
		 * model consists of one or more parts,or layers.
		 */
		//TODO hardcoded 5 here as numPieces, nasty work :P
		super(new SRP_FitnessMeasure(), new SRP_FitnessMeasure(), 5, 
				Config.getInstance().numFeatures, Config.getInstance().numRegisters);
		layers = new Layer[5];
		for(int i = 0; i < 5; i++){//TODO hardcoded 5 as numPieces here
			layers[i] = modelFactory.createLayer();
		}

		if(execute == null)
			execute = new SRPE();
	}

	public SRP_Model(SRP_Model program){
		super(program);
	}

	/** this constructor is used to convert a multipopulation cooperative coevolution program
	 * into a parallel multiclw.gass program
	 * @param mp
	 * @param partFactory
	 */
	public SRP_Model(ManyProgram<?, MultiClassProgram> mp, IProgramFactory<MultiClassProgram> partFactory){
		super(mp);

		if(weighted)
			weights = MatrixFactory.zeros(ValueType.DOUBLE, numPieces, 1);

	}

	@Override
	public void reinitialize(SRP_Model cp){
		super.reinitialize(cp);
		
		for(int i = 0; i < cp.layers.length; i++){
//			layers[i].reinitialize(cp.layers[i]); //always copy entirely. these ARE a deep clone
		}
	}

	@Override
	public void markIntrons() {
		//Do Nothing
	}

	@Override
	public int randomlyCullToSize(int maxLength) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public SRP_Model clone() {
		return new SRP_Model(this);
	}

	
	/*
	 * TODO rc is not required.
	 * 
	 */
	public void execute(FitnessEnvironment<?> fe) {
			execute.execute(fe, this);

	}
	
	public Layer[] layers(){
		return layers;
	}

	@Override
	protected MultiClassProgram[] createBackingArray(int length) {
		System.out.println("DOdgy call to createBackingArray");
		return new MultiClassProgram[length];
	}

	
	public <F extends IFitnessCase> void updateFitness(SRP_FitnessEnvironment<F> tfe) {
			execute.updateFitness(tfe, this);
	}

	@Override
	public void markIntrons(RegisterEvaluator re) {
		//AzL Does nothing. Simply provides override for Slotted PLGP		
	}
	public <F extends IFitnessCase>void updateType(FitnessEnvironment<F> fe, IFitnessMeasure ifm){
		// calculate training fitness
		if (!fe.loadFirstCase()) {
			throw new RuntimeException("No fitness cases in fe to evaluate against");
		}
		int count = 0;
		do {
			fe.zeroRegisters();

			execute(fe);
			ifm.updateError( fe.currentCase());

			count++;
		} while (fe.loadNextCase());	
	}

	@Override
	public void execute(FitnessEnvironment<?> fe, RegisterCollection rc,
			int caseNum) {
		System.out.println("SRP_Model --> Wrong execute call");
		
	}
	
}
