package gp_operators;

import misc.Rand;
import core.APartsProgram;
import core.Config;
import core.InstructionProgram;

/** This mutation operator chooses a random part and performs a macro mutation on it*/
public class PMMutation<T extends APartsProgram<T,E>, E extends InstructionProgram<E>> extends GP_Operator<T> {

	private MacroMutation<E> mm = new MacroMutation<E>();
	
	@Override
	public int execute(T p1, T p2, T res) {
		int modPart = Rand.Int(p1.parts().length);
		int modPos;
		
		//make a backup of the original program
		if(Config.getInstance().REG_CACHE){
			res.setModPart(modPart);
			E part = res.parts()[modPart];
			E backup = part.clone();
			
			part.SetOldVersion(null);//stops infinite recursion
			modPos = mm.execute(p1.parts()[modPart], p2.parts[modPart], res.parts[modPart]);
			
			res.parts[modPart].SetOldVersion(backup);
		}
		else{
			modPos = mm.execute(p1.parts()[modPart], p2.parts[modPart], res.parts()[modPart]);
		}

		return modPos;
	}

}
