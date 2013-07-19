package gp_operators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import misc.WeightedCollection;
import core.Config;
import core.MultiClassProgram;

/** Exchanges only entire class graphs as above. However in addition we use a heuristic
 * to predict which class graphs we should exchange in order to obtain the optimal result.
 */
public class SelectiveCrossover extends GP_Operator<MultiClassProgram>{

	public int execute(MultiClassProgram p1, MultiClassProgram p2, MultiClassProgram res){

		//the classes we are exchanging alleles from 
		Set<Integer> classes = new HashSet<Integer>();

		double[] diff = p1.diff(p2);

		WeightedCollection<Integer> wc = new WeightedCollection<Integer>();

		for(int i = 0; i < Config.getInstance().numClasses; i++){
			wc.addElement(i, Math.max(diff[i], 1));
		}

		int numSwap = (int)(Math.random()*Config.getInstance().numClasses);

		for(int i = 0; i < numSwap; i++){
			classes = wc.getRandomElements(numSwap);
		}

		ArrayList<Integer> ins1 = p1.alleleInstructions(classes);
		ArrayList<Integer> ins2 = p2.alleleInstructions(classes);

		int min = Math.min(ins1.size(), ins2.size());

		int modPos;
		if(ins1.isEmpty()){
			modPos = p1.size()-1;
		}
		else{
			modPos = ins1.get(0);
		}
		
		for(int i = 0; i < min; i++){
			res.replaceInstruction(ins1.get(i), p2.rawAccessToTheInternalInstructions().get(ins2.get(i)));
		}
		if(ins1.size() > ins2.size()){
			int count = 0;
			for(int i = min; i < ins1.size(); i++){
				res.removeInstruction(ins1.get(i) - count);
				count++;
			}
		}
		else if(ins1.size() < ins2.size()){
			for(int i = min; i < ins2.size(); i++){
				res.addInstruction(
						p2.rawAccessToTheInternalInstructions().get(ins2.get(i)));
			}
		}

		return modPos;
	}

}
