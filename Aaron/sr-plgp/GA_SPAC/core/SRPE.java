package core;

import libCPS.CPSwrapper;
import modelling.FromCPS;
//import ec.app.libCPS.CPSwrapper;
//import ec.app.modelling.*;
import modelling.ToCPS;


public class SRPE{

	private boolean traceCPSflow=false;
	private boolean SHOWMODEL=true;

	protected static CPSwrapper c;

	public static boolean useWeights = false;

	public double execute(SRP_FitnessEnvironment fe, Individual ind, CPSwrapper cps){

		/**
		 * We require an entirely new approach to execution here.
		 * Rather than executing the parts and summing registers, we
		 * will form the layers into an array that represents the model.
		 */
		c = cps;
		/*
		 * arrModel will be fed to libCPS as a single array
		 * where each element 0,2,4... is a thickness and
		 * each element 1,3,5... is a velocity.
		 * Half space will be made explicit inside libCPS
		 */
		double[] genes = ind.getGenes().clone();
		double aperature = 20; //hardcoded
		double frequencies[] = fe.getFreq();
		int j=0;

		j=0;
		if(SHOWMODEL){
			System.out.println("model -------");
			for(int i=0; i< genes.length/2; i++){
				System.out.println(genes[j++]+" "+genes[j++]);
			}
		}

//        double omdl[] = new double[4];

		//Create communication object to pass parameters to libCPS
		ToCPS toCPS = new ToCPS(aperature, frequencies, genes);



		//Create communication object using the return values from libCPS
				if(traceCPSflow)System.out.println("before call ");
		FromCPS retval = c.callCPS(toCPS);
				if(traceCPSflow)System.out.println("after call ");

		//Upon return, we will have three arrays that will be encapsulated in the FromCPS object.
        double  freq[] = retval.getFrequencies();
        double  cc[] = retval.getCoherency();
        double  ellip[] = retval.getEllipticity();

		//The resolution of error should be done immediately and we call the fitness function.
        double sum = fe.calculateFitnessRMSE(freq, cc, ellip);
        ind.setCoh(cc);
		ind.setFreq(freq);
        System.out.println("RMSE "+sum+"\n");
        return sum;
        //System.out.println("RMSE "+sum+"\n"+model.toString());
        //Assign fitness to model
		//model.setFitness(sum);
		//model.setFitnessStatus(true);
		//Assign the results to the model...
		//libCPS is expensive, and we may wish to re-use these for
		//A more heuristic approach to fitness

		//model.setEllip(ellip);

	}

//	public <F extends IFitnessCase, APP extends APartsProgram>void updateFitness(SRP_FitnessEnvironment tfe, SProgram sr_prog){
//		sr_prog.updateFitness(tfe);
//	}

}