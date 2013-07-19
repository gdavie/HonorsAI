package core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import misc.Stats;

public class Logger {
	

	public static Stats[] avgBestFit;
	private static String path="";
	
	public static void initialize(String pth){
		path=pth;
		//for keeping track of the average best fitness at each generation
		avgBestFit = new Stats[Config.getInstance().maxGenerations];
		for(int i = 0; i < avgBestFit.length; i++){
			avgBestFit[i] = new Stats();
		}
	}
	
	public static void updateAvgBestFit(int gen, double fit){
		avgBestFit[gen].update(fit);
	}
	
	/** Appends statistics regarding the population to the file filePath. identifier is the 
	 generation number or something similar identifying why the logging is happening. The
	 method assumes the fitneses for each program are correct. This method is not const
	 because it calls GetFittestProgram, which is not const because it sorts the internal
	 vector of programs.*/
	public static<T extends IPopulation<T,E>,E extends IProgram<E>> 
		void logStatistics (String filePath, String identifier, IPopulation<T,E> pop){

		System.out.println("logging statistics");

		FileWriter fstream;
		try {
			fstream = new FileWriter(filePath,true);

			BufferedWriter fout = new BufferedWriter(fstream);

			fout.write("############################## " + identifier + " ##############################"
					+ "\n");

			ArrayList<E> programs = pop.getPrograms();

			// Calculate the statistics:
			double fitnessSum = 0;
			double sizeSum = 0;
			Map<String, Integer> programCount = new HashMap<String,Integer>();
			for(int i = 0; i < programs.size(); i++){
				programCount.put(programs.get(i).toString(), 0);
			}

			for(int i = 0; i < pop.size(); ++i) {
				fitnessSum += programs.get(i).fitness();
				sizeSum += programs.get(i).size();

				programCount.put(programs.get(i).toString(), programCount.get(programs.get(i).toString())+1);
			}


			fout.write( "Diversity:" + programCount.size() + " / " + pop.size() 
					+ "\n");
			fout.write("Average Fitness:" + fitnessSum / pop.size() + "\n");
			fout.write("Average Size:" + sizeSum / pop.size() + "\n" + "\n");

			fout.write("Current Best Program:\n" + pop.best.toString(true, true) + "\n");
			fout.write("\n\n\n\n");
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Logs the string representation of the entire population to the file filePath, overwriting
	 whatever is already at filePath. The method assumes the fitnesses are all correct and
	 that the introns are correctly marked.*/
	public static<T extends IPopulation<T,E>, E extends IProgram<E>> void logPopulation(String filePath, IPopulation<T,E> pop){

		System.out.println("logging population");

		FileWriter fstream;
		try {
			fstream = new FileWriter(path+"out.txt",true);

			BufferedWriter fout = new BufferedWriter(fstream);
			fout.write(pop.toString(true, true) + "\n");
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** This method calls the statistics logging and full population logging functions. The 
	 method assumes the fitnesses are all correct and that the introns are correctly marked.
	 Not const because LogStatistics isn't.*/
	public static<T extends IPopulation<T,E>, E extends IProgram<E>> void log(String identifier, IPopulation<T,E> pop){
		logStatistics(Config.getInstance().statsLogFilePath + ".txt", identifier, pop);
		logPopulation(Config.getInstance().popLogFilePath + "." + identifier + ".txt", pop);
	}

	/** Logs the statistics and sometimes the population, depending on the value of gen. The 
	 method assumes the fitnesses are all correct and that the introns are correctly marked.
	 Not const because LogStatistics isn't.*/
	public static<T extends IPopulation<T,E>, E extends IProgram<E>> void log(int gen, IPopulation<T,E> pop ){
		String conv = "";
		conv += gen;
		logStatistics((Config.getInstance().statsLogFilePath + ".txt"), conv, pop);//TODO removed c_str()
		if((gen % Config.getInstance().popLogInterval) == 0) {
			logPopulation(Config.getInstance().popLogFilePath + "." + conv + ".txt", pop);
		}
	}
	
	public static void logBestFits(){
		System.out.println("Logging best fitnesses");
		FileWriter fstream;
		try {
			fstream = new FileWriter(Config.getInstance().bestFitsFilePath + ".csv",true);

			BufferedWriter fout = new BufferedWriter(fstream);
			String result = "";
			for(int i = 0; i < avgBestFit.length; i++){
				result += avgBestFit[i].mean + ", " + avgBestFit[i].var + "\n";
			}
			fout.write(result);
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}


}
