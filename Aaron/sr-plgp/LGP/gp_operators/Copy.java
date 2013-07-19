package gp_operators;

import core.InstructionProgram;

/** this operator INTENTIONALLY DOES NOTHING. used for ESP*/
public class Copy<IPR extends InstructionProgram<IPR>> extends GP_Operator<IPR> {

	@Override
	public int execute(IPR p1, IPR p2, IPR res) {
		return 0;
	}

}
