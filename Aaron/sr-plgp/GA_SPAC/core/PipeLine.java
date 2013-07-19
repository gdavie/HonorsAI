package core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.omg.CORBA.DoubleSeqHelper;

import com.sun.corba.se.impl.ior.WireObjectKeyTemplate;

import libCPS.CPSwrapper;


public class PipeLine {

	private Settings settings;

	private int genNumber;

	private Generation currentGen;

	private SRP_FitnessEnvironment coherencies;
	private SRPE srpe;
	private CPSwrapper cps;

	private Crossover crossover;
	private Mutate mutate;

	private boolean test = true;

	public PipeLine(Settings set) {
		settings = set;
		currentGen = new Generation(settings);
		crossover = new Crossover(settings);
		mutate = new Mutate(settings);

		cps = new CPSwrapper();
		coherencies = new SRP_FitnessEnvironment();
		coherencies.addCasesFromFile(settings.gethardCodedFile()); //hardcoded value
		CPSwrapper.setMAXSIZE(coherencies.getLstFreq().size());
		srpe = new SRPE();

		genNumber = 0;
		run();
		System.out.println("--------Final Generation---------");
		currentGen.sort();
		printGeneration(currentGen);
		writeCurve(currentGen.getInds().get(0));
//		double[] testLayer = {0.09514085752104096, 0.29533381107572343 , 0.03334749487585918, 0.17091288189759749};
//		Individual testInd = new Individual(testLayer);
//		testInd.setFitness((float)srpe.execute(coherencies, testInd, cps));
//		writeCurve(testInd);
		System.out.println("--------FINISHED!---------");
		double[] layers = currentGen.getInds().get(0).getGenes();
		for(int j = 0; j < layers.length; j++){
			System.out.print(layers[j]+"  ");
		}
		System.out.println();
		System.out.println("Fitness = "+ currentGen.getInds().get(0).getFitness());
		writeLog();
	}

	private void run() {
		ArrayList<Individual> parentInds = new ArrayList<Individual>();
		ArrayList<Individual> childInds = new ArrayList<Individual>();
		ArrayList<Individual> mutantInds = new ArrayList<Individual>();
		for(int g = 0; g < settings.getGenerations(); g++){
			genNumber++;

			parentInds.clear();
			childInds.clear();
			mutantInds.clear();
			parentInds = new ArrayList<Individual>();
			if(test) System.out.println("DEBUG - Gen "+genNumber);
			for(Individual i: currentGen.getInds()){
				if(i.getFitness() == Double.MAX_VALUE){
					i.setFitness((float)srpe.execute(coherencies, i, cps));
				}
			}
			printGeneration(currentGen);
			getFittest(parentInds);
			if(test) System.out.println("DEBUG - Fittest Parents sorted. parentInds size = "+parentInds.size());
			childInds = crossover.produce(parentInds);
			if(test) System.out.println("DEBUG - Crossover complete. childInds size = "+childInds.size());

			mutantInds = mutate.produce(parentInds);
			if(test) System.out.println("DEBUG - Mutate complete. mutantInds size = "+mutantInds.size());
			parentInds.addAll(childInds);
			if(test) System.out.println("DEBUG - Childrend added to Parents. paretnInd size = "+parentInds.size());
			parentInds.addAll(mutantInds);
			if(test) System.out.println("DEBUG - Mutate added to Parents. paretnInd size = "+parentInds.size());
			currentGen.newGeneration(parentInds);
		}
		//syso fitest
	}

	private void getFittest(ArrayList<Individual> inds) {
		currentGen.sort();
		if(test) System.out.println("DEBUG - currentGen.getInds() = "+currentGen.getInds().size());
		inds.addAll(currentGen.getInds().subList(0, settings.getnumberOfElites()));

	}

	private void printGeneration(Generation g){
		System.out.println("------------ GEN "+genNumber+" ------------");
		for(Individual i: g.getInds()){
			System.out.println("Individual "+(g.getInds().indexOf(i)));
			double[] layers = i.getGenes();
			for(int j = 0; j < layers.length; j++){
				System.out.print(layers[j]+"  ");
			}
			System.out.println();
			System.out.println("Fitness = "+i.getFitness());
			System.out.println();
		}

	}

	public void writeLog(){

		try {
			FileWriter fstream = new FileWriter("finaloutput.txt",false);
			BufferedWriter w = new BufferedWriter(fstream);

			double[] layers = currentGen.getInds().get(0).getGenes();
			for(int j = 0; j < layers.length; j++){
				w.write(layers[j]+"  \n");
			}
			w.write("Fitness = "+ currentGen.getInds().get(0).getFitness());
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeCurve(Individual ind){
		//This will be to write the best one to file
		String site = "test"; //hardcoded
		try {
			FileWriter fstream = new FileWriter("plot."+site+".txt",false);
			BufferedWriter w = new BufferedWriter(fstream);
			w.write("Freq, Coherency\n");
			 for(int i = 0; i< ind.getFreq().length-1; i++){
				 if(!Double.isNaN(ind.getCoh()[i])){
					 w.write(ind.getFreq()[i]+","+ind.getCoh()[i]+"\n");
				 }
		        }
			 w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
