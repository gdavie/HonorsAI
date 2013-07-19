package core;

import dbg.RegisterEvaluator;



public abstract class SPLGP_Blueprint<BP extends SPLGP_Blueprint<BP,IPR>,IPR extends IProgram<IPR>> 
		extends APartsProgram<BP,IPR>{

	public IPopulation<?,IPR>[] sub_pops;
	private int selectedFactor[];
	private int id;
	/**normal constructor*/
	public SPLGP_Blueprint(int prog_size, IPopulation<?,IPR>[] sub_pops, int numPieces, int numFeatures, int numRegisters){
		super(new MultiClassFitnessMeasure(), new MultiClassFitnessMeasure(), numPieces,
				numFeatures, numRegisters);
		this.sub_pops = sub_pops;
		selectedFactor = new int[numPieces];
		for(int i = 0; i < numPieces; i++){
			int index = (int)(Math.random()*sub_pops[i].size());
			parts[i] = getProgram(i, index);
			selectedFactor[i]=index;
		}
	}

	public <X extends SPLGP_Blueprint<X,IPR>> SPLGP_Blueprint(SPLGP_Blueprint<X, IPR> cp){
		super(cp);

		sub_pops = cp.sub_pops; //TODO this could be problematic. shared population at the moment
	}
	
	public void reinitialize(BP cp){
		super.reinitialize(cp);

		for(int i = 0; i < cp.parts.length; i++){
			parts[i] = cp.parts[i];//These are always indices, NOT programs, DO NOT DEEP CLONE!!!
		}
		
		sub_pops = cp.sub_pops; //TODO this could be problematic. shared population at the moment

	}
	
	public void pmReinitialize(BP cp){
		super.reinitialize(cp);
		sub_pops = cp.sub_pops; //TODO this could be problematic. shared population at the moment
	}

	@Override
	public void markIntrons() {
		for(IPR sp : parts){
			sp.markIntrons();
		}
	}

	@Override
	public int randomlyCullToSize(int maxLength) {
		// TODO FILL THIS IN PRONTO
		return 1;
	}

	public int selectedFactor(int subpop) {
		return selectedFactor[subpop];
	}

	public void setSelectedFactor(int subpop, int factor) {
		selectedFactor [subpop]= factor;
	}

//	public int getId() {
//		return id;
//	}
//	public void setId(int id) {
//		this.id = id;
//	}

	/**returns the prog_num program from the sub_pop_num'th sub population
	 * 
	 * @param sub_pop_num the sub population to read from
	 * @param prog_num the program index to read
	 * @return the associated program
	 */
	public IPR getProgram(int sub_pop_num, int prog_num){
		return sub_pops[sub_pop_num].programs.get(prog_num);
	}
	public void markIntrons(RegisterEvaluator re) {
		for(IPR sp : parts){
			/** here is where we can
			 * capture the stats for each subpop.
			 *
			 */
			//System.out.println("-------------------");
			//this will iterate through each subpop
			sp.markIntrons(re);//AzM this will return the register values we want, one set per slot
			//So we then want to keep these registers 1..n and add them across the subpops.
		}
		//System.out.println("Sanity BP "+this.id);
		if(re!=null)re.finishedBP();//This should copy the bp regcounter into the generation store and reset the subpop counter
		//re.printSingleBP();
	}
}
