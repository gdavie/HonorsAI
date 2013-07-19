package gp_operators;

import core.IProgram;

public abstract class GP_Operator<IPR extends IProgram<IPR>> {

	/**
	 * 
	 * @param p1 the first program. res is a copy of this program
	 * @param p2 optional second program. only used for crossover methods
	 * @param res the program to modify
	 * @return the position at which the first program was modified. ModPos
	 */
	public abstract int execute(IPR p1, IPR p2, IPR res);

}
