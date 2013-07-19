package gp_operators;

import misc.WeightedCollection;
import core.Instruction;
import core.MultiClassProgram;

/** as addRandomInstruction, except the destination register is chosen at random
 * proportional to how effective the program is at determining each class. I.e. if
 * the program performs poorly on class 3, the destination register is more like to be
 * 3.
 */
public class SelectiveAddRandomInstruction extends GP_Operator<MultiClassProgram>{

	public int execute(MultiClassProgram p1, MultiClassProgram p2, MultiClassProgram res){

		double[] resps = p1.getResps();

		WeightedCollection<Integer> wc = new WeightedCollection<Integer>();
		for(int i = 0; i < resps.length; i++){
			wc.addElement(i, resps[i]);
		}

		int index = wc.getRandomElement();
		Instruction ins = res.instructionFactory();
		ins.setDest(index);
		
		int modPos = res.size();
		
		res.insertInstruction(res.size(), ins);

		return modPos;
	}

}
