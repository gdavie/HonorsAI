package hash;

import java.util.List;

import core.Instruction;
import core.InstructionProgram;

public class SimpleHash<INSP extends InstructionProgram<INSP>> extends Hash<INSP,Integer> {

	@Override
	public Integer hash(INSP program) {
		List<Instruction> instructions = program.rawAccessToTheInternalInstructions();
		int hash = 0;
		for(int i = 0; i < instructions.size(); i++){
			Instruction ins = instructions.get(i);
			
			int dest = ins.destinationIndex();
			
			int v1 = ins.firstArgumentIndex();
			int t1 = ins.firstArgumentType();
			
			int v2 = ins.firstArgumentIndex();
			int t2 = ins.firstArgumentType();
		}
		throw new RuntimeException("method in error");
	}

	
	
}
