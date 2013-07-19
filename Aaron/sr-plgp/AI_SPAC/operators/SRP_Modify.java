package operators;

import java.util.Random;

import core.Config;
import core.SProgram;

/** This mutation operator chooses a random layer and performs a macro mutation on it*/
public class SRP_Modify extends SRP_Operator{
	
	
	Config c = Config.getInstance();
	
	protected double addSubRangeH = c.addSubRangeH;
	protected double divMultRangeH =c.divMultRangeH;
	
	protected double addSubRangeVS = c.addSubRangeVS;
	protected double divMultRangeVS =c.divMultRangeVS;
	
	protected static Random r ;
	/**
	 * Modifies a single parameter ie layer 1 V_s.
	 * 
	 * Currently, this will branch out to random modifications as a proof of concept,
	 * although we should create different class for each operator to give better 
	 * granularity. 
	 * 
	 * @param p1 parent 1
	 * @param p2 not used here, but part of the interface
	 * @param res : The resultant child
	 */
	@Override
	public void execute(SProgram p1, SProgram p2, SProgram res) {
		System.out.println("undefined call to execute in modify class");
	}
	
	

}
