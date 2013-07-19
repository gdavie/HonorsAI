package core;

import core.Config;
import core.SPLGP_Program;
import core.SPLGP_Pfactory;
import core.SPLGP_Population;
import core.MultiClassPopulation;
import core.SPLGP;

public class HCS_Population extends SPLGP<HCS_Population, HCS_Program, SPLGP_Program>{

	public HCS_Population(SPLGP_Population[] sub_pops, int numFeatures, int numRegisters){
		super(sub_pops, new HCS_ProgramFactory(sub_pops, numFeatures, numRegisters), numFeatures, numRegisters);
	} 
//original one
	public static HCS_Population create(SPLGP_Pfactory mcpf, int numFeatures, int numRegisters, boolean dbg) {
		SPLGP_Population[] sub_pops = new SPLGP_Population[Config.getInstance().numPieces]; //TODO create these
		for(int i = 0; i < sub_pops.length; i++){
			sub_pops[i] = new SPLGP_Population(Config.getInstance().populationSize/Config.getInstance().numPieces
													,numFeatures, numRegisters);
		}

		return new HCS_Population(sub_pops, numFeatures, numRegisters);
	}

}
