package execute;

import core.APartsProgram;
import core.FitnessEnvironment;
import core.IProgram;
import core.RegisterCollection;

public class PME_basic<E extends IProgram<E>> extends PME<E> {

	@Override
	public void execute(FitnessEnvironment<?> fe, RegisterCollection rc, int num, APartsProgram<?,E> app){
		
		IProgram<E>[] parts = app.parts();

		RegisterCollection temp_rc = new RegisterCollection(rc.size());
		temp_rc.zeroRegisters();
		for(int i = 0; i < parts.length; i++){
			fe.zeroRegisters();
			parts[i].execute(fe, null, num);
			temp_rc.add(fe.getRegisters());
		}

		// Now store finalRegisterValues
		rc.copyRegisters(temp_rc);
	}
}
