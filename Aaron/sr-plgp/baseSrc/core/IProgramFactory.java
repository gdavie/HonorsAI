package core;

public abstract class IProgramFactory<E> {

	protected int numFeatures;

	protected int numRegisters;
	
	public IProgramFactory(int numFeatures, int numRegisters){
		this.numFeatures = numFeatures;
		this.numRegisters = numRegisters;
	}
	
	public abstract E createProgram(int size);
	
	public abstract E createProgram(E prog);
		
}
