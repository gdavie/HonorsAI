package Fitness;

import libCPS.CPSwrapper;
import modelling.FromCPS;
import modelling.LayerGenerator;
import modelling.ToCPS;
import core.APartsProgram;
import core.Config;
import core.IFitnessCase;
import core.Layer;
import core.SProgram;


public class SRPE{

	private boolean traceCPSflow=false;
	private boolean SHOWMODEL=true;
	protected Config config = Config.getInstance();

	protected static CPSwrapper c;

	public static boolean useWeights = false;

	public void execute(SRP_FitnessEnvironment fe, SProgram model){

		/**
		 * We require an entirely new approach to execution here.
		 * Rather than executing the parts and summing registers, we
		 * will form the layers into an array that represents the model.
		 */
		Layer[] layers = model.layers();
		/*
		 * arrModel will be fed to libCPS as a single array
		 * where each element 0,2,4... is a thickness and
		 * each element 1,3,5... is a velocity.
		 * Half space will be made explicit inside libCPS
		 */
		double aperature = config.aperture;
		double frequencies[] = fe.getFreq();
		double arrModel[] = new double[layers.length*2];
		int j=0;
		//Fake model
		/*
		 * 1	(0.0086, 0.1235) (0.1365 0.2787)	(20m)
2	(0.009,  0.1335)  (0.1630,0.2731)	(20m)
3	(0.0161,0.1646)				(15m)
4	(0.0174, 0.1469)			(20m)
5	(0.0193, 0.2687)				(20m)
1 runScript.sh.o286523
best :1.6135777991498714
H : (0.8618400029877489,0.2753)
H : (0.01569026725838626 ,0.11103551386090817)


2 runScript.sh.o286529
best :1.4099105533264225
H : (0.001248857222303159,0.27977412557545006)
H : (0.0017968331118320868,0.015027843389916843)

3 runScript.sh.o286538

best :1.5212131548291603
H : (0.009803914648014014,0.15754765251853142)
H : (0.04189606255876963 ,0.24538998432918802)


4 runScript.sh.o286541

best :1.6785875544154845
(0.7364265010029102,0.2844153639952907)
(0.0020094636623706487,0.013015303612176544)

4b runScript.sh.o286540
1.7480773061696502

(0.008590474217257074,0.12651486962248848)
(0.04102933138656712 ,0.2458855787445225)


5 runScript.sh.o286546

best :1.4945303775089303
H : (0.014319719591142987,0.2640468363985122)
H : (1.3095676644686418,0.4714080304195551)
0.06273941806481655 V_s : 0.23738401492907818

*/
	//	model.setSite(2);
//		layers[0] = LayerGenerator.createTestLayer(0.06273941806481655,0.23738401492907818)	;
//		layers[1] = LayerGenerator.createTestLayer(0.01569026725838626 ,0.11103551386090817);

		for(int i = 0; i < layers.length; i++){
			arrModel[j++]=layers[i].getThickness();//0,2,4...
			arrModel[j++]=layers[i].getVelocity();//1,3,5,...
		}
		j=0;
		if(SHOWMODEL){
			System.out.println("model -------");
			for(int i=0; i< arrModel.length/2; i++){
				System.out.println(arrModel[j++]+" "+arrModel[j++]);
			}
		}

//        double omdl[] = new double[4];

		//Create communication object to pass parameters to libCPS
		ToCPS toCPS = new ToCPS(aperature, frequencies, arrModel);

		CPSwrapper c = config.cps;

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
        System.out.println("RMSE "+sum+"\n"+model.toString());
        //Assign fitness to model
		model.setFitness(sum);
		model.setFitnessStatus(true);
		//Assign the results to the model...
		//libCPS is expensive, and we may wish to re-use these for
		//A more heuristic approach to fitness
		model.setCoh(cc);
		model.setEllip(ellip);
		model.setFreq(freq);
	}

	public <F extends IFitnessCase, APP extends APartsProgram>void updateFitness(SRP_FitnessEnvironment tfe, SProgram sr_prog){
		sr_prog.updateFitness(tfe);
	}

}