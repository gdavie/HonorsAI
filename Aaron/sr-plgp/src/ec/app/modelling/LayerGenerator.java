package ec.app.modelling;

import java.util.Random;

import ec.app.gpspac.Layer;


public class LayerGenerator {

	//TODO this shouldn't be constants!
	//What are the units expected by libCPS?
	private static double rhRange;
	private static double rvsRange;

	public static Layer createLayer(double hRange, double vsRange){
		rhRange=hRange;
		rvsRange=vsRange;
		return new Layer(randomH(), randomVs());
	}
	public static Layer createTestLayer(double h, double vs){
		return new Layer(h, vs);
	}

	public static double randomH(){
		Random r = new Random();
		return 0 + (rhRange) * r.nextDouble();
	}

	public static double randomVs(){
		Random r = new Random();
		return 0 + (rvsRange) * r.nextDouble();
	}
}
