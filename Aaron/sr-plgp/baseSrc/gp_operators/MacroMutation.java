package gp_operators;

import misc.Rand;
import core.Instruction;
import core.InstructionProgram;

/**Replaces an entire instruction with a new, randomly generated instruction
 */
public class MacroMutation<INSP extends InstructionProgram<INSP>> extends GP_Operator<INSP>{

	public int execute(INSP p1, INSP p2, INSP res){

		int mutand = Rand.Int(res.size());

		Instruction ins = res.instructionFactory();
		res.replaceInstruction(mutand, ins); 
		
		return mutand;
	}

}
