package misc;


/**A class for updating statistics in an online fashion. Keeps track of
 * both the mean and the variance of a series of numbers, and does so online,
 * so we can calculate the statistics of an arbitrary number of data points
 * without having to store them. THIS IS VITAL to the success of the BMA method.
 * 
 * This is used both in BMA for the purposes of calculating model probs, and as a method
 * for storing statistics about runs for empirical testing.
 * 
 * @author Carlton
 *
 */
public class Stats{

	public double var = 0;

	public double M2 = 0;//sum of the squares

	public double mean = 0;

	public int n = 0;
	
	public Stats(){}

	public Stats(double var, double M2, double mean, int n){
		this.var = var;
		this.mean = mean;
		this.M2 = M2;
		this.n = n;
	}

	public Stats(Stats stats){
		var = stats.var;
		M2= stats.M2;
		mean = stats.mean;
		n = stats.n;
	}
	
	public Stats clone(){
		return new Stats(this);
	}
	
	public void update(double val){

		n++;
		if(n == 1){
			mean = val;
			var = 0;
		}
		else{
			double nMean = mean + (val - mean)/n;
			M2 = M2 + (val - mean)*(val - nMean);
			var = M2/n;
			mean = nMean;
		}
	}
	
	public String toString(){
		return "(" + mean + ")";
	}

}