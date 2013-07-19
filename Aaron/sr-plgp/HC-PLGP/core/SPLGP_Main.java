package core;

import core.SPLGP_Pfactory;
import core.Main;
import core.MultiClassFileReader;

/**
 * Entry point for SPLGP algorithm
 * Combines package-specific Main and many_sub_piop.Many classes 
 * to extract a layer of indirection.
 * 
 * Uses a HCS_population and Pfactory
 * @author AJ Scoble
 *
 */
public class SPLGP_Main extends CV_Main {

	private boolean DEBUG;
	public SPLGP_Main(String[] args) {
		super(args);
		this.DEBUG=super.isDEBUG();
	}

	//@Override
	public HCS_Population create() {
		// Building the population object, randomising the fitness of it all:
		return HCS_Population.create(new SPLGP_Pfactory(), c.numFeatures, c.numRegisters, DEBUG);
	}
	
	public void doSetup(){
		super.doSetup(new MultiClassFileReader(), null);
	}
	
	public static void main(String [] args) {
		new SPLGP_Main(args);
	}

}
