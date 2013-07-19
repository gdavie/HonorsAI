package cache;

import core.RegisterCollection;

public class Normaliser {
	RegisterCollection out;
	boolean dbg;

	public Normaliser(){
		dbg = false;
		if(dbg)System.out.println("*********************************");		
	}
	
	public double normalise(double x, int function){
	double norm=0;
		if(dbg)System.out.print(": "+x);
		switch(function){
		case 0:
			norm = Math.tanh(x);
			break;
		case 1:
			norm = x/(Math.sqrt(1+(x*x)));
			break;
		case 2:
			norm = x/(1+Math.abs(x));
			break;
		default:
			System.out.println("Normaliser Fall-through");
		}
		if(dbg)System.out.print("n "+norm+" |");
		if(norm>1||norm<-1)System.out.println("<<<<<<Normalisation value "+norm);
	 return norm;
	}
}
