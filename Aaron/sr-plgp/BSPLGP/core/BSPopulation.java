package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import local.Localiser;

public class BSPopulation extends ManyPopulation<BSPopulation, BSProgram, MultiClassProgram>{

	boolean TEST = false;

	private static final int TRAIN=0, VALID=1;
	private static final int MAX = Config.getInstance().populationSize/Config.getInstance().numPieces;

	//set up the cache
	//[train||test][which subpop][which program][which training example][which register]
	double[][][][][] cache = new double[2][config.numPieces][config.populationSize/config.numPieces][config.numTrain+1][config.numRegisters];

	//extract and store all result classes for easy access. Should really do this during initialization.
	//stored in the form int[train/test][num]
	int[][] output = null;

	//stores average fitness values
	Fit[][] fits = new Fit[config.numPieces][MAX];

	public BSPopulation(MultiClassPopulation[] sub_pops, int numFeatures, int numRegisters){
		super(sub_pops, new BSProgramFactory(sub_pops, numFeatures, numRegisters), numFeatures, numRegisters);

		for(int i = 0; i < config.numPieces; i++){
			for(int j = 0; j < MAX; j++){
				fits[i][j] = new Fit();
			}
		}
	} 

	public static BSPopulation create(MultiClassProgramFactory mcpf, int numFeatures, int numRegisters) {
		MultiClassPopulation[] sub_pops = new MultiClassPopulation[Config.getInstance().numPieces]; //TODO create these
		for(int i = 0; i < sub_pops.length; i++){
			sub_pops[i] = new MultiClassPopulation(Config.getInstance().populationSize/Config.getInstance().numPieces
					,numFeatures, numRegisters);
		}

		return new BSPopulation(sub_pops, numFeatures, numRegisters);
	}

	/** Updates the fitness of all programs which currently have false fitness-is-correct status
	 flags. After this method is called all programs in this population will have their
	 fitness values set correctly.*/
	@Override
	public <F extends IFitnessCase> void evaluateFlaggedPrograms(FitnessEnvironment<F> tfe, FitnessEnvironment<F> vfe){

		/**STEP 0: make sure the training and validation set examples have had their output cached*/

		if(output == null){
			output = new int[2][Math.max(config.numTrain, config.numValid)];
			List<MultiClassFitnessCase> cases = (List<MultiClassFitnessCase>)tfe.getCases();
			for(int i = 0; i < config.numTrain; i++){
				output[TRAIN][i] = cases.get(i).classNumber();
			}

			cases = (List<MultiClassFitnessCase>)vfe.getCases();
			for(int i = 0; i < config.numValid; i++){
				output[VALID][i] = cases.get(i).classNumber();
			}
		}


		/**STEP 1: Evaluate all factors on all training examples*/

		RegisterCollection frv = new RegisterCollection(config.numRegisters);

		//for every subpopulation
		for(int popNum = 0; popNum < sub_pops.length; popNum++){
			IPopulation<?,MultiClassProgram> sub_pop = sub_pops[popNum];
			MultiClassProgram mcp;

			//for every program in that subpop
			for(int progNum = 0; progNum < sub_pop.size(); progNum++){
				mcp = sub_pop.get(progNum);

				//for every training case
				tfe.loadFirstCase();
				int count = 0;
				do {
					tfe.zeroRegisters();

					mcp.execute(tfe, frv, count);

					double[] memory = cache[TRAIN][popNum][progNum][count];
					System.arraycopy(frv.getRegisters(), 0, memory, 0, config.numRegisters);

					count++;
				} while (tfe.loadNextCase());	


				//for every validation case
				count = 0;
				vfe.loadFirstCase();
				do {
					vfe.zeroRegisters();

					mcp.execute(vfe, frv, count);

					double[] memory = cache[VALID][popNum][progNum][count];
					System.arraycopy(frv.getRegisters(), 0, memory, 0, config.numRegisters);

					count++;
				} while (vfe.loadNextCase());	
			}
		}

		/**STEP 2: Search for good blueprints*/

		//STEP 2.1: find a good ordering of the pieces
		List<MultiClassProgram>[] ordered = new List[config.numPieces];
		for(int i = 0; i < config.numPieces; i++){
			ordered[i] = Localiser.order(sub_pops[i].programs);
		}

		//STEP 2.2: do PSO to find blueprints.

		//initialize a set of random blueprints;
		for(int i = 0; i < config.numBluePrints; i++){

			//re-randomize the indices so we can track them. Hack but oh well.
			for(int j = 0; j < config.numPieces; j++){
				int index = (int)(Math.random()*sub_pops[j].size());
				programs.get(i).parts[j] = sub_pops[j].programs.get(index);
				programs.get(i).indices[j] = index;
			}
		}

		//evaluate the new random blueprints
		sumAllBlueprints(TRAIN, config.numTrain);
		sumAllBlueprints(VALID, config.numValid);


		//get ready to store fitness values. double[sub_pop][program]
		for(int i = 0; i < config.numPieces; i++){
			for(int j = 0; j < MAX; j++){
				fits[i][j].clear();
			}
		}


		//do pso
		double avg_fit = doPSO(fits);


		/**Step 3: Use information collected during PSO to evaluate factors*/

		int num_ignored = 0;

		for(int f = 0; f < config.numPieces; f++){
			for(int p = 0; p < sub_pops[f].size(); p++){
				MultiClassProgram mcp = sub_pops[f].get(p);

				if(fits[f][p].count != 0){
					mcp.trainFitnessMeasure.fitness = fits[f][p].sum/fits[f][p].count;
				}
				else{
					mcp.trainFitnessMeasure.fitness = avg_fit;
					num_ignored++;
				}
			}
		}
		System.out.println(num_ignored + " examples were unexecuted.");

	}

	private double doPSO(Fit[][] fits){

		double sum = 0;

		List<Particle> particles = new ArrayList<Particle>();

		int[][] g_best = new int[config.NUM_GROUPS][config.numPieces];
		double[] g_fit = new double[config.NUM_GROUPS];
		for(int i = 0; i < config.NUM_GROUPS; i++){
			g_fit[i] = Double.MAX_VALUE;
		}

		//initialize the particles
		for(int i = 0; i < config.numBluePrints; i++){
			int group = i % config.NUM_GROUPS;
			Particle particle = new Particle(programs.get(i).indices, group);//NOTE: NOTE A CLONE. CHANGES ACTUAL PROGRAM

			particle.randomVel();
			particle.fit = programs.get(i).trainFitnessMeasure.fitness;
			particle.localFit = particle.fit;

			//keep track of global best
			if(particle.fit < g_fit[particle.group]){
				g_best[particle.group] = particle.pos.clone();
				g_fit[particle.group] = particle.fit;
			}

			particles.add(particle);

		}

		double l_r, g_r;

		for(int iter = 0; iter < config.PSO_ITERATIONS; iter++){

			double avg_fit = 0;
			double avgVel = 0;

			//TESTING
			boolean t = false;
			
			//update particle velocity and position
			for(Particle particle: particles){


				//TESTING:
				if(TEST || t){
					System.out.println("PARTICLE:");
					System.out.println("Global Best:      " + Arrays.toString(g_best[particle.group]));
					System.out.println("Local Best:       " + Arrays.toString(particle.localBest));
					System.out.println("Current Position: " + Arrays.toString(particle.pos));
					System.out.println("Old Vel:          " + Arrays.toString(particle.vel));
				}

				//track avg vel
				for(int i = 0; i < config.numPieces; i++){
					avgVel += Math.abs(particle.vel[i]);
				}

				//Velocity
				l_r = Math.random();
				g_r = Math.random();

				int[] part2 = new int[config.numPieces], part3 = new int[config.numPieces];

				part2 = diff(particle.localBest, particle.pos);
				mult(part2, config.L_W*l_r);

				part3 = diff( g_best[particle.group], particle.pos);
				mult(part3, config.G_W*g_r);

				mult(particle.vel, config.V_W);
				add(particle.vel, part2);
				add(particle.vel, part3);
				
				//ADDING IN A SMALL MOVEMENT IN A RANDOM DIRECTION
				//for(int i = 0; i < particle.vel.length; i++){
				//	particle.vel[i] += (int)(Math.random()*4-2);
				//}

				//Position
				add(particle.pos, particle.vel);

				//TESTING
				if(TEST || t){
					System.out.println("New Vel:          " + Arrays.toString(particle.vel));
					t = false;
				}

			}

			//STEP 2.2.2: evaluate new particle positions

			//check the bounds
			for(BSProgram bsp: programs){
				checkBounds(bsp.indices);
			}

			//rematch indices
			for(BSProgram bsp: programs){
				bsp.matchIndices();
				bsp.setFitnessStatus(false);
			}

			//recompute fitness values
			for(BSProgram prog:programs){
				prog.zeroFitness();
			}
			sumAllBlueprints(TRAIN, config.numTrain);
			sumAllBlueprints(VALID, config.numValid);

			//update fitness values for localBest, globalBest etc
			for(int i = 0; i < programs.size(); i++){
				BSProgram bsp = programs.get(i);
				Particle p = particles.get(i);

				double fitness = bsp.trainFitnessMeasure.fitness;

				//store the fitness away for later processing
				for(int f = 0; f < config.numPieces; f++){
					fits[f][bsp.indices[f]].sum += fitness;
					fits[f][bsp.indices[f]].count++;
				}

				avg_fit += fitness;


				//update local best
				if(fitness < p.localFit){
					p.localBest = p.pos.clone();
					p.localFit = fitness;
				}

				//update global best
				if(fitness < g_fit[p.group]){
					g_best[p.group] = p.pos.clone();
					g_fit[p.group] = fitness;
				}

				//update the overall best PROGRAM here
				double temp = bsp.validationFitness();
				if(best == null || temp < best.validationFitness()){
					System.out.println("best fitness: " + temp);
					best = bsp.clone();
					if(config.printBest){
						System.out.println(best);
					}
				}
			}

			System.out.println("average: " + avg_fit/programs.size());
			System.out.println("avg Velocity: " + avgVel/particles.size());

			//keeps track of overall average fitness.
			sum += avg_fit/(programs.size()*config.PSO_ITERATIONS);
		}
		return sum;
	}

	private void sumAllBlueprints(int train /*zero or one*/, int numInstance){

		//train||test][which subpop][which program][which training example][which register]

		//for each program
		for(int p = 0; p < programs.size(); p++){

			BSProgram bsp = programs.get(p);
			RegisterCollection frv = train==TRAIN ? bsp.trainFinalRegisterValues : bsp.validFinalRegisterValues;

			//for each training example
			for(int t = 0; t < numInstance; t++){

				//zero registers
				frv.zeroRegisters();

				//sum the factors
				for(int f = 0; f < config.numPieces; f++){
					frv.add( cache[ train ][ f ][ bsp.indices[f] ][ t ] );
				}

				if(frv.largestRegisterIndex() != output[train][t]) {
					if(train == TRAIN)
						bsp.trainFitnessMeasure.fitness++;
					else
						bsp.validFitnessMeasure.fitness++;
				}

			}
			bsp.setFitnessStatus(true);
		}
	}

	public void checkBounds(int[] pos){
		for(int i = 0; i < pos.length; i++){
			if(pos[i] >= MAX){
				pos[i] = MAX-1;
			}
			else if(pos[i] < 0){
				pos[i] = 0;
			}
		}
	}

	class Particle{

		public int[] pos, localBest;
		public int[] vel;
		public double fit, localFit;
		
		public int group;

		public Particle(int[] pos, int group){
			this.pos = pos;
			this.vel = new int[pos.length];
			this.group = group;

			this.localBest = pos.clone();
		}

		public void randomVel(){
			for(int i = 0; i < vel.length; i++){
				vel[i] = (int)( ( Math.random()*MAX*2-MAX )*config.vel_scale );
			}
		}

	}



	public static int[] diff(int[] x, int[] y){
		int[] ret = new int[x.length];
		for(int i = 0; i < x.length; i++){
			ret[i] = x[i] - y[i];
		}
		return ret;
	}

	public static void add(int[] x, int[] y){
		for(int i = 0; i < x.length; i++){
			x[i] += y[i];
		}
	}

	public static void sub(int[] x, int[] y){
		for(int i = 0; i < x.length; i++){
			x[i] -= y[i];
		}
	}

	public static void mult(int[] x, double y){
		for(int i = 0; i < x.length; i++){
			x[i] = (int)(x[i] * y);
		}
	}

	private class Fit{
		public double sum = 0;
		public double count = 0;

		public void clear(){
			sum = 0;
			count = 0;
		}
	}

}














