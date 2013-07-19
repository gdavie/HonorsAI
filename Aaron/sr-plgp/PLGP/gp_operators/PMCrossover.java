package gp_operators;

import misc.Rand;
import core.APartsProgram;
import core.Config;
import core.InstructionProgram;

/** This crossover operator chooses an equivilent random part (1-1, 2-2 etc) from each program and performs 
 * crossover between them*/
public class PMCrossover<T extends APartsProgram<T,E>, E extends InstructionProgram<E>> extends GP_Operator<T> {

	private CrossoverHomologous<E> ch = new CrossoverHomologous<E>();

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
			
			modPos = ch.execute(p1.parts[modPart], p2.parts[modPart], res.parts[modPart]);
			
			res.parts[modPart].SetOldVersion(backup);
		}
		else{
			modPos = ch.execute(p1.parts[modPart], p2.parts[modPart], res.parts[modPart]);
		}

		return modPos;
	}

}
