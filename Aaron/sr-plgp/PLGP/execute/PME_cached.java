package execute;

import core.APartsProgram;
import core.Cache;
import core.FitnessEnvironment;
import core.IProgram;
import core.RegisterCollection;

public class PME_cached<E extends IProgram<E>> extends PME<E>{

	@Override
	public void execute(FitnessEnvironment<?> fe, RegisterCollection rc, int num, APartsProgram<?,E> app) {

		Cache reg_cache = app.getReg_cache();

		boolean isTrain = fe.type == FitnessEnvironment.TRAIN;

		E[] parts = app.parts();
		
		//IFNODEF cache
		if(!config.REG_CACHE || app.modPart() < 0 || fe.type == FitnessEnvironment.TEST){//-1 implies cloned
			
			RegisterCollection temp_rc = new RegisterCollection(rc.size());
			temp_rc.zeroRegisters();
			for(int i = 0; i < parts.length; i++){
				fe.zeroRegisters();
				parts[i].execute(fe, null, num);
				temp_rc.add(fe.getRegisters());
			}

			// Now store finalRegisterValues
			rc.copyRegisters(temp_rc);

			if(config.REG_CACHE && fe.type != FitnessEnvironment.TEST){
				reg_cache.update(rc, num, isTrain);
			}
		}
		else{
			RegisterCollection cached = reg_cache.get(num, isTrain);

			fe.zeroRegisters();
			parts[app.modPart()].old_version.execute(fe, null, num);
			cached.sub(fe.getRegisters());

			fe.zeroRegisters();
			parts[app.modPart()].execute(fe, null, num);
			cached.add(fe.getRegisters());

			rc.copyRegisters(cached);

			reg_cache.update(rc, num, isTrain);
		}
	}

}
