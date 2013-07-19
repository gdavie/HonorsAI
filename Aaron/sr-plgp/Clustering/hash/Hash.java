package hash;

import core.InstructionProgram;

public abstract class Hash<INSP extends InstructionProgram<INSP>, X> {

	public abstract X hash(INSP program);
	
}
