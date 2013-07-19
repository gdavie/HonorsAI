package core;

public class ESPPopulation extends ManyPopulation<ESPPopulation, ESPProgram, MultiClassProgram>{

	public ESPPopulation(MultiClassPopulation[] sub_pops, int numFeatures, int numRegisters){
		super(sub_pops, new ESPProgramFactory(sub_pops, numFeatures, numRegisters), numFeatures, numRegisters);
	} 

	public static ESPPopulation create(MultiClassProgramFactory mcpf, int numFeatures, int numRegisters) {
		MultiClassPopulation[] sub_pops = new MultiClassPopulation[Config.getInstance().numPieces]; //TODO create these
		for(int i = 0; i < sub_pops.length; i++){
			sub_pops[i] = new MultiClassPopulation(Config.getInstance().populationSize/Config.getInstance().numPieces
													,numFeatures, numRegisters);
		}

		return new ESPPopulation(sub_pops, numFeatures, numRegisters);
	}
	
}
