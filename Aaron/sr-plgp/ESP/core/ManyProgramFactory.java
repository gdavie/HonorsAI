package core;

public abstract class ManyProgramFactory<MPR extends ManyProgram<MPR, IPR>,IPR extends IProgram<IPR>>
		extends IProgramFactory<MPR> {
	
	protected IPopulation<?, IPR>[] sub_pops;
	
	public ManyProgramFactory(IPopulation<?, IPR>[] sub_pops, int numFeatures, int numRegisters){
		super(numFeatures, numRegisters);
		this.sub_pops = sub_pops;
	}
}
