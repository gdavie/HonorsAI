package execute;

import java.util.Arrays;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.enums.ValueType;

import core.APartsProgram;
import core.FitnessEnvironment;
import core.IFitnessCase;
import core.IProgram;
import core.MultiClassFitnessCase;
import core.PMProgram;
import core.RegisterCollection;

public class PME_hybrid<E extends IProgram<E>> extends PME<E>{

	private int numRegisters;

	private int numParts;

	private double[][] vectors;

	private Matrix ones;

	public double[][] all;

	public double[][] all_t;

	public PME_hybrid( int numRegisters, int numParts){

		this.numRegisters = numRegisters;
		this.numParts = numParts;

		vectors = new double[numParts][numRegisters];

		ones = MatrixFactory.ones(ValueType.DOUBLE, numParts, 1);

		all = new double[numParts][numRegisters*config.numTrain];//holds all the info
		all_t = new double[1][numRegisters*config.numTrain];
		Arrays.fill(all_t[0], -10);
	}

	@Override
	public void execute(FitnessEnvironment<?> fe, RegisterCollection rc, int num, APartsProgram<?,E> app) {

		E[] parts = app.parts();

		for(int i = 0; i < parts.length; i++){
			int s2 = num*numRegisters;
			if(useWeights  && fe.type == FitnessEnvironment.TRAIN){
				System.arraycopy(all[i], s2, vectors[i], 0, numRegisters);
			}
			else{
				fe.zeroRegisters();
				parts[i].execute(fe, null, num);

				//here we will copy to "all" if we are trying to learn the weights 
				if(!useWeights  && fe.type == FitnessEnvironment.TRAIN){//are we building up weights

					System.arraycopy(fe.getRegisters().getRegisters(), 0, all[i], s2, numRegisters);
				}

				System.arraycopy(fe.getRegisters().getRegisters(), 0, vectors[i], 0, numRegisters);
			}
		}

		//setting up 
		//TODO this just assumes we have a MultiClass fitness Case
		if(!useWeights && fe.type == FitnessEnvironment.TRAIN){
			MultiClassFitnessCase mcfc = (MultiClassFitnessCase) fe.currentCase();
			int s = num*numRegisters + mcfc.classNumber();
			all_t[0][s] = 100;
		}

		Matrix phi = MatrixFactory.importFromArray(vectors);
		phi = phi.transpose();

		Matrix result = null;
		if(useWeights){
			result = phi.mtimes(((PMProgram)app).weights);//TODO BAD CAST
		}
		else
			result = phi.mtimes(ones);

		// Now store finalRegisterValues
		System.arraycopy(result.transpose().toDoubleArray()[0], 0, rc.getRegisters(), 0, numRegisters);

	}

	@Override
	public <F extends IFitnessCase, APP extends APartsProgram> void updateFitness(FitnessEnvironment<F> tfe, FitnessEnvironment<F> vfe, APP ipp){
		PMProgram ip = (PMProgram)ipp;//AzM hack in 
		ip.zeroFitness();
		ip.markIntrons();

		//update fitness once using orignal values
		PME.useWeights = false;
		Arrays.fill(all_t[0], -10);
		ip.updateType(tfe, ip.getTrainFinalRegisterValues(), ip.getTrainFitnessMeasure());

		//do it again using weights
		ip.setFitnessStatus(false);
		PME.useWeights = true;
		
		double fitness = ip.fitness();
		
		ip.zeroFitness();
		ip.weights = calcWeights(all, all_t);
		ip.updateType(tfe, ip.getTrainFinalRegisterValues(),  ip.getTrainFitnessMeasure());

		double hybridFitness = fitness*0.5 + ip.fitness()*0.5;//form a hybrid fitness
		ip.setFitness(hybridFitness);
		
		//note we will still be using weights here
		ip.updateType(vfe, ip.getValidFinalRegisterValues(), ip.getValidFitnessMeasure());
	}
}
