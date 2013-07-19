package ec.app.modelling;
/**
 * This class is to hold the three arrays that are passed back from 
 * libCPS.
 * @author az
 *
 */
public class FromCPS {
	double[] frequencies;
	double[] coherency;
	double[] ellipticity;
	int count;
	
	
	public FromCPS(double[] frequencies, double[] coherency,
			double[] ellipticity, int count) {
		this.frequencies = frequencies;
		this.coherency = coherency;
		this.ellipticity = ellipticity;
		this.count=count;
	}
	public double[] getFrequencies() {
		return frequencies;
	}
	public double[] getCoherency() {
		return coherency;
	}
	public double[] getEllipticity() {
		return ellipticity;
	}
	public int getCount() {
		return count;
	}
}
