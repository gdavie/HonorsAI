

import misc.FileReader;
import core.MultiClassFitnessCase;

public class ARFF_Reader extends FileReader<MultiClassFitnessCase> {

	@Override
	public MultiClassFitnessCase readLine(String s) {
		return MultiClassFitnessCase.generate(s);
	} 

}
