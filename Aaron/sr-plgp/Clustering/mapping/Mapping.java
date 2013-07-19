package mapping;

import java.util.List;
import java.util.Map;

import core.MultiClassProgram;

public abstract class Mapping {

	public abstract Map<MultiClassProgram, MultiClassProgram> map(List<MultiClassProgram> list, int numClusters);
	
}
