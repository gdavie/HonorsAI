package execute;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;

import core.APartsProgram;
import core.Config;
import core.FitnessEnvironment;
import core.IFitnessCase;
import core.IProgram;
import core.PMProgram;
import core.RegisterCollection;

public abstract class PME<E extends IProgram<E>> {

	protected Config config = Config.getInstance();

	public static boolean useWeights = false;

	public abstract void execute(FitnessEnvironment<?> fe, RegisterCollection rc, int num, APartsProgram<?,E> app);

	/** uses least squares regression to find the optimal weights assocated with a given matrix.
	 *  Assumes the matrix provided is in the correct form for performing least squares regression.
	 *  Assumes the target vector is of the correct form
	 * @param in
	 * @return
	 */
	public static Matrix calcWeights(Matrix in, Matrix t){

		Matrix[] svd = in.svd();

		// calculate the mp psuedo inverse of epsilon. epsilon is diagonal so we just need to transpose, then
		// take the reciprocal of each number on the main diagonal.
		Matrix Et = svd[1].transpose();
		for(int i = 0; i < in.getColumnCount() && i < in.getRowCount(); i++){
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

		Matrix weights = psuedoInverse.mtimes(t);

		return weights;
	}
	
	public static Matrix calcWeights(double[][] all, double[][] all_t){
		Matrix phi = MatrixFactory.importFromArray(all).transpose();
		Matrix t = MatrixFactory.importFromArray(all_t).transpose();
		Matrix weights = calcWeights(phi, t);
		return weights;
	}

	public <F extends IFitnessCase, APP extends APartsProgram>void updateFitness(FitnessEnvironment<F> tfe, FitnessEnvironment<F> vfe, APP ip){
		// Zero the fitness to a null state, mark the introns to optimise
		// execution time:

		ip.zeroFitness();
		ip.markIntrons();

		ip.updateType(tfe, ip.getTrainFinalRegisterValues(), ip.getTrainFitnessMeasure());
		//System.out.println(ip.fitness());
		ip.updateType(vfe, ip.getValidFinalRegisterValues(), ip.getValidFitnessMeasure());
	}

}