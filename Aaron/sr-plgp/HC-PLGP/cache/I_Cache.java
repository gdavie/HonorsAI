package cache;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import core.RegisterCollection;

public abstract class I_Cache{
	
	//Two caches, one each for training and validation
//	public RegisterCollection [][][] trainCache;
//	public RegisterCollection [][][] validCache;
	//Number of training and validation instances.
//	public int numTrain, numValid;
	//classNums is collected once per experiment, and hold class labels over all instances.
	//These have been left as arraylists for now, performance boost by turning into arrays 
	//may not be worth time to refactor.
	public ArrayList<Integer> trainClassNums;
	public ArrayList<Integer> validClassNums;	
	// this counts mis-classifications, so numbers stored
	// allow us to cache 32767 incorrect classifications. plenty.
	public short [][][] fitness;//fitness [b][s][f]
	//This one accumulates over all blueprints. Needs a bit more space.
	public int [][] facFit;//fitness [s][f]
	// This will only store a value of 0 or 1.
	// Could use a boolean but this will do; and boolean
	// size is undefined.
	public byte [][][] done;//done[b][s][f]

		
		public boolean labelsCaptured = false;
		
		public abstract void initialiseCaches(int blueprints, int subpops, int factors);
		public abstract void addInstanceRC_T(RegisterCollection rc, int s, int f, int instNum);
		public abstract void addInstanceRC_V(RegisterCollection rc, int s, int f, int instNum);
		
		public abstract RegisterCollection rc_T(int s, int f, int instNum);
		public abstract RegisterCollection rc_v(int s, int f, int instNum);
		
		public abstract ArrayList<Integer> getClassNums_T() ;
		public abstract ArrayList<Integer> getClassNums_V() ;
		
		public abstract void addClassNum_T(int classNum) ;
		public abstract void addClassNum_V(int classNum) ;
		
		public abstract void killTrainCache();
		public abstract void killValidCache();

}

