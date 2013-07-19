package Killmes;

import java.util.Scanner;

import core.Config;
import core.IFitnessCase;
import core.MultiClassFitnessCase;


public class SRP_FitnessCase extends IFitnessCase {

	protected int classNumber;

	/** Initialise a MultiClassFitnessCase from a pattern file string (see docs for format)*/
	public SRP_FitnessCase(String patternFileLine){
		Scanner scan = new Scanner(patternFileLine);
		/*
		 * This is somewhat simplified as we are simply reading in a number of doubles.
		 * File format is much more compact --> data only, no headers to parse etc
		 */
		double bufDouble;
		int i = 0;
		//We only want the first two columns
		while(scan.hasNext()&&i<2) {
			bufDouble = scan.nextDouble();
			features.add(bufDouble);
			i++;
		}
		//Config.getInstance().numFeatures = i;
	}

	/** Return the 0-base class number of the class this case represents.*/
	public int classNumber()  {
		return classNumber; 
	}

	/** Returns a new MultiClassFitnessCase. Caller should manage the memory (which
     FitnessEnvironment will do if it is just passed to FitnessEnvironment::AddCase).*/
	public static SRP_FitnessCase generate(String pFL){
		return new SRP_FitnessCase(pFL);
	}
}
