package ec.app.tuto4;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class Div extends GPNode
	{
	    public String toString() { return "/"; }

	    public int expectedChildren() { return 2; }

	    public void eval(final EvolutionState state,
	                     final int thread,
	                     final GPData input,
	                     final ADFStack stack,
	                     final GPIndividual individual,
	                     final Problem problem)
	        {
	        double result;
	        DoubleData rd = ((DoubleData)(input));

	      //evaluate children[1] first to determine if the demoniator is 0
	        children[1].eval(state,thread,input,stack,individual,problem);
	        if (rd.x==0.0)
	            // the answer is 1.0 since the denominator was 0.0
	            rd.x = 1.0;
	        else
	            {
	            result = rd.x;

	            children[0].eval(state,thread,input,stack,individual,problem);
	            rd.x = rd.x / result;
	            }




//	        children[0].eval(state,thread,input,stack,individual,problem);
//	        result = rd.x;
//
//	        children[1].eval(state,thread,input,stack,individual,problem);
//	        rd.x = result/rd.x;
	        }
	    }


