package gp_operators;

import misc.Rand;
import core.APartsProgram;
import core.Config;
import core.InstructionProgram;

/**in this form of crossover we simply exchange parts and DO NOT modify the genetic materal of any given part*/
public class PartCrossover<T extends APartsProgram<T,E>, E extends InstructionProgram<E>> extends GP_Operator<T> {

	@Override
	public int execute(T p1, T p2, T res) {
		int modPart = Rand.Int(p1.parts.length);
		int modPos;
		
		//make a backup of the original program
		if(Config.getInstance().REG_CACHE){
			res.setModPart(modPart);
			E part = res.parts()[modPart];
			E backup = part.clone();
			
			part.SetOldVersion(null);//stops infinite recursion

			//copy the part from the other parent
			modPos = 0;
			res.parts[modPart].reinitialize(p2.parts[modPart]);
			
			res.parts[modPart].SetOldVersion(backup);
		}
		else{
			modPos = 0;
			res.parts[modPart].reinitialize(p2.parts[modPart]);
		}

		return modPos;
	}

}
