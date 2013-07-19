package operators;

import java.util.Random;

import gp_operators.MacroMutation;
import misc.Rand;
import modelling.LayerGenerator;
import core.Config;
import core.Instruction;
import core.Layer;
import core.SProgram;

/** This mutation operator chooses a random layer and performs a macro mutation on it*/
public class SRP_Modify_mul extends SRP_Modify{
	
	/**
	 * Modifies a single parameter ie layer 1 V_s.
	 * 
	 * Currently, this will branch out to random modifications as a proof of concept,
	 * although we should create different class for each operator to give better 
	 * granularity. 
	 * 
	 * @param p1 parent 1
	 * @param p2 not used here, but part of the interface
	 * @param res : The resultant child
	 */
	@Override
	public void execute(SProgram p1, SProgram p2, SProgram res) {
		int layerToMod = Rand.Int(p1.layers().length);
		
		double value=0.0;
		
		int parameterToMod = Rand.Int(2);
		String param = "xx";
		
		r = new Random();
		double modVal =  0.0;
		
		if(parameterToMod>=1){//VS				
			modVal =  1 + (divMultRangeVS) * r.nextDouble();
			value= res.layers[layerToMod].getVelocity();
			res.layers[layerToMod].setVelocity(value*modVal);
			if(DEBUGMODIFY){
				param="V_s";
				System.out.println(value);
			}
		}else{//H
			modVal =  1 + (divMultRangeH) * r.nextDouble();
			value= res.layers[layerToMod].getThickness();
			res.layers[layerToMod].setThickness(value*modVal);
			if(DEBUGMODIFY){
				param="H";
				System.out.println(value);
			}
		}
		if(DEBUGMODIFY)System.out.println("Modify MUL called "+layerToMod+" : "+param+" : "+modVal);
	}

}
