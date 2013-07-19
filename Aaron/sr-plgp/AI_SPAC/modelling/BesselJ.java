package modelling;

import java.io.BufferedReader;

import java.io.FileReader;
import java.util.ArrayList;
import cern.jet.math.*;
public class BesselJ {
	static ArrayList<Double>f = new ArrayList<Double>();
	static ArrayList<Double>c = new ArrayList<Double>();
	static ArrayList<Double>target = new ArrayList<Double>();
	//theory = bessel(2.0*Math.PI*f.get(i)*r/c.get(i),0) ;
	
	/**
	 * 	  { cylindrical Bessel function }
	  { of the first kind }
	  { the Gamma function is required }
	 * @param x
	 * @param n
	 * @return
	 */
	public static double bessel(double x, double n){
		//Variable declarations
		double TOT = 1.0E-5;
		double pi = Math.PI;
		double term, new_term, sum, x2;
		int i;
		
		double bessJ=0.0;
		//Begin the bessel function
			x2=x*x;
//			System.out.println(x+" "+n);
			if (x==0.0 && n ==1.0){
				bessJ =0.0;
			}else if(x>15){//asymptotic expansion
				bessJ =  Math.sqrt(2/(pi*x))*Math.cos(x - pi/4 - n*pi/2);
			}else{//regular infinite series 
				if(n==0.0){
			        sum = 1.0;
				}else{
			        sum = Math.exp(n * Math.log(x/ 2))/ gamma(n+1.0);
			      new_term = sum;
			      i = 0;
			    //  while(Math.abs(new_term)>Math.abs(sum*TOT)){
			      do{
				        i++;
				        term = new_term;
				        new_term = -term * x2 * 0.25/(i * (n + i));
				        sum += new_term;  
			      }
			      while(Math.abs(new_term)>Math.abs(sum*TOT));
			      bessJ = sum;
				}
			}

		return bessJ;
	}


	private static double gamma(double x){
		int i,j;
		double y, gam, gamma;
		if(x>=0.0){
			y=x+2;
			gam = Math.sqrt(2*Math.PI/y)*Math.exp(y*Math.log(y)
					+(1-1/(30*y*y))/(12*y)-y);
			gamma = gam/(x*(x+1));
		}else{//X < 0
			j=0;
			y=x;
			//while(y<=0){
			do{	
				j++;
				y++;
			}
			while (y<=0);
			gam = gamma(y);
			for(i=0; i <j-1; i++){
				gam = gam / (x+i);
			}
			gamma=gam;
		}
		return gamma;
	}
}
