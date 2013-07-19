package gp_operators;

import core.InstructionProgram;

/** Deletes a randomly selected instruction from a program*/
public class RemoveRandomInstruction<IPR extends InstructionProgram<IPR>> extends GP_Operator<IPR>{

	private MacroMutation<IPR> mm = new MacroMutation<IPR>();
	
	public int execute(IPR p1, IPR p2, IPR res){

		if(res.size() == 1){
			// Regress to macro-mutation in the case of a remove on a size 1 program:
			return mm.execute(p1, p2, res);
		} 

		int modPos = res.removeRandomInstruction();

		return modPos;
	}

}
