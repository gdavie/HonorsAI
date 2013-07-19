package gp_operators;

import core.MultiClassProgram;

/** ass removeRandomInstruction(), but bad instructions are more likely to be removed
 */
public class SelectiveRemoveRandomInstruction extends GP_Operator<MultiClassProgram>{

	SelectiveMacroMutation smm = new SelectiveMacroMutation();

	public int execute(MultiClassProgram p1, MultiClassProgram p2, MultiClassProgram res){

		if(res.size() == 1){
			// Regress to macro-mutation in the case of a remove on a size 1 program:
			return smm.execute(p1, p2, res);
		}

		int modPos = res.JrandomInstruction();
		res.removeInstruction(modPos);

		return modPos;
	}

}
