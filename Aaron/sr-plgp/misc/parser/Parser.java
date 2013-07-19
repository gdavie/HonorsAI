package parser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class Parser {

	//the fraction of individuals to place into a seperate test set
	private int fold = 1;

	private boolean crossValid = true;

	//Contains all of the instances in the file
	private ArrayList<Line> lines = new ArrayList<Line>();

	//this is neccesary to ensure correct file format, but is not actually used for anything;
	private String lineHeader = "fileName x y x2 y2";

	//the class number. is incremented each time a new 

	private Map<String, Integer> classNums = new HashMap<String, Integer>();

	private Map<String, Double> mapping = new HashMap<String, Double>();
	
	//the position of the className
	private int classPos;
	
	//the first non junk value
	private int startVal; 
	
	//the destination folder
	private String destination = "";

	private String delimeter = ",";

	private boolean translate = true;

	public Parser(String [] args){
		classPos = Integer.parseInt(args[1]);
		startVal = Integer.parseInt(args[2]);
		readFile(args[0]);
		shuffle();
		if(crossValid)
			do5CrossValid(args[0], fold);
		else
			writeFile(args[0]);
	}

	/**reads the input from a comma seperated file*/
	public void readFile(String fName){
		try {
			Scanner scan = new Scanner(new File(fName));
			while(scan.hasNext()){

				String line = scan.nextLine();
				Scanner lScan = new Scanner(line);
				
				if(delimeter != null){
					lScan.useDelimiter(delimeter);
				}

				Line instance = new Line();
				for(int i = 1;lScan.hasNext(); i++){
					
					boolean isDouble = lScan.hasNextDouble();
					String temp = lScan.next();
					if(i < startVal){
						continue;
					}
					else if(temp.equals("?"))
						instance.features.add(0.0);
					else if(i != classPos){
						if(!isDouble){
							if(mapping.containsKey(temp))
								instance.features.add(mapping.get(temp));
							else{
								instance.features.add((double)mapping.size());
								mapping.put(temp, (double)mapping.size());
							}
						}
						else
							instance.features.add(Double.parseDouble(temp));
					}
					else{
						instance.className = temp;
						//assigns the class a class num. Assignment order is forwards pass of the natural numbers.
						if(classNums.containsKey(instance.className))
							instance.classNum = classNums.get(instance.className);
						else{
							instance.classNum = classNums.size();
							classNums.put(instance.className, classNums.size());
						}
					}
				}
				lines.add(instance);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**shuffles the order of the training examples in the data set.*/
	public void shuffle(){
		for(int i = 0; i < lines.size()*20; i++){
			int x = (int)(Math.random()*(lines.size()-1));
			int y = (int)(Math.random()*(lines.size()-1));
			Line temp = lines.get(x);
			lines.set(x, lines.get(y));
			lines.set(y, temp);
		}
	}
	
	public void do5CrossValid(String fName, int fold){
		for(int i = 0; i < fold ;i++){
			writeFile(fName, (int)(((double)i)/fold * lines.size()), fold);
		}
	}

	public void writeFile(String fName, int start, int fold){
		try {
			FileWriter trainStream = new FileWriter(destination + "TRAIN" + fName);
			BufferedWriter trainOut = new BufferedWriter(trainStream);

			FileWriter testStream = new FileWriter(destination + "TEST" + fName);
			BufferedWriter testOut = new BufferedWriter(testStream);
			
			FileWriter validStream = new FileWriter(destination + "VALID" + fName);
			BufferedWriter validOut = new BufferedWriter(validStream);

			
			trainOut.write(lines.size() + " 0 " + classNums.size() + " " + lines.get(0).features.size()+ "\n");
			testOut.write(lines.size() + " 0 " + classNums.size() + " " + lines.get(0).features.size()+ "\n");
			validOut.write(lines.size() + " 0 " + classNums.size() + " " + lines.get(0).features.size()+ "\n");
			
			

			for(int i = 0; i < lines.size(); i++){
				Line l = lines.get(i);
				if(i%3==1)
					testOut.write(l.toString() + "\n");
				else if( i%3==2)
					validOut.write(l.toString() + "\n");
				else
					trainOut.write(l.toString() + "\n");
			}

			trainOut.close();
			testOut.close();
			validOut.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeFile(String fName){
		try {
			FileWriter stream = new FileWriter(destination + "PAR" + fName);
			BufferedWriter out = new BufferedWriter(stream);


			for(int i = 0; i < lines.size(); i++){
				Line l = lines.get(i);
				out.write(l.toString() + "\n");
			}

			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String [] args){
		new Parser(args);
	}

	private class Line {

		//The name of the class to which this instance belongs
		public String className;

		//The number associated with instances of this class (artificially created when file is read)
		public int classNum;

		//The features associated with this class instance
		public ArrayList<Double> features = new ArrayList<Double>();	

		public String toString(){
			String value = lineHeader + " " + classNum + " " + className;
			for(int i = 0; i < features.size(); i++){
				value += " " + features.get(i);
			}
			return value;
		}
	}
}
