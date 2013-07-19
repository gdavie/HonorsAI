/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.gaspac;
import libCPS.CPSwrapper;
import ec.*;
import ec.app.gpspac.Layer;
import ec.app.gpspac.SRPE;
import ec.app.gpspac.SRP_FitnessEnvironment;
//import ec.app.libCPS.CPSwrapper;
import ec.simple.*;
import ec.vector.*;

public class AddSubtract extends Problem implements SimpleProblemForm
    {
	 protected static SRP_FitnessEnvironment coherencies;
		private CPSwrapper cps = new CPSwrapper();
		private String hardCodedFile = "/u/students/daviegeor/4year/489/datasets/site01_20m.dat";//hardcoded value
		private int aperture = 20; //hardcoded value - distance between points - equaltril triangle
		private boolean setup = true;
		private SRPE srpe;

		public void setup(){
			coherencies = new SRP_FitnessEnvironment();
			coherencies.addCasesFromFile(hardCodedFile); //hardcoded value
			CPSwrapper.setMAXSIZE(coherencies.getLstFreq().size());
			srpe = new SRPE();
			System.out.println(CPSwrapper.getMAXSIZE());
			System.out.println("Loaded "+ (coherencies.getLstCoh().size()+1) + " Frequency points. Beginning evolution.");
		}


    public void evaluate(final EvolutionState state,
        final Individual ind,
        final int subpopulation,
        final int threadnum)
        {
    	if(setup){
    		setup();
    		setup = false;
    	}
        if (ind.evaluated) return;

        if (!(ind instanceof DoubleVectorIndividual))
            state.output.fatal("Whoa!  It's not a DoubleVectorIndividual!!!",null);

        DoubleVectorIndividual ind2 = (DoubleVectorIndividual)ind;

        float rawfitness = 0;
        Layer[] l = {new Layer(ind2.genome[0],ind2.genome[1]), new Layer(ind2.genome[2],ind2.genome[3])};
//        for(int x=0; x<ind2.genome.length; x++){
//
//        	//insert CPSwrapper stuff here
//        	rawfitness = srpe.execute(coherencies, l, cps);
//            //if (x % 2 == 0) rawfitness += ind2.genome[x];
//           //else rawfitness -= ind2.genome[x];
//        }

        rawfitness = (float)srpe.execute(coherencies, l, cps);

        // We finish by taking the ABS of rawfitness.  By the way,
        // in SimpleFitness, fitness values must be set up so that 0 is <= the worst
        // fitness and +infinity is >= the ideal possible fitness.  Our raw fitness
        // value here satisfies this.
        if (rawfitness < 0) rawfitness = -rawfitness;
        if (!(ind2.fitness instanceof SimpleFitness))
            state.output.fatal("Whoa!  It's not a SimpleFitness!!!",null);
        ((SimpleFitness)ind2.fitness).setFitness(state, rawfitness, false);
        ind2.evaluated = true;
        }
    }
