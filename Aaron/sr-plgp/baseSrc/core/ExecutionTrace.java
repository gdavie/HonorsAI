package core;

/**this is actually a 2d array implemented as a single 1d array for the purposes of execution time
 * speedup
 * 
 * This array is of the form:
 * x instructions
 * y registers
 * @author downeycarl
 *
 * @param <T>
 */
public class ExecutionTrace {
	
	private static int length;
	private double[] trace;
	
	public ExecutionTrace(int x, int y){
		length = y;
		trace = new double[x*y];
	}
	
	public ExecutionTrace(ExecutionTrace et){
		trace = new double[et.trace.length];
		System.arraycopy(et.trace, 0, trace, 0, trace.length);
	}
	
	public double readRegister(int x, int y){
		return trace[x*length + y];
	}
	
	public void writeRegister(int x, int y, double value){
		trace[x*length + y] = value;
	}
	
	public void copy(ExecutionTrace et){
		System.arraycopy(et.trace, 0, trace, 0, trace.length);
	}
	
	public void copyFromRegisters(RegisterCollection rc, int pos){
		System.arraycopy(rc.getRegisters(), 0, trace, length*pos, rc.getRegisters().length);
	}
	
    public void copyToRegisters(RegisterCollection rc, int pos){
    	System.arraycopy(trace, pos*ExecutionTrace.length, rc.getRegisters(), 0, length);
    }
	
	public ExecutionTrace clone(){
		return new ExecutionTrace(this);
	}
	
	public RegisterCollection readRegisters(int x){
		RegisterCollection rc = new RegisterCollection(length);
		for(int i = 0; i < length; i++){
			rc.write(i, trace[length*x+i]);
		}
		return rc;
	}
}
