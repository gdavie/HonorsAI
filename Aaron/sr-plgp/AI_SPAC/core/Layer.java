package core;

public class Layer {
	private double thickness;
	private double velocity;
	
	public Layer(double thickness, double velocity){
		this.thickness=thickness;
		this.velocity=velocity;
	}

	public double getThickness() {
		return thickness;
	}

	public void setThickness(double thickness) {
		//SANITY CHECK :: no non-positive thickness
		if(thickness>0){
			this.thickness = thickness;
		}

	}

	public double getVelocity() {
		return velocity;
	}

	public void setVelocity(double velocity) {
		//SANITY CHECK :: no non-positive velocities
		if(velocity>0){
			this.velocity = velocity;
		}
	}
	
	public Layer clone() {
		return new Layer(this.thickness, this.velocity);
	}

	public void reinitialize(Layer layer) {
		this.thickness=layer.thickness;
		this.velocity=layer.velocity;
		
	}
	public String printLayer(){
		return "H : "+ thickness + " V_s : "+velocity;
	}
}
