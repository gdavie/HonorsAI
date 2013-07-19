package Killmes;

import Fitness.SRP_FileReader;


/** basic LGP implementation of a multiclass classification system*/
public class SRP extends AI_SPAC{

	public SRP(String [] args){
		super(args);
	}
	
	public void doSetup(){
		super.doSetup(new SRP_FileReader(), null);
	}
	
//	@Override
//	public SRP_Population create() {
//		// Building the population object, randomising the fitness of it all:
//		return SRP_Population.create(new SRP_ModelFactory());
//	}
	
//	public static void main(String [] args) {
//		new SRP(args);
//	}

}
