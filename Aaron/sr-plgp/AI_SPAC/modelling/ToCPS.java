package modelling;
/**
 * This class is to hold the values we are passing to libCPS.
 * @author az
 *
 */
public class ToCPS {
	double aperture;
	double[] frequencies;
	double[] layerModel;
	
	public ToCPS(double aperture, double[] frequencies, double[] layerModel) {
		this.aperture = aperture;
		this.frequencies = frequencies;
		this.layerModel = layerModel;
	}

	public double getAperture() {
		return aperture;
	}

	public double[] getFrequencies() {
		return frequencies;
	}

	public double[] getLayerModel() {
		return layerModel;
	}

	public void setAperture(int aperture) {
		this.aperture = aperture;
	}

	public void setFrequencies(double[] frequencies) {
		this.frequencies = frequencies;
	}

	public void setLayerModel(double[] layerModel) {
		this.layerModel = layerModel;
	}
}
