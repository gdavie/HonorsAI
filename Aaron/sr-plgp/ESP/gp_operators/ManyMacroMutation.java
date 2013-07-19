package gp_operators;

import misc.Rand;
import core.IProgram;
import core.ManyProgram;

public class ManyMacroMutation<MPR extends ManyProgram<MPR, IPR>, IPR extends IProgram<IPR>> extends GP_Operator<MPR> {

	@Override
	public int execute(MPR p1, MPR p2, MPR res) {
		int mutand = Rand.Int(res.parts().length);
		int new_prog_index = Rand.Int(res.sub_pops[mutand].size());

		IPR part = res.getProgram(mutand, new_prog_index);
		res.replacePart(mutand, part);
		
		//System.out.println("part:index " + mutand + ":" + new_prog_index);
		
		return mutand;
	}

}
