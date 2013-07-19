package core;

import java.util.ArrayList;
import java.util.List;

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
public class Cache2 {
	
	private List<RegisterCollection> train = new ArrayList<RegisterCollection>();
	private List<RegisterCollection> valid = new ArrayList<RegisterCollection>();
	
	private static final int numTrain = Config.getInstance().numTrain;
	private static final int numValid = Config.getInstance().numValid;
	private static final int numRegisters = Config.getInstance().numRegisters;
	
	
	
	public Cache2(){
		
		for(int i = 0; i < numTrain; i++){
			train.add(new RegisterCollection(numRegisters));
		}
		for(int i = 0; i < numTrain; i++){
			valid.add(new RegisterCollection(numRegisters));
		}
		
	}
	
	public Cache2(Cache2 c){
		for(int i = 0; i < numTrain; i++){
			train.add(c.train.get(i).clone());
		}
		for(int i = 0; i < numTrain; i++){
			valid.add(c.valid.get(i).clone());
		}
	}
	
	public Cache2 clone(){
		return new Cache2(this);
	}
	
	public void reinitialise(Cache2 c){
		for(int i = 0; i < numTrain; i++){
			train.get(i).copyRegisters(c.train.get(i));
		}
		for(int i = 0; i < numTrain; i++){
			valid.get(i).copyRegisters(c.valid.get(i));
		}
	}
	
	public void update(RegisterCollection rc, int num, boolean isTrain){
		if(isTrain){
			train.get(num).copyRegisters(rc);
		}else{
			valid.get(num).copyRegisters(rc);
		}
	}
	
	public RegisterCollection get(int num, boolean isTrain){
		RegisterCollection rc = new RegisterCollection(numRegisters);
		if(isTrain){
			rc.copyRegisters(train.get(num));
		}else{
			rc.copyRegisters(valid.get(num));
		}
		return rc;
	}
	
}
