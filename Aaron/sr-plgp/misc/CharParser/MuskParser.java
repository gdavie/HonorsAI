package CharParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class MuskParser {
	
	//Contains all of the instances in the file
	private ArrayList<String> lines = new ArrayList<String>();
	
	public MuskParser(){
		try {
			
			int numMusk = 0, numNon = 0;
			
			Scanner scan = new Scanner(new File("datasets/musk.data"));
			
			String cls, output = "";
			int clsNum;
			while(scan.hasNext()){
				String line = scan.nextLine();
				Scanner lineScan = new Scanner(line);
				lineScan.useDelimiter(",");
				
				cls = lineScan.next();//get the class
				lineScan.next();//ignore the exact type
				clsNum = cls.substring(0, 1).equals("N")?0:1;
				
				numMusk += clsNum;
				numNon += clsNum==1?0:1;
				
				output = "fileName x y x2 y2";
				
				output += " " + clsNum + " " + cls.substring(0,1);
				while(lineScan.hasNext()){
					output += " " + lineScan.nextDouble();
				}
				
				if(clsNum == 1){
					for(int i = 0; i < 5; i++){
						lines.add(output);
					}
				}
				else{
					lines.add(output);
				}
			}
		
			System.out.println("musk: " + numMusk);
			System.out.println("non: " + numNon);
			
			shuffle();
			
			writeFile();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**shuffles the order of the training examples in the data set.*/
	public void shuffle(){
		for(int i = 0; i < lines.size()*20; i++){
			int x = (int)(Math.random()*(lines.size()-1));
			int y = (int)(Math.random()*(lines.size()-1));
			String temp = lines.get(x);
			lines.set(x, lines.get(y));
			lines.set(y, temp);
		}
	}

	public void writeFile(){
		try {
			FileWriter trainStream = new FileWriter("datasets/TRAINmusk.data");
			BufferedWriter trainOut = new BufferedWriter(trainStream);

			FileWriter testStream = new FileWriter("datasets/TESTmusk.data");
			BufferedWriter testOut = new BufferedWriter(testStream);
			
			FileWriter validStream = new FileWriter("datasets/VALIDmusk.data");
			BufferedWriter validOut = new BufferedWriter(validStream);

			
			trainOut.write(lines.size() + " 0 " + 2 + " " + 168 + "\n");
			testOut.write(lines.size() + " 0 " + 2 + " " + 168 + "\n");
			validOut.write(lines.size() + " 0 " + 2 + " " + 168 + "\n");
			
			for(int i = 0; i < lines.size(); i++){
				String l = lines.get(i);
				if(i%3==1)
					testOut.write(l + "\n");
				else if( i%3==2)
					validOut.write(l + "\n");
				else
					trainOut.write(l + "\n");
			}

			trainOut.close();
			testOut.close();
			validOut.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String [] args){
		
		new MuskParser();
	}
	
}
