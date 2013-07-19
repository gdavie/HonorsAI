package CharParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**this class parses the data held in the characters dataset*/
public class CharParser {

	private int numAtt = 6;

	private int max = 0;

	private String destination = "caps.data";

	ArrayList<Tuple> instances = new ArrayList<Tuple>();

	public CharParser(String [] args){
		readFiles(args[0]);
		shuffle();
		buffer();
		printFile();
	}
	
	private void buffer(){
		for(Tuple t: instances){
			while(t.count < max){
				t.value += " 0 0 0 0 0 0";//adding in buffer strings
				t.count++;
			}
		}
	}

	private void readFiles(String folderName){
		System.out.println("reading files");
		File folder = new File(folderName);
		File[] files = folder.listFiles();

		for(int i = 0; i < files.length; i++){
			System.out.println("reading file: " + i);
			File f = files[i];
			if(f.isFile()){
				String s = "";
				try {
					Scanner scan = new Scanner(f);
					
					int nl = 0;
					int cls = 0;
					
					while(scan.hasNext()){
						
						nl++;
						if(nl > max)
							max = nl;
						
						String line = scan.nextLine();
						Scanner ls = new Scanner(line);
						
						cls = ls.nextInt() - 1;
						
						ls.next(); ls.next();//skip over the segment number and the word line
						for(int j = 0; j < numAtt; j++){
							s += ls.nextDouble() + " ";
						}
					}
					s = f.getName() + " x y " + cls + " " + f.getName().substring(0,1) + " " + s;
					s = s.trim();
					instances.add(new Tuple(nl, s));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void shuffle(){
		System.out.println("shuffling");
		for( int i = 0; i < instances.size()*20; i++){
			int i1 = (int)(Math.random()*instances.size());
			int i2 = (int)(Math.random()*instances.size());
			Tuple temp = instances.get(i1);
			instances.set(i1, instances.get(i2));
			instances.set(i2, temp);
		}
	}

	private void printFile(){
		FileWriter trainStream;
		try {
			System.out.println("Writing files to: " + destination);
			trainStream = new FileWriter(destination);
			BufferedWriter trainOut = new BufferedWriter(trainStream);

			for(Tuple s : instances){
				trainOut.write(s.value + "\n");
			}

			trainOut.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("max was: " + max);
	}

	public static void main(String [] args){
		new CharParser(args);
	}
	
	private class Tuple{
		public int count;
		public String value;
		
		public Tuple(int count, String value){
			this.count = count;
			this.value = value;
		}
	}
}
