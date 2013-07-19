package core;

import misc.FileReader;

public class MultiClassFileReader extends FileReader<MultiClassFitnessCase> {

	@Override
	public MultiClassFitnessCase readLine(String s) {
		return MultiClassFitnessCase.generate(s);
	} 

}
