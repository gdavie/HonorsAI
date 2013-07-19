package gp_operators;

import java.util.ArrayList;

import misc.Rand;
import core.Instruction;
import core.InstructionProgram;

/** Replaces a random sequence of instructions in the first parent with a random sequence of
instructions from the second. The sequences may be of different length. Does *not* randomly
cull to size if it exceeds the maximum length criterion.*/
public class CrossoverFree<INSP extends InstructionProgram<INSP>> extends GP_Operator<INSP>{

	public int execute(INSP p1, INSP p2, INSP res){

		// Subsequence to be removed:
		int firstStart = Rand.Int(p1.size() - 1);
		int firstEnd = firstStart + 1 + Rand.Int(p1.size() - firstStart);

		// Subsequence to be added:
		int secondStart = Rand.Int(p2.size() - 1);
		int secondEnd = secondStart + 1 + Rand.Int(p2.size() - secondStart);

		// Do the transfer...
		ArrayList<Instruction> instrsRes = res.rawAccessToTheInternalInstructions();
		ArrayList<Instruction> instrsSecond = p2.rawAccessToTheInternalInstructions();

		// ... first remove the subsequence from first.
		// Then add the one from second in its place - can't insert a range directly since we're
		// using pointers and they will only be shallow copied:

		
		//This is ugly.. but removing lots of items is worse.
		ArrayList<Instruction> temp = new ArrayList<Instruction>();
		for(int i = 0; i < firstStart; i++){
			temp.add(instrsRes.get(i));
		}
		for(int i = secondStart; i < secondEnd; ++i) {
			temp.add(instrsSecond.get(i).clone());
		}
		for(int i = firstEnd; i < p1.size(); i++){
			temp.add(instrsRes.get(i));
		}

		res.setInstructions(temp);

		return firstStart;
	}

}
