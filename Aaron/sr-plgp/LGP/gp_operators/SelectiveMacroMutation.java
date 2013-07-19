package gp_operators;

import core.Instruction;
import core.MultiClassProgram;

/** as macroMutation(), but bad instructions have a higher likelihood of being selected, 
 * and good instructions have a lower likelihood
 */
public class SelectiveMacroMutation extends GP_Operator<MultiClassProgram>{

	public int execute(MultiClassProgram p1, MultiClassProgram p2, MultiClassProgram res){

		int mutand = res.JrandomInstruction();

		// Assume a population shares its config with the instructions, so just pass the its one in:
		Instruction ins = res.instructionFactory();
		res.replaceInstruction(mutand, ins);

		return mutand;
	}

}
