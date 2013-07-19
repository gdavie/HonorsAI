package core;

import java.util.ArrayList;

public abstract class InstructionProgram<INSP extends InstructionProgram<INSP>> extends IProgram<INSP>{

	protected ArrayList<Instruction> instructions;
	
	public InstructionProgram(IFitnessMeasure tfm, IFitnessMeasure vfm, int programSize, int numFeatures, int numRegisters) {
		super(tfm, vfm, numFeatures, numRegisters);
		instructions = new ArrayList<Instruction>();

		for(int i = 0; i < programSize; ++i) {
			Instruction ins = instructionFactory();
			instructions.add(ins);
		}
	}
	
	public <X extends InstructionProgram<X>>InstructionProgram(InstructionProgram<X> rhs){
		super(rhs);

		// Copy the instructions:
		instructions = new ArrayList<Instruction>();
		for(int i = 0; i < rhs.size(); ++i) {
			instructions.add(rhs.instructions.get(i).clone());
		}
	}
	
	public void addInstruction(Instruction ins){
		instructions.add(ins);
	}
	
	public void reinitialize(INSP rhs){
		super.reinitialize(rhs);

		/**first copy the program*/
		int size = size();
		int rhs_size = rhs.size();
		int min = Math.min(size, rhs_size);
		
		for(int i = 0; i < min; i++){
			copyInstruction(rhs.instructions.get(i), i);
			
		}
		if(size > rhs_size){
			for(int i = size-1; i >= rhs_size; i--){//going downwards to save computation time
				removeInstruction(i);
			}
		}
		else if(size < rhs_size){
			for(int i = size; i < rhs_size; i++){
				addInstruction(rhs.instructions.get(i).clone());
			}
		}
	}
	
	public void copyInstruction(Instruction ins, int pos){
		instructions.get(pos).copy(ins);
	}
	
	public void insertInstruction(int index, Instruction ins){
		instructions.add(index, ins);
	}

	/** If the program is longer than the maximum length allowed by the configuration,
	 instructions are randomly selected and removed until it is at the maximum length.*/
	public int randomlyCullToSize(int cullToThisSize){
		return Instruction.randomlyCullToSize(instructions, cullToThisSize);
	}
	
	/** NOTE: THIS METHOD VIOLATES THE WHOLE OBJECT ENCAPSULATION SCHEMATA. It is necessary
	 though */
	public ArrayList<Instruction> rawAccessToTheInternalInstructions() {
		return instructions; 
	}
	
	public void removeInstruction(int index){
		instructions.remove(index);
	}
	
	public int removeRandomInstruction(){
		return Instruction.removeRandomInstruction(instructions);
	}
	
	public Instruction replaceInstruction(int index, Instruction ins){
		return instructions.set(index, ins);
	}
	
	public void setInstructions(ArrayList<Instruction> ins){
		instructions = ins;
	}
	
	/** Returns the number of instructions (both introns and exons) in the program.*/
	public int size(){ 
		return instructions.size(); 
	}
}
