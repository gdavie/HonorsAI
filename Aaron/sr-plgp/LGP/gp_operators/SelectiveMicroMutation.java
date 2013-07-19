package gp_operators;

import core.MultiClassProgram;

/** as microMutation(), but bad instructions have a higher likelihood of being selected, 
 * and good instructions have a lower likelihood
 */
public class SelectiveMicroMutation extends AMicroMutation<MultiClassProgram>{

	public SelectiveMicroMutation(int numFeatures, int numRegisters) {
		super(numFeatures, numRegisters);
	}

	public int execute(MultiClassProgram p1, MultiClassProgram p2, MultiClassProgram res){

		int modPos = res.JrandomInstruction();
		
		microMutation(res.rawAccessToTheInternalInstructions().get(modPos));

		return modPos;
	}

}
