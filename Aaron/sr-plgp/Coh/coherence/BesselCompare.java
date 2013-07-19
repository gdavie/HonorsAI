package coherence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
public class BesselCompare {
	static ArrayList<Double>freq = new ArrayList<Double>();
	static ArrayList<Double>vel = new ArrayList<Double>();
	static ArrayList<Double>target = new ArrayList<Double>();
	static boolean checkError=false;
	
	
	public static void main(String [] args){
		String ASCfileName = args[0];
		String theoCohFileName= args[1];
		double r = Double.parseDouble(args[2]);
		String outFileName= args[3];
		readASC(ASCfileName);
		if(checkError)readCOH(theoCohFileName);
		iterate(r, outFileName);
	}
	
	private static void iterate(double r, String outFileName){
		double theory;
		String strLine;
		//read the ASC file
		double sum =0.0;
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(outFileName));
			out.write("Freq,Theo\n");
			for(int i=0; i< freq.size()-1; i++){
				double c=vel.get(i)*1000;
				double f=freq.get(i);
				double pi = Math.PI;
				//This is the library Bessel function. For tsting only.
		//		theory = cern.jet.math.Bessel.j0(2.0*pi*f*r/c) ;
				//This is the ported equation. Use this, as it will allow us
				//to modify it if we see fit at any stage.
				theory = PasBessel.bessel(2.0*pi*f*r/c,0) ;
				if(checkError)sum = sum + (Math.abs(theory-target.get(i)))/freq.get(i)/freq.get(i);
				if(checkError)System.out.println(freq.get(i) +" "+theory+" "+target.get(i));
				strLine = freq.get(i) +","+theory+"\n";
				out.write(strLine);
			}
			if(checkError)System.out.println("Sum "+sum);
		out.flush();
		out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	private static void readASC(String ASCfileName){
		String[] tokens;
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(ASCfileName));		
		String strLine;
		in.readLine();//dump header
		//Read File Line By Line
		while ((strLine = in.readLine()) != null)   {
			//System.out.println(strLine);
			tokens = strLine.split("\\s+");
			freq.add(Double.parseDouble(tokens[4]));
			vel.add(Double.parseDouble(tokens[5]));
		}
		in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void readCOH(String theoCohFileName){
		String[] tokens;
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(theoCohFileName));
		

		String strLine;
		in.readLine();//dump header?
		//Read File Line By Line
		int z =0;
		while ((strLine = in.readLine()) != null)   {
			tokens = strLine.split("\\s+");
			target.add(Double.parseDouble(tokens[2]));
			z++;
		}
		in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
