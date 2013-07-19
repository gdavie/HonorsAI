package misc;

import core.IFitnessCase;

public abstract class FileReader<F extends IFitnessCase> {
	
	public abstract F readLine(String s);
	
}
