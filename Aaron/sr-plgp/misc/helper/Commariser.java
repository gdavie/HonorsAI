package helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class Commariser {

	public static void main (String [] args){

		String fileName = args[0];

		try {
			Scanner scan = new Scanner(new File(fileName));
			
			FileWriter fstream;
			fstream = new FileWriter(fileName + ".csv",true);

			BufferedWriter fout = new BufferedWriter(fstream);
			String result = "";

			while(scan.hasNext()){
				result += scan.nextDouble() + "," + scan.nextDouble() + "\n";
			}

			fout.write(result);
			fout.close();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
