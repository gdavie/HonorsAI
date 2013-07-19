package gp_operators;

import misc.Rand;
import core.Instruction;
import core.InstructionProgram;

/** Inserts a new, randomly generated instruction as some random point in the 
 * program
 */
public class AddRandomInstruction<INSP extends InstructionProgram<INSP>> extends GP_Operator<INSP>{

	public int execute(INSP p1, INSP p2, INSP res){

		Instruction ins = res.instructionFactory();
		int modPos = Rand.Int(res.size() + 1);
		res.insertInstruction(modPos, ins);

		return modPos;
	}

}
