package core;

public abstract class APartsProgram<APP extends APartsProgram<APP,IPR>,IPR extends IProgram<IPR>>
extends	IProgram<APP> {

	public IPR[] parts;
	protected int numPieces;

	//IFDEF REG_CACHE
	private int modPart;
	protected Cache reg_cache;

	public APartsProgram(IFitnessMeasure tfm, IFitnessMeasure vfm, int numPieces, int numFeatures, int numRegisters) {
		super(tfm, vfm, numFeatures, numRegisters);
		parts = createBackingArray(numPieces);
		this.numPieces = numPieces;

		//IFDEF REG_CACHE
		if(config.REG_CACHE){
			modPart = -1;
			reg_cache = new Cache();
		}
	}

	/** clone constructor*/
	public <X extends APartsProgram<X,IPR>> APartsProgram(APartsProgram<X, IPR> app){
		super(app); 

		parts = createBackingArray(app.parts.length);

		numPieces = app.numPieces;

		int i = 0;
		for(IPR part : app.parts){
			parts[i] = part.clone();
			i++;
		}

		//IFDEF REG_CACHE
		if(config.REG_CACHE){
			modPart = app.modPart;
			reg_cache = app.reg_cache.clone();
		}
	}

	public void reinitialize(APP rhs){
		super.reinitialize(rhs);
		
		//IFDEF REG_CACHE
		if(config.REG_CACHE){
			modPart = rhs.modPart;
			reg_cache.reinitialise(rhs.reg_cache);
		}
	}


	@Override
	public <F extends IFitnessCase> void updateFitness(FitnessEnvironment<F> tfe, FitnessEnvironment<F> vfe){
		// Zero the fitness to a null state, mark the introns to optimise execution time:
		super.updateFitness(tfe, vfe);
		setFitnessStatus(false);
	}

	@Override
	public int size() {
		int sum = 0;
		for(int i = 0; i < parts.length; i++){
			sum += parts[i].size();
		}
		return sum;
	}

	@Override
	public String toString(boolean printFitness, boolean commentIntrons) {
		String s = "";
		for(int i = 0; i < parts.length; i++){
			s += parts[i].toString(printFitness, commentIntrons);
		}
		return s;
	}


	public String toString(){
		return toString(true,true);
	}

	public IPR[] parts(){
		return parts;
	}

	/**updates pointer only*/
	public void replacePart(int i, IPR part){
		parts[i] = part;
	}

	public Cache getReg_cache() {
		return reg_cache;
	}
	
	public int modPart(){
		return modPart;
	}
	
	public void setModPart(int modPart){
		this.modPart = modPart;
	}
	
	protected abstract IPR[] createBackingArray(int length);

}