package cluster;

import hash.VectorHash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mapping.Mapping;
import mapping.SpanningTreeMapping;
import core.Config;
import core.IPopulation;
import core.ManyPopulation;
import core.ManyProgram;
import core.MultiClassProgram;

public class Cluster<MPOP extends ManyPopulation<MPOP,MPR,MultiClassProgram>,MPR extends ManyProgram<MPR,MultiClassProgram>>{
	VectorHash<MultiClassProgram> vh = new VectorHash<MultiClassProgram>();
	Mapping mapping = new SpanningTreeMapping();
	Config c = Config.getInstance();
	
	public Cluster(){
		
	}
	
	/**NOTE THIS ONLY CLUSTERS PROGRAMS WHICH ARE IN ONE OR MORE BLUEPRINTS*/
	public void resize(MPOP mp, int newSize){
		
		List<MPR> programs = mp.getPrograms();
		IPopulation<?,MultiClassProgram>[] sub_pops = mp.getSub_Pops();
		
		//make the lists.
		List<MultiClassProgram>[] list = (List<MultiClassProgram>[])new List[c.numPieces];
		for(int i = 0; i < c.numPieces; i++){
			list[i] = new ArrayList<MultiClassProgram>();
		}

		for(MPR hp: programs){
			for(int i = 0; i < c.numPieces; i++){
				list[i].add(hp.parts()[i]);
			}
		}
		
		for(int i = 0; i < Config.getInstance().numPieces; i++){
			Map<MultiClassProgram, MultiClassProgram> map = mapping.map(list[i], newSize);
			for(MultiClassProgram key: map.keySet()){
				MultiClassProgram value = map.get(key);
				
				//make sure its in the subpops
				if(!sub_pops[i].getPrograms().contains(value))
					sub_pops[i].getPrograms().add(value);
			}
			
			//now replace each program with its elected representative
			for(MPR hp:programs){
				hp.parts[i] = map.get(hp.parts[i]);
				if(map.get(hp.parts()[i]) == null)
					System.out.println("NULL");
			}
		}
	}
	
	public void weightFitness(MPOP mp){
		
	}
	
	public Map<MultiClassProgram, List<MultiClassProgram>> reps(MPOP mp, int popNum, int newSize){
		
		List<MultiClassProgram> list = mp.getSub_Pops()[popNum].getPrograms();
		Map<MultiClassProgram, MultiClassProgram> map = mapping.map(list, newSize);
		
		//inverse
		Map<MultiClassProgram, List<MultiClassProgram>> inverse = new HashMap<MultiClassProgram, List<MultiClassProgram>>();
		
		for(MultiClassProgram mcp: map.keySet()){
			MultiClassProgram rep = map.get(mcp);
			if(!inverse.containsKey(rep)){
				inverse.put(rep, new ArrayList<MultiClassProgram>());
			}
			inverse.get(rep).add(mcp);
		}
		return inverse;
		
	}
	
	public List<MultiClassProgram> close(MPOP pop, MultiClassProgram prog, int listSize, int popNum, boolean useActive){
		List<MultiClassProgram> programs = pop.getSub_Pops()[popNum].getPrograms();
		
		//set up the tuple list
		List<Tuple> tuples = new ArrayList<Tuple>();
		int[] progHash = vh.hash(prog);
		for(int i = 0; i < programs.size(); i++){
			MultiClassProgram temp = programs.get(i);
			int dist = (int)SpanningTreeMapping.hammingDistance(progHash, vh.hash(temp));	
			
			//Sometimes we only want to consider active programs
			if(!useActive || temp.active)
				tuples.add(new Tuple(temp, dist));
		}
		
		Collections.sort(tuples);
		
		List<MultiClassProgram> result = new ArrayList<MultiClassProgram>();
		for(int i = 0; i < listSize; i++){
			result.add(tuples.get(i).mcp);
		}
		
		return result;
	}
	
	/**
	 * @author  AJ Scoble
	 */
	private class Tuple implements Comparable<Tuple>{
		
		public MultiClassProgram mcp;
		public int dist;
		
		public Tuple(MultiClassProgram mcp, int dist){
			this.mcp = mcp;
			this.dist = dist;
		}

		public int compareTo(Tuple o) {
			return ((Integer)dist).compareTo(o.dist);
		}
	}
	
} 
