package misc;

import java.util.Random;

import arguments.ConstantFactory;

public class Rand {

	private static Random r = new Random();
	
	/** Initialise the random number generator with a time-dependent seed.*/
    public static void Init(){
    	double seed =System.currentTimeMillis()/1000;
    	System.out.println("SEED RANDOM "+seed);
    	r.setSeed((int)(seed));
    }
    
    /** Initialise the random number generated with a specified seed.*/
    public static void Init(int seed){
    	r.setSeed(seed);
    }
    
    /** Returns a random integer in the range [0, ceiling).*/
    public static int Int(int ceiling){
    	
    	return ceiling!=0 ? r.nextInt(ceiling) : 0;
    	
    }
    
    /** Returns a random integer in the range [0, RAND_MAX].*/
    public static int Int(){
    	return r.nextInt(Integer.MAX_VALUE);
    }
    
    /** Returns a float in the range [0, 1).*/
    public static float uniform(){
    	return r.nextFloat();
    }
    
    /** Returns a float normally distributed from the mean */
    public static double normal(double mean,double sd){
    	double u1,u2,r,theta,v,pi=3.141592654;
    	  u1 = uniform();
    	  u2 = uniform();
    	  r = Math.sqrt(-2.0*Math.log(u1));
    	  theta =2.0*pi*u2;
    	  v = r*Math.cos(theta);
    	  return(sd*v+mean);
    }
    
   public static double generateRandomConstant(ConstantFactory cf){
	   return cf.createConstant();
   }

	
}
