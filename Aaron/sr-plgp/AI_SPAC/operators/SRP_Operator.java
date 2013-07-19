package operators;

import core.SProgram;

public abstract class SRP_Operator{

	protected final boolean DEBUGMODIFY=false;
	protected final boolean DEBUGMUTATION=false;
	protected final boolean DEBUGXOVER=false;
	/**
	 * 
	 * @param p1 the first program. res is a copy of this program
	 * @param p2 optional second program. only used for crossover methods
	 * @param res the program to modify
	 * @return the position at which the first program was modified. ModPos
	 */
	public abstract void execute(SProgram p1, SProgram p2, SProgram res);

}
