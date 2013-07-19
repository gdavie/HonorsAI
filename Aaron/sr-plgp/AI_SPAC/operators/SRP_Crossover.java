package operators;

import misc.Rand;
import core.Config;
import core.Layer;
import core.SProgram;

/**in this form of crossover we simply exchange parts and DO NOT modify the genetic materal of any given part*/
public class SRP_Crossover extends SRP_Operator{

	
	/**
	 * Simply swaps a single layer between the two parents
	 * @param p1 parent 1
	 * @param p2 parent 2
	 * @param res : The resultant child
	 */
	@Override
	public void execute(SProgram p1, SProgram p2, SProgram res) {
		int layerToMod = Rand.Int(p1.layers.length);
		if(DEBUGXOVER)System.out.println("Xover called "+layerToMod);
		res.layers[layerToMod].reinitialize(p2.layers[layerToMod]);
	}

}
