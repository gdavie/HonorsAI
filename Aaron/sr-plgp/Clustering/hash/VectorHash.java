package hash;

import core.Instruction;
import core.InstructionProgram;


public class VectorHash<INSP extends InstructionProgram<INSP>> extends Hash<INSP,int[]>{

	public static final int OFFSET = 100;
	
	@Override
	public int[] hash(INSP program) {
		int[] ret = new int[program.size()*4];

		int j=0;
		for(int i = 0; i < program.size(); i++){
			Instruction ins = program.rawAccessToTheInternalInstructions().get(i);
			
			ret[j++] = ins.destinationIndex();
			
			int v1 = ins.getFirstArgument().hashCode();
			int t1 = ins.firstArgumentType()*OFFSET;
			ret[j++] = v1+t1;//TODO type + index compressed into single value
			
			ret[j++] = ins.getOp().index();
			
			int v2 = ins.getSecondArgument().hashCode();
			int t2 = ins.firstArgumentType()*OFFSET;
			ret[j++] = v2+t2;//TODO type + index compressed into single value
		}
		
		return ret;
	}

}
