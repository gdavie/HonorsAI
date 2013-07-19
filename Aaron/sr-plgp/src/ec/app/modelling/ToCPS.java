package ec.app.modelling;
/**
 * This class is to hold the values we are passing to libCPS.
 * @author az
 *
 */
public class ToCPS {
	double aperture;
	double[] frequencies;
	double[] layerModel;
	boolean test = true;

	public ToCPS(double aperture, double[] frequencies, double[] layerModel) {
		this.aperture = aperture;
		this.frequencies = frequencies;
		this.layerModel = layerModel;
	}

	public double getAperture() {
		if(test)System.out.println("getAperture: "+aperture);
		return aperture;
	}

	public double[] getFrequencies() {
		if(test){
			System.out.println("getFrequencies: ");
			for(int i = 0; i < frequencies.length; i++){
				System.out.print(""+frequencies[i]+", ");
			}
			System.out.println();
		}
			return frequencies;

	}

	public double[] getLayerModel() {
		if(test){
			System.out.println("getLayerModel: ");
			for(int i = 0; i < layerModel.length; i++){
				System.out.print(""+layerModel[i]+", ");
			}
			System.out.println();
		}
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
