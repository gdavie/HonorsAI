package execute;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.calculation.Calculation.Ret;
import org.ujmp.core.enums.ValueType;

import core.APartsProgram;
import core.FitnessEnvironment;
import core.IProgram;
import core.MultiClassFitnessCase;
import core.PMProgram;
import core.RegisterCollection;

public class PME_weighted<E extends IProgram<E>> extends PME<E>{

	private int numRegisters;

	private int numParts;

	private double[][] vectors;

	public PME_weighted(E[] parts, int numRegisters, int numParts){

		this.numRegisters = numRegisters;
		this.numParts = numParts;

		vectors = new double[numParts][numRegisters];
	}

	@Override
	public void execute(FitnessEnvironment<?> fe, RegisterCollection rc, int num, APartsProgram<?,E> app){

		E[] parts = app.parts();
		
		for(int i = 0; i < parts.length; i++){
			fe.zeroRegisters();
			parts[i].execute(fe, null, num); 

			System.arraycopy(fe.getRegisters().getRegisters(), 0, vectors[i], 0, numRegisters);
		}

		Matrix phi = MatrixFactory.importFromArray(vectors);
		phi = phi.transpose();

		Matrix result = null;
		
		if(!useWeights  && fe.type == FitnessEnvironment.TRAIN){//are we building up weights

			Matrix[] svd = phi.svd();

			// calculate the mp psuedo inverse of epsilon. epsilon is diagonal so we just need to transpose, then
			// take the reciprocal of each number on the main diagonal.
			Matrix Et = svd[1].transpose();
			for(int i = 0; i < numRegisters && i < numParts; i++){
				double val = Et.getAsDouble(i,i);
				if(val!=0){
					val = 1/val;
					Et.setAsDouble(val, i,i);
				}
			}

			//now if SVD = UEV* compute VE+U*, which for our case since real numbers is VE+U^T
			Matrix Ut = svd[0].transpose();
			Matrix V = svd[2];

			Matrix psuedoInverse = V.mtimes(Et).mtimes(Ut);

			//TODO this just assumes we have a MultiClass fitness Case
			MultiClassFitnessCase mcfc = (MultiClassFitnessCase) fe.currentCase();
			Matrix t = MatrixFactory.fill(-10, numRegisters,1);
			t.setAsDouble(100, mcfc.classNumber(), 0);

			Matrix weights = psuedoInverse.mtimes(t);
			
			((PMProgram)app).weights.plus(Ret.ORIG, false, weights);
			//now use the weights to add the original vectors together. at the moment just using ones
			Matrix ones = MatrixFactory.ones(ValueType.DOUBLE,numParts,1);
			result = phi.mtimes(ones);
		}
		else{//otherwise use them
			result = phi.mtimes(((PMProgram)app).weights);
		}

		// Now store finalRegisterValues
		System.arraycopy(result.transpose().toDoubleArray()[0], 0, rc.getRegisters(), 0, numRegisters);

	}
}
