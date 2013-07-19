package core;

import gp_operators.CrossoverHomologous;
import gp_operators.MacroMutation;

public class InstructionPopulation<INSPOP extends InstructionPopulation<INSPOP,INSP>,INSP extends InstructionProgram<INSP>> extends IPopulation<INSPOP,INSP> {

	public InstructionPopulation(IProgramFactory<INSP> programFactory, int popSize, int numFeatures, int numRegisters) {
		super(programFactory, popSize, numFeatures, numRegisters);
		setup();
	}

	protected void setup(){

		gp_ops.addElement(new MacroMutation<INSP>(), 20);
		//gp_ops.addElement(new MicroMutation<E>(numFeatures, numRegisters), 20);
		//gp_ops.addElement(new AddRandomInstruction<E>(), 10);
		//gp_ops.addElement(new RemoveRandomInstruction<E>(), 10); 
		gp_ops.addElement(new CrossoverHomologous<INSP>(), 20);
	}

}
