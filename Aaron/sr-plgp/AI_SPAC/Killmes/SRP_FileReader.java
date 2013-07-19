package Killmes;

import misc.FileReader;

public class SRP_FileReader extends FileReader<SRP_FitnessCase> {

	@Override
	public SRP_FitnessCase readLine(String s) {
		return SRP_FitnessCase.generate(s);
	} 

}
