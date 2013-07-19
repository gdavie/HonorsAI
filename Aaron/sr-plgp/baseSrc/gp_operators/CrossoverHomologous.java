package gp_operators;

import misc.Rand;
import core.Instruction;
import core.InstructionProgram;
import core.MultiClassProgram;

/** Replaces a random sequence of instructions in the first parent with a random sequence of
instructions from the second. The sequences will be the same length and start at the same
position in both of the parents. If the programs are different size just select again and do 
a free crossover.*/
public class CrossoverHomologous<INSP extends InstructionProgram<INSP>> extends GP_Operator<INSP>{

	private CrossoverFree<INSP> cf = new CrossoverFree<INSP>();
	
	public int execute(INSP p1, INSP p2, INSP res){

		if(p1.size() != p2.size()) {
			return cf.execute(p1, p2, res);
		}
		else {
			// Put copies of second.instructions [start,end) over [start,end) in first:
			int start = Rand.Int(p1.size());
			int end = start + Rand.Int(p1.size() - start) + 1;

			
			/**
			MultiClassProgram mcp = new MultiClassProgram(1);
			MultiClassProgram mcp2 = new MultiClassProgram(1);
			
			System.out.println("mcp: \n" + mcp);
			System.out.println("mcp2: \n" + mcp2);
			
			System.out.println("making changes");
			//mcp.rawAccessToTheInternalInstructions().set(0, mcp2.rawAccessToTheInternalInstructions().get(0).clone());
			
			//mcp.replaceInstruction(0, (Instruction)(mcp2.rawAccessToTheInternalInstructions().get(0)).clone());
			mcp.copyInstruction( mcp2.rawAccessToTheInternalInstructions().get(0), 0);
			
			
			System.out.println("mcp: \n" + mcp);
			System.out.println("mcp2: \n" + mcp2);
			
			mcp.reinitialize(new MultiClassProgram(1));
			
			System.out.println("mcp: \n" + mcp);
			System.out.println("mcp2: \n" + mcp2);
			
			*/
			
			for(int i = start; i < end; ++i) {
				//res.replaceInstruction(i, (Instruction)(p2.rawAccessToTheInternalInstructions().get(i))).clone();
				res.copyInstruction( p2.rawAccessToTheInternalInstructions().get(i), i);
			}

			return start;			   
		}
	}

}
