package gp_operators;

import core.Config;
import core.IProgram;
import core.IProgramFactory;
import core.ManyProgram;

public class ManyRandomBlueprint<MPR extends ManyProgram<MPR,IPR>,IPR extends IProgram<IPR>> extends GP_Operator<MPR> {

	private IProgramFactory<MPR> factory;
	
	public ManyRandomBlueprint( IProgramFactory<MPR> factory){
		this.factory = factory;
	}
	
	@Override
	public int execute(MPR p1, MPR p2, MPR res) {
		int mutand = 0;
		
		res.reinitialize(factory.createProgram(Config.getInstance().maxLength));
		
		return mutand;
	}

}
