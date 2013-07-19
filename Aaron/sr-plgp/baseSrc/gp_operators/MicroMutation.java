package gp_operators;

import misc.Rand;
import core.InstructionProgram;

/** replaces a random operator, argument, or destination register in a program 
 * with a new, randomly generated one.
 */
public class MicroMutation<INSP extends InstructionProgram<INSP>> extends AMicroMutation<INSP>{
	
	public MicroMutation(int numFeatures, int numRegisters){
		super(numFeatures, numRegisters);
	}
	
	public int execute(INSP p1, INSP p2, INSP res){
		
		int modPos = Rand.Int(p1.size());
		microMutation(res.rawAccessToTheInternalInstructions().get(modPos));

		return modPos;
	}

}
