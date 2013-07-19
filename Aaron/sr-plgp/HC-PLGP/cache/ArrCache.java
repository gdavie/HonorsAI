package cache;

import java.util.ArrayList;
import core.RegisterCollection;

/**
 * This class is to contain the set of register collections so we can refer to them later.
 * 
 * Each instance from the training set will contain a set of registers for each program.
 *
 * Diagnostics and logging have been stripped out - the rewrite broke them all anyways.
 * The dimensionality of the arrays has been hidden with arraylist-like calls
 * @author scobleaaro
 * 070712 Stripped out version, modified to run on arrays rather than nested arraylists.
 */
public class ArrCache extends I_Cache{
//	//Two caches, one each for training and validation
	public RegisterCollection [][][] trainCache;
	public RegisterCollection [][][] validCache;
//	//Number of training and validation instances.
	private int numTrain, numValid;
//	//classNums is collected once per experiment, and hold class labels over all instances.
//	//These have been left as arraylists for now, performance boost by turning into arrays 
//	//may not be worth time to refactor.
//	public ArrayList<Integer> trainClassNums;
//	public ArrayList<Integer> validClassNums;	
//	// this counts mis-classifications, so numbers stored
//	// allow us to cache 32767 incorrect classifications. plenty.
//	public short [][][] fitness;//fitness [b][s][f]
//	//This one accumulates over all blueprints. Needs a bit more space.
//	public int [][] facFit;//fitness [s][f]
//	// This will only store a value of 0 or 1.
//	// Could use a boolean but this will do; and boolean
//	// size is undefined.
//	public byte [][][] done;//done[b][s][f]

	/**
	 * Constructor simply sets the cache up, we need the following dimensions :
	 * @param numTrain
	 * @param numValid
	 */
	public ArrCache(int numTrain, int numValid){
		this.numTrain=numTrain;
		this.numValid=numValid;
		trainClassNums = new ArrayList<Integer>();
		validClassNums = new ArrayList<Integer>();				
	}
 
	/**
	 * Resets the cache between generations. The following parameters are required
	 * to set up the dimensions.
	 * @param blueprints 
	 * @param instances
	 * @param subpops
	 * @param factors
	 */
	public void initialiseCaches(int blueprints, int subpops, int factors){
		trainCache= new RegisterCollection[subpops][factors][numTrain];
		validCache= new RegisterCollection[subpops][factors][numValid];		
		fitness= new short [blueprints][subpops][factors];
		facFit= new int [subpops][factors];
		done= new byte [blueprints][subpops][factors];
	}

	/**
	 * Add a register collection that relates to the requested cache location,
	 * to the set of training outputs.
	 * @param rc Register collection
	 * @param s subpopulation index
	 * @param f factor index
	 * @param instNum instance index
	 */
	public void addInstanceRC_T(RegisterCollection rc, int s, int f, int instNum){
		//TODO We could normalise the cache here, when the rc's are being added?
		trainCache[s][f][instNum]=rc.clone();
	}
	
	/**
	 * Add a register collection that relates to the requested cache location,
	 * to the set of Validation outputs.
	 * @param rc Register collection
	 * @param s subpopulation index
	 * @param f factor index
	 * @param instNum instance index
	 * cached.addInstanceRC_T(rc, s, f, caseNum);
	 */
	public void addInstanceRC_V(RegisterCollection rc, int s, int f, int instNum){
		//TODO We could normalise the cache here, when the rc's are being added?
		validCache[s][f][instNum]=rc.clone();
	}
	///////////////////Accessors and mutators for training RCs/////////////////////////////////////////////////
	public RegisterCollection rc_T(int s, int f, int instNum){
			return trainCache[s][f][instNum];
	}
	public ArrayList<Integer> getClassNums_T() {
			return trainClassNums;			
	}	
	public void addClassNum_T(int classNum) {
			trainClassNums.add(classNum);			
	}
///////////////////Accessors and mutators for validation RCs/////////////////////////////////////////////////
	public RegisterCollection rc_v(int s, int f, int instNum){
		return validCache[s][f][instNum];
	}
	public ArrayList<Integer> getClassNums_V() {
			return validClassNums;			
	}
	public void addClassNum_V(int classNum) {
			validClassNums.add(classNum);
	}
	
	public void killTrainCache(){
		trainCache=null;
	}
	public void killValidCache(){
		validCache=null;
	}
}
