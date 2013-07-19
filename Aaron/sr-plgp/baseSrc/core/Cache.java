package core;

import java.util.Formatter;

/** Used to store the final register values for an LGP program for EVERY training example.
 * This is important to do if you want any form of cacheing whatsover. 
 * 
 * DEPRECATED TO SOME DEGREE. this was intended to be done for every subprogram of a program,
 * but this proved too costly in terms of memory and probably wouldn't be memory efficient anyway.
 * 
 * REPLACED with caching OLD PROGRAMS.
 * @author downeycarl
 *
 */
public class Cache {
	
	private double[] values;
	
	private static final int numTrain = Config.getInstance().numTrain;
	private static final int numValid = Config.getInstance().numValid;
	private static final int numRegisters = Config.getInstance().numRegisters;
	
	private static int validOffset;
	
	public Cache(){
		
		int size = numTrain*numRegisters + numValid*numRegisters;
		validOffset = numTrain*numRegisters;
		
		values = new double[size];
	}
	
	public Cache(Cache c){
		values = new double[c.values.length];
		System.arraycopy(c.values, 0, values, 0, values.length);
	}
	
	public Cache clone(){
		return new Cache(this);
	}
	
	public void reinitialise(Cache c){
		System.arraycopy(c.values, 0, values, 0, values.length);
	}
	
	public void update(RegisterCollection rc, int num, boolean train){
		int start = offset(num,train);
		System.arraycopy(rc.getRegisters(), 0, values, start, numRegisters);
	}
	
	public RegisterCollection get(int num, boolean train){
		RegisterCollection rc = new RegisterCollection(numRegisters);
		System.arraycopy(values,  offset(num,train), rc.getRegisters(),  0, numRegisters);
		return rc;
	}
	
	private static int offset(int num, boolean train){
		return (train?0:validOffset) + num*numRegisters;
	}
	
	public boolean equals(Object o){
		Cache c = (Cache)o;
		for(int i = 0; i < values.length; i++){
			if(c.values[i] != values[i]){
				return false;
			}
		}
		return true;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		Formatter f = new Formatter(sb);
		
		f.format("%s", "[");
		for(int i = 0; i < values.length; i++){
			f.format("%.2f ", values[i]);
			if((i+1)%numRegisters == 0){
				f.format("%s", "],[");
			}
		}
		f.format("%s", "]");
		return sb.toString();
	}

}
