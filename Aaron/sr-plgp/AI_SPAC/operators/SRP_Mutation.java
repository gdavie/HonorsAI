package operators;

import misc.Rand;
import modelling.LayerGenerator;
import core.SProgram;

/** This mutation operator chooses a random layer and performs a macro mutation on it*/
public class SRP_Mutation extends SRP_Operator{
	
	
	/**
	 * This mutation procedure will change a single parameter from a single layer,
	 * for example, the thickness of layer 1, to a random value.
	 * the LayerGenerator class is used so that we will have a single point
	 * when we wish to refine the range of randomness.
	 * 
	 * @param p1 the parent
	 * @param p2 not used for mutation, but part of the interface
	 * @param res : the resultant child
	 */
	@Override
	public void execute(SProgram p1, SProgram p2, SProgram res) {
		int layerToMod = Rand.Int(p1.layers().length);
		
		int parameterToMod = Rand.Int(2);
		String param = "xx";
		
		if(parameterToMod>=1){
			if(DEBUGMUTATION)param="V_s";
			res.layers[layerToMod].setVelocity(LayerGenerator.randomVs());
		}else{
			if(DEBUGMUTATION)param="H";
			res.layers[layerToMod].setThickness(LayerGenerator.randomH());
		}
		if(DEBUGMUTATION)System.out.println("Mutation called "+layerToMod+" : "+param);
	}

}
