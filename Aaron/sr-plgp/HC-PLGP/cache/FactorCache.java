package cache;

import java.util.ArrayList;

import core.RegisterCollection;

/**
 * This is a simple arraylist that holds cache values
 * for a number of instances for a single factor.
 * @author AJ Scoble
 *
 */
public class FactorCache extends ArrayList<RegisterCollection> {
	private static final long serialVersionUID = -6656050150363140324L;
	
	public RegisterCollection instance(int index){
		return this.get(index);
	}

}
