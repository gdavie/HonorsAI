package info;

import mapping.SpanningTreeMapping;
import core.APartsProgram;
import core.Config;
import core.MultiClassProgram;
import core.PMPopulation;

public class Info {

	private static Config c = Config.getInstance();

	public static double avgIntraPopDist(PMPopulation pmp){

		double dist = 0;
		int count = 0;

		for(int i = 0; i < c.numPieces; i++){
			for(APartsProgram<?,MultiClassProgram> app1: pmp.getPrograms()){
				for(APartsProgram<?,MultiClassProgram> app2: pmp.getPrograms()){
					dist += SpanningTreeMapping.hammingDistance(app1.parts()[i], app2.parts()[i]);
					count++;
				}
			}
		}
		return dist/count;
	}

	public static double avgInterPopDist(PMPopulation pmp){

		double dist = 0;
		int count = 0;

		for(int i = 0; i < c.numPieces; i++){
			for(int j = 0; j < c.numPieces; j++){
				if(i!=j){
					for(APartsProgram<?,MultiClassProgram> app1: pmp.getPrograms()){
						for(APartsProgram<?,MultiClassProgram> app2: pmp.getPrograms()){
							dist += SpanningTreeMapping.hammingDistance(app1.parts()[i], app2.parts()[j]);
							count++;
						}
					}
				}
			}
		}
		return dist/count;
	}

	public static double avgTotDist(PMPopulation pmp){

		double dist = 0;
		int count = 0;
		
		for(int i = 0; i < c.numPieces; i++){
			for(int j = 0; j < c.numPieces; j++){
				for(APartsProgram<?,MultiClassProgram> app1: pmp.getPrograms()){
					for(APartsProgram<?,MultiClassProgram> app2: pmp.getPrograms()){
						dist += SpanningTreeMapping.hammingDistance(app1.parts()[i], app2.parts()[j]);
						count++;
					}
				}
			}
		}
		return dist/count;
	}


}
