package core;

public abstract class BlueprintFactory<BP extends SPLGP_Blueprint<BP, IPR>,IPR extends IProgram<IPR>>
		extends IProgramFactory<BP> {

	protected IPopulation<?, IPR>[] sub_pops;
	
	public BlueprintFactory(IPopulation<?, IPR>[] sub_pops, int numFeatures, int numRegisters){
		super(numFeatures, numRegisters);
		this.sub_pops = sub_pops;
	}
}
