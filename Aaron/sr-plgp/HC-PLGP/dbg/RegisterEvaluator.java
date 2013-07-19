package dbg;
 /**
  * This is so we can extract information from the registers.
  * We wish to observe which registers remain unused, and whether we can
  * obtain any useful knowledge from that.
  * 
  * Maybe we can eventually tie that in with fitness or selection
  * information to really work out how the evolutionary process is working
  * @author AJ Scoble
  *
  */
public class RegisterEvaluator {
	//registers per subpopulation
	//			per blueprint
	//			per generation
	private int[][] bpNullRegs;
	private int[][][][] genRegs;
	private int [][] stats;
	public int gen;
	public int bp;
	public int slot;
	public int numreg;
	public int numBp;
	public int numSlot;

	public RegisterEvaluator(int numBlueprints, int slots, int numGenerations, int numReg){
		this.numreg=numReg;
		this.numBp=numBlueprints;
		this.numSlot=slots;
		//This will keep the NR counter for one blueprint. Subpops * registers
		bpNullRegs= new int[numSlot][numreg];
		//This will add a bpNullReg for each blueprint*generation
		genRegs= new int[numGenerations+1][numBlueprints][numSlot][numreg];
		//This is to collate the registers by slot; blueprints are irrelevant here
		stats= new int[numSlot][numreg];
		gen=0;
		bp=0;
		slot=0;
	}


	public void setBlueprintRegs(int[][] blueprintRegs) {
		this.bpNullRegs = blueprintRegs;
	}

	public void clearStats(){
		stats= new int[numSlot][numreg];
	}
	public void finishGen(){
		gen++;
		//System.out.println("RE Finished gen "+gen);
		bp=0;
	}
	//Adds a set of register flags for one slot in the blueprint(subpop)
	public void addRegisters(int[] reg){
		bpNullRegs[slot]=reg;
		//And add to totals
		for(int i=0;i<reg.length; i++){
			if(reg[i]>0)stats[slot][i]++;
		}
		slot++;
	}
	//Add the blueprint to the generation counter
	public void finishedBP(){
		//System.out.println("finished BP gen "+gen+" bp "+bp);
		genRegs[gen][bp]=bpNullRegs;
		if(bp>=(numBp-1)){//Wrap around to bp 0
			bp=0;
		}else{
			bp++;			
		}
		slot=0;
	}

	/**
	 * outputs NR info for a single slot in a BP
	 * Should extend this to select a particular slot.
	 */
	public void printSingleSlot() {
		System.out.print("bp "+bp+":");
		for(int i=0; i<numreg; i++){
			System.out.print(bpNullRegs[slot-1][i]+" ");
		}
		System.out.println();
	}

	/**
	 * Iterates through each slot in the blueprint and
	 * print the registers
	 */
	public void printSingleBP() {
		System.out.println("blueprint "+bp);
		for(int s=0; s< numSlot; s++){
			System.out.print("Slot "+s+" : ");
			for(int i=0; i<numreg; i++){
				System.out.print(bpNullRegs[s][i]+" ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	/**
	 * Iterates through all the blueprints in a generation
	 * Iterates through each slot in the blueprint and
	 * print the registers
	 */
	public void printBPregs() {
		System.out.println("Generation "+gen);
		for(int b=0; b<numBp;b++){
			System.out.println("blueprint "+b);
			for(int s=0; s< numSlot; s++){
				System.out.print("Slot "+s+" : ");
				for(int i=0; i<numreg; i++){
					//
					System.out.print(bpNullRegs[s][i]+" ");
					System.out.print("//Broken counting");
				}
				System.out.println();
			}			
		}

		System.out.println();
	}
	
	/**
	 * Iterates through all the blueprints in a generation
	 * Iterates through each slot in the blueprint and
	 * print the totals for each register by slot only
	 */
	public void printGenTotals() {
		System.out.println("Generation "+gen);
			for(int s=0; s< numSlot; s++){
				System.out.print("Slot "+s+" : ");
				for(int i=0; i<numreg; i++){
					System.out.print(stats[s][i]+" ");
				}
				System.out.println();
			}			
		System.out.println();
	}
}
