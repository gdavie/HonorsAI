package misc;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

import core.Config;


public class Run_Stats{

	private Map<Integer, Stats> averages = new HashMap<Integer, Stats>();
	private Config c;

	public static final int totalTime = 0;

	public static final int bestGen=1;

	public static final int bestGenTime = 2;

	public static final int trainAcc = 3;

	public static final int validAcc = 4;

	public static final int testAcc = 5;

	public Run_Stats(Config c){

		for(int i = 0; i < 6; i++){
			averages.put(i, new Stats());
		}

		this.c = c;
	}

	public String toString(){

		Formatter f = new Formatter();

		f.format("%20s, %10d, %10d, %10d, %10d,", c.file, c.numPieces, c.maxLength, c.numRegisters, c.maxGenerations);
		for(int i = 0; i < 6; i++){
			f.format(" %10f, ", averages.get(i).mean);
		}
		f.format(" %10f, %10f", averages.get(trainAcc).var, averages.get(testAcc).var, "\n");

		return f.toString();
	}

	public String header(){
		Formatter f = new Formatter();
		f.format("%20s, %10s, %10s, %10s, %10s, %10s, %10s, %10s, %10s, %10s, %10s, %10s, %10s\n",c.file, 
				"factors", "maxLength", "numReg", "maxGen", "totTime",
				"bestGen", "bstGenT", "trainAcc","validAcc", 
				"testAcc", "trainSD", "testSD");

		return f.toString();
	}

	public void updateSum(int type, double value){
		averages.get(type).update(value);
	}

}
