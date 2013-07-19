package gp_operators;

import misc.Rand;
import core.APartsProgram;
import core.InstructionProgram;

public class PMChromosoneCrossover<T extends APartsProgram<T,E>, E extends InstructionProgram<E>> extends GP_Operator<T> {

	@Override
	public int execute(T p1, T p2, T res) {
		int mutand = Rand.Int(p1.parts.length);
		res.parts()[mutand] = p2.parts()[mutand].clone();
		
		return mutand;
	}

}
