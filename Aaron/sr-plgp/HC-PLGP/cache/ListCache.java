package cache;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import core.IProgram;
import core.RegisterCollection;

/**
 * This class is to contain the set of register collections so we can refer to them later.
 * The vertical index will relate to instances in the training set (maybe we should use the 
 * validation set for this work? can easily change)
 * Each instance from the training set will contain a set of registers for each program.
 * We will also want to build into this class the diagnostic tools we will need, for example 
 * dumping relevat information to file.
 * @author scobleaaro
 * @param <E>
 * TODO will need to tidy this cache up and get it running more efficiently. access methods are flaky.
 * Maybe maps would be better, iterating through instances is probably a poor approach. 
 */
public class ListCache extends I_Cache{
	//Full cache, index is the subpopulation
	public GenCache trainCache;
	public GenCache validCache;
	//List of lists; index is the factor
	public SubPopCache subPopCache;
	//ArraytList of Register Collections, where index is the instance
	public FactorCache factorCache;
	//classNums is collected once per experiment, and hold class labels over all instances.
//	public ArrayList<Integer> trainClassNums;
//	public ArrayList<Integer> validClassNums;
	//progfitness independent of blueprints. minimise
	public int [][] facFit;//fitness [s][f]
	//blueprint,subpop, factor fitness. minimise
	public int [][][] fitness;
	//Hit for proportional search.
	public byte [][][] done;
	
	
	//DEBUGSWITCH
	public boolean log = false;
	private String path;

	private static FileWriter reg_outs;
	private static BufferedWriter reg_bufr;
//	private NumberFormat form = new DecimalFormat("#0.0000");

//	private boolean trainLabelsCaptured = false, validLabelsCaptured = false;
	

	public ListCache(String pth,boolean log){
		this.path = pth;
		this.log=log;
		trainClassNums = new ArrayList<Integer>();
		validClassNums = new ArrayList<Integer>();
		trainCache= new GenCache();
		validCache= new GenCache();
		//System.out.println(path);
		try {
			reg_outs = new FileWriter(path+"_regLog",false);
			reg_bufr		 = new BufferedWriter(reg_outs);
		} catch (IOException e) {
			e.printStackTrace();
		}				

	}

	public void initialiseCaches(int blueprints, int subpops, int factors){
		trainCache.clear();
		validCache.clear();
		fitness= new int [blueprints][subpops][factors];
		done= new byte [blueprints][subpops][factors];
		facFit= new int [subpops][factors];
		for(int sp=0;sp<subpops;sp++){
			trainCache.add(new SubPopCache());
			validCache.add(new SubPopCache());
			for(int fac=0;fac<factors;fac++){
				trainCache.subpop(sp).add(new FactorCache());
				validCache.subpop(sp).add(new FactorCache());
			}
		}
	}
	/**
	 * @param rc - Results for factor that has just been executed
	 * @param s - subpopulation idex for that factor
	 * @param f - factor index within that subpopulation
	 * 
	 */
	public void addInstanceRC_T(RegisterCollection rc, int s, int f, int dummy){
		//TODO We could normalise the cache here, when the rc's are being added?
		//Need to clone here else we are just duplicatiing a pointer !!!!
		trainCache.subpop(s).factor(f).add(rc.clone());
	}
	
	public void addInstanceRC_V(RegisterCollection rc, int s, int f, int dummy){
		//TODO We could normalise the cache here, when the rc's are being added?
		//Need to clone here else we are just duplicatiing a pointer !!!!
		validCache.subpop(s).factor(f).add(rc.clone());
	}
	///////////////////Accessors and mutators for training RCs/////////////////////////////////////////////////
	// 070712 Removed boolean switches, no point making a test every time access is made.
	public RegisterCollection rc_T(int s, int f, int index){
			return trainCache.subpop(s).factor(f).get(index);	
	}

	public ArrayList<Integer> getClassNums_T() {
			return trainClassNums;			
	}
	
	public int getClassNum_T(int index) {
			return trainClassNums.get(index);			
	}
	public void addClassNum_T(int classNum) {
			trainClassNums.add(classNum);			
	}
///////////////////Accessors and mutators for validation RCs/////////////////////////////////////////////////
	public RegisterCollection rc_v(int s, int f, int index){
			return validCache.subpop(s).factor(f).get(index);
	}

	public ArrayList<Integer> getClassNums_V() {
			return validClassNums;			
	}
	
	public int getClassNum_V(int index) {
			return validClassNums.get(index);			
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
