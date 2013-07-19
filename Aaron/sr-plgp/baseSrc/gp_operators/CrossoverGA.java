package gp_operators;

import misc.Rand;
import core.InstructionProgram;

/** Replaces a random sequence of instructions in the first parent with a random sequence of
instructions from the second. The sequences will always be the same length but may come
from different positions in the second parent. If the programs are different size just select
again and do a free crossover.*/
public class CrossoverGA<INSP extends InstructionProgram<INSP>> extends GP_Operator<INSP>{

	private CrossoverFree<INSP> cf = new CrossoverFree<INSP>();

	public int execute(INSP p1, INSP p2, INSP res){

		if(p1.size() != p2.size()) {
			return cf.execute(p1,p2,res);
		}
		else {
			// Put copies of second.instructions [start,end) over [start,end) in first:
			int start = Rand.Int(p1.size());
			int end = start + Rand.Int(p1.size() - (start + 1));

			int secondStart = Rand.Int(p2.size() - (end - start));
			int secondEnd = secondStart + (end - start);

			int iFirst = start; // Move these out of the loop declaration just to
			int iSecond = secondStart; // make shorter, so it fits on just one line.

			for(; iFirst < end && iSecond < secondEnd; ++iFirst, ++iSecond) {
				res.copyInstruction(p2.rawAccessToTheInternalInstructions().get(iSecond), iFirst);
			}

			return start;
		}
	}

}
