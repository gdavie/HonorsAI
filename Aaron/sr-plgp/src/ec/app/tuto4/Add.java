package ec.app.tuto4;
import ec.*;
import ec.gp.*;
import ec.util.*;

public class Add extends GPNode
    {
    public String toString() { return "+"; }

    public int expectedChildren() { return 2; }

	@Override
	public void eval(final EvolutionState state,
            final int thread,
            final GPData input,
            final ADFStack stack,
            final GPIndividual individual,
            final Problem problem){
				double result;
				DoubleData rd = ((DoubleData)(input));

				children[0].eval(state,thread,input,stack,individual,problem);
				result = rd.x;

				children[1].eval(state,thread,input,stack,individual,problem);
				rd.x = result + rd.x;
				}
    }