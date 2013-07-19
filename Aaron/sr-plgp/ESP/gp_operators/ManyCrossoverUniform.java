package gp_operators;

import misc.Rand;
import core.IProgram;
import core.ManyProgram;

public class ManyCrossoverUniform<MPR extends ManyProgram<MPR, IPR>, IPR extends IProgram<IPR>> extends GP_Operator<MPR> {

	@Override
	public int execute(MPR p1, MPR p2, MPR res) {

		if(p1.parts().length != p2.parts().length) {
			System.out.println("blueprints incompatible sizes");
			return 0;
		}
		else {
			//exchange parts
			int i = Rand.Int(p1.parts().length);
			res.replacePart(i, p2.parts()[i]);
			
			return i;
		}
		

	}

}
