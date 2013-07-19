package core;


/**This approach is the same as the basic Cooperative Coevolution approach, EXCEPT
 * that each subprogram is drawn from a different population. I.e. if each blueprint 
 * consists of 5 subprograms, then there will be 5 subpopulations, with each blueprint
 * having a single program from each subpopulation.
 * 
 * @author downeycarl
 *
 * @param 
 */
public abstract class Many extends Main{
	public Many(String[] args){
		super(args);
	}
	
	public void doSetup(){
		super.doSetup(new MultiClassFileReader(), null);
	}
	
}
