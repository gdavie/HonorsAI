/**
 * 
 */
package cache;

import java.util.ArrayList;


/**
 * Contains all factorRCAllInstances for a given subpop
 * @author AJ Scoble
 *
 */
public class SubPopCache extends ArrayList<FactorCache> {
	
	public FactorCache factor(int index){
		return this.get(index);
	}

	
}
