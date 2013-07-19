package core;

import java.util.Scanner;

import core.Config;
import core.IFitnessCase;


public class MultiClassFitnessCase extends IFitnessCase {

	protected int classNumber;

	/** Initialise a MultiClassFitnessCase from a pattern file string (see docs for format)*/
	public MultiClassFitnessCase(String patternFileLine){
		Scanner scan = new Scanner(patternFileLine);

		double bufDouble;

		scan.next(); // The original image file for this pattern - don't keep
		scan.next(); // The x-position in this pattern - don't keep
		scan.next(); // The y-position in this pattern - don't keep
		scan.next(); // the second x-position in this pattern - don't keep
		scan.next(); // the second y-position in this pattern - don't keep
		classNumber = scan.nextInt();
		if(Config.getInstance().numClasses <= classNumber){
			Config.getInstance().numClasses = classNumber + 1;
		}
		scan.next(); // The name of the class in this pattern - don't keep

		int i = 0;
		while(scan.hasNext()) {
			bufDouble = scan.nextDouble();
			features.add(bufDouble);//TODO This is horrible. If T is anything but double..
			i++;
		}
		Config.getInstance().numFeatures = i;
	}

	/** Return the 0-base class number of the class this case represents.*/
	public int classNumber()  {
		return classNumber; 
	}

	/** Returns a new MultiClassFitnessCase. Caller should manage the memory (which
     FitnessEnvironment will do if it is just passed to FitnessEnvironment::AddCase).*/
	public static MultiClassFitnessCase generate(String pFL){
		return new MultiClassFitnessCase(pFL);
	}
}
