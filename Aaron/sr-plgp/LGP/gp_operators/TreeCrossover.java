package gp_operators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import core.Config;
import core.MultiClassProgram;

/** implements a form of crossover where only entire class graphs are exchanged
*/
public class TreeCrossover extends GP_Operator<MultiClassProgram>{

	public int execute(MultiClassProgram p1, MultiClassProgram p2, MultiClassProgram res){

		boolean debug = false;
		if(debug){
			System.out.println("first :");
			System.out.println(p1.toString(false, true));
			System.out.println("second: ");
			System.out.println(p2.toString(false, true));
		}
		//the class we are exchanging alleles from

		Set<Integer> classes = new HashSet<Integer>();

		for(int i = 0; i < Config.getInstance().numClasses; i++){
			if(Math.random() > 0.5){
				classes.add(i);
			}
		}

		ArrayList<Integer> ins1 = p1.alleleInstructions(classes);
		ArrayList<Integer> ins2 = p2.alleleInstructions(classes);

		if(debug){
			System.out.println("cls: " + classes);
			System.out.println(ins1);
			System.out.println(ins2);
		}

		int modPos;
		if(ins1.isEmpty()){
			modPos = p1.size()-1;
		}
		else{
			modPos = ins1.get(0);
		}
		
		int min = Math.min(ins1.size(), ins2.size());

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
