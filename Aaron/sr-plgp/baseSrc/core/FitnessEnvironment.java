package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import misc.FileReader;

/**This class stores the memory environment, fitness cases and have methods for evaluating 
 programs. T is the type of the features and registers for this problem. This class manages 
 the memory of the fitness cases added to it.*/
public class FitnessEnvironment<F extends IFitnessCase>{

	public static final int TRAIN = 0;

	public static final int VALID = 1;

	public static final int TEST = 2;
	private RegisterCollection registers;
	private ArrayList<F> cases = new ArrayList<F>();
	private int currentCase;
	public final int type;
	
	public FitnessEnvironment(int type, int numRegisters){
		registers = new RegisterCollection(numRegisters);
		currentCase = 0;
		this.type = type;
		
	}
	
	public RegisterCollection cloneRegisters(){
		return registers.clone();
	}

    /** Returns the value of the i'th feature*/
    public double readFeature(int i) {
    	return cases.get(currentCase).F(i);
    }

    /** Returns the value of register i*/
    public double readRegister(int i){
    	return registers.read(i);
    }
    
    /** Write the value val to the register i*/
    public void writeRegister(int i, double val) { 
    	registers.write(i, val); 
    }

    /** Sets all read/write registers to have a value of 0*/
    public void zeroRegisters() { 
    	registers.zeroRegisters();
    }

    /** Returns true if the feature vector for the first case could be loaded, false otherwise*/
    public boolean loadFirstCase() { 
    	currentCase = 0; 
    	return numberOfCases() > 0; 
    }

    /** Returns true if there was a "next case" to load*/
    public boolean loadNextCase() { 
    	return ++currentCase < numberOfCases(); 
    }

    /** Returns a pointer to the current case*/
    public F currentCase() {
    	return cases.get(currentCase); 
    }

    /** Returns the number of cases in this fitness environment:*/
    public int numberOfCases() { 
    	return cases.size();
    }

    /** Adds the specified case to the FitnessEnvironment - these get passed to fitness measures
     when a program's error is being updated, so that is where they should probably be checked
     as being the correct type. An instance of this class will manage the memory of the 
     pointers to the fitness cases.*/
    public void addCase(F fc) { 
    	cases.add(fc); 
    }


    /** Assumes the file is 4 numbers on one line followed by series of fitness cases of whatever type the function pointer
     expects, one per line.*/
    public void addCasesFromFile(String filePath, FileReader<? extends F> fr){
    	
    	Scanner scan;
    	
		try {
			scan = new Scanner(new File(filePath));
			scan.nextLine();
	    	while(scan.hasNext()){
	    		addCase(fr.readLine(scan.nextLine()));
	    	}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
    }

    /** Returns a string representation of this FitnessEnvironment, i.e. the value of the
     features for the current fitness case and the current values of the registers. Does
     not place a trailing new line after the string representation, even though its multiline.*/
    public String toString(){
    	 String buffer = "";

    	  buffer += "R: ";
    	  for(int i = 0; i < Config.getInstance().numRegisters; ++i) {
    	    buffer += readRegister(i) + " ";
    	  }
    	  buffer += "\n";
    	  buffer += "F: ";
    	  for(int i = 0; i < Config.getInstance().numFeatures; ++i) {
    	    buffer += readFeature(i) + " ";
    	  }
    	  buffer += "\n";
    	  buffer += "Number of cases loaded: " + cases.size();

    	  return buffer;
    }
    
    public int size(){
    	return cases.size();
    }
    
    public RegisterCollection getRegisters(){
    	return registers;
    }
    
    /** WARNING
     * this is a horrible hack designed to work with hyper GP by allowing me to execute one off program
     * evaluations for label functions when determining the substrate. DO NOT USE THIS FOR ANY OTHER PURPOSE.
     * @param ifc
     */
    public void hackCurrentCase(F ifc){
    	cases.add(currentCase, ifc);
    }
    
    public List<F> getCases(){
    	return cases;
    }
    
 
}
