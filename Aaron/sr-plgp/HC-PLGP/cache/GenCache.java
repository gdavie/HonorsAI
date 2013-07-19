package cache;

import java.util.ArrayList;

/**
 * Contains the caches for all subpopulations for 
 * a given generation 
 * @author AJ Scoble
 *
 */
public class GenCache extends ArrayList<SubPopCache> {

	public SubPopCache subpop(int index){
		return this.get(index);
	}
}
