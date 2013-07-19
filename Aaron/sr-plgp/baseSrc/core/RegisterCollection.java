package core;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;


/**This class represents some collection of registers. If they are read-only just make
 the collection const.*/
public class RegisterCollection {
	
	private double[] registers;
	
	public RegisterCollection(int size){
		registers = new double[size];
	}
	
	public RegisterCollection(RegisterCollection rc){
		registers = new double[rc.registers.length];
		System.arraycopy(rc.registers, 0, registers, 0, registers.length);
	}
	
	/** Returns the size (number of registers) of this RegisterCollection*/
    public int size(){
    	return registers.length; 
    }

    /** Returns the value of the i'th register. Does not bounds check.*/
    public double read(int i){
    	return registers[i];
    }

    /** Writes the value T to the i'th register. Does not bounds check.*/
    public void write(int i, double val) {
    	registers[i] = val;
    }

    /** Zeroes all of the registers*/
    public void zeroRegisters(){
    	Arrays.fill(registers, 0);
    }
    

    // Helper Methods:
    /** Returns the register index of the register with the highest value (used in, e.g. winner
     takes all). If two registers are equal it returns the one with the lower index.*/
    /** At the moment i see no way to do these here, must have concrete types.*/
    public int largestRegisterIndex(){
    	double max = registers[0];
    	int index = 0;
    	for(int i = 1; i < size(); i++){
    		double temp = registers[i];
    		if (temp > max){
    			max = temp;
    			index = i;
    		}
    	}
    	return index;
    }

    public String toString(){
    	String buffer = "";
    	  buffer += registers[0];
    	  for(int i = 1; i < size(); ++i) {
    	    buffer += " " + registers[i];
    	  }
    	  return buffer;
    }
    public String toStringShort(){
    	NumberFormat form = new DecimalFormat("#0.00");
    	String buffer = "";
    	  buffer += form.format(registers[0]);
    	  for(int i = 1; i < size(); ++i) {
    	    buffer += " " + form.format(registers[i]);
    	  }
    	  return buffer;
    }
    
    public RegisterCollection clone(){
    	return new RegisterCollection(this);
    }
    
    public void copyRegisters(RegisterCollection rc){
    	System.arraycopy(rc.registers, 0, registers, 0, registers.length);
    }
    
    public double[] getRegisters(){
    	return registers;
    }
    
    public boolean equals(Object o){
    	RegisterCollection rc = (RegisterCollection)o;
    	for(int i = 0; i < rc.size(); i++){
    		if(registers[i] != rc.registers[i]){
    			return false;
    		}
    	}
    	return true;
    }
    
    public void add(RegisterCollection rc){
    	add(rc.registers);
    }
    
    public void add(double[] rc){
    	for(int i = 0; i < registers.length; i++){
    		registers[i] = registers[i] + rc[i];
    	}
    }
    
    public void sub(RegisterCollection rc){
    	sub(rc.registers);
    }
    
    public void sub(double[] rc){
    	for(int i = 0; i < registers.length; i++){
    		registers[i] = registers[i] - rc[i];
    	}
    }


}
