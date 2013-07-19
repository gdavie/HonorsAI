package mapping;

import graph.Edge;
import graph.Node;
import hash.VectorHash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unionFind.UnionFind;
import core.MultiClassProgram;

public class SpanningTreeMapping extends Mapping{

	public static VectorHash<MultiClassProgram> vHash = new VectorHash<MultiClassProgram>();
	
	@Override
	public Map<MultiClassProgram, MultiClassProgram> map(List<MultiClassProgram> list, int numClusters) {
		
		//Declare the graph
		List<Node> nodes = new ArrayList<Node>();
		List<Edge> edges = new ArrayList<Edge>();
		
		//set up the union find. this will be the clustering we want
		UnionFind<Node> uf = new UnionFind<Node>();
		
		List<Edge> st = part1(list, nodes, edges, uf);
		
		//remove the appropriate number of edges
		for(int i = 0; i < numClusters-1; i++){
			st.remove(st.size()-1);
		}
		
		return part2(nodes, uf, numClusters, st);
		
	}

	public Map<MultiClassProgram, MultiClassProgram> mapDist(List<MultiClassProgram> list, double maxDist) {
		
		//Declare the graph
		List<Node> nodes = new ArrayList<Node>();
		List<Edge> edges = new ArrayList<Edge>();
		
		//set up the union find. this will be the clustering we want
		UnionFind<Node> uf = new UnionFind<Node>();
		
		List<Edge> st = part1(list, nodes, edges, uf);
		
		//remove the appropriate number of edges
		while(st.size() > 0){
			Edge e = st.get(st.size()-1);
			if(e.weight < maxDist)
				st.remove(st.size()-1);
			else
				break;
		}
		
		int numClusters = list.size() - (st.size()-1);
		
		return part2(nodes, uf, numClusters, st);
		
	}
	
	
	public List<Edge> part1(List<MultiClassProgram> list, List<Node> nodes, List<Edge> edges, UnionFind<Node> uf){
		List<MultiClassProgram> temp2_list = new ArrayList<MultiClassProgram>();
		for(MultiClassProgram mcp : list){
			if(!temp2_list.contains(mcp)){
				temp2_list.add(mcp);
			}
		}	
		
		//make the COMPLETE graph
		for(int i = 0; i < list.size(); i++){
			MultiClassProgram mcp = list.get(i);
			nodes.add(new Node(mcp));
		}
		
		//precompute hashes
		Map<Node, int[]> hashes = new HashMap<Node, int[]>();
		for(Node n : nodes){
			hashes.put(n, vHash.hash(n.value));
			
			//testing
			/*
			for(int i = 0; i < hashes.get(n).length; i++){
				System.out.printf("%3d, ", hashes.get(n)[i]);
			}
			System.out.println("");
			*/
		}
		
		//find the hamming distances
		for(Node n1: nodes){
			for(Node n2: nodes){
				
				double distance = hammingDistance(hashes.get(n1), hashes.get(n2));
				
				Edge e = new Edge(n1, n2, distance);
				edges.add(e);
			}
		}
		

		
		for(Node n: nodes){
			uf.add(n);
		}
		
		//find the spanning tree
		return kruskals(nodes, edges, uf);
		
	}
	
	
	public Map<MultiClassProgram, MultiClassProgram> part2(List<Node> nodes, UnionFind<Node> uf, int numClusters, List<Edge> st){
		//reset the union-find without removed edges
		uf.reset();
		for(Node n: nodes){
			uf.add(n);
		}
		for(Edge e: st){
			uf.connect(e.n1, e.n2);
		}
		
		//form a list of sets (better representation 
		List<Node>[] sets = (List<Node>[])new List[numClusters];
		for(int i = 0; i < numClusters; i++){
			sets[i] = new ArrayList<Node>();
		}
		Map<Node, List<Node>> setMap = new HashMap<Node, List<Node>>();
		
		int i = 0;
		
		for(Node n: nodes){
			Node top = uf.find(n);
			if(setMap.containsKey(top))
				setMap.get(top).add(n);
			else{
				sets[i].add(n);
				setMap.put(top, sets[i++]);
			}
		}
		
		
		for(i = 0; i < sets.length; i++){
			System.out.print(sets[i].size() + ", ");
		}
		System.out.println("");
		
		
		//now find representatives
		Map<MultiClassProgram, MultiClassProgram> reps = new HashMap<MultiClassProgram, MultiClassProgram>();
		
		for(i = 0; i < sets.length; i++){
			Node best = null;
			double bestDist = Double.MAX_VALUE;
			
			for(int j = 0; j < sets[i].size(); j++){
				Node candidate = sets[i].get(j);
				int[] h1 = vHash.hash(candidate.value);
				double candidateDist = 0;
				
				for(int y = 0; y < sets[i].size(); y++){
					Node other = sets[i].get(y);
					int[] h2 = vHash.hash(other.value);
					candidateDist += hammingDistance(h1, h2);
				}
				
				if(candidateDist < bestDist){
					best = candidate;
					bestDist = candidateDist;
				}
			}
			for(int y = 0; y < sets[i].size(); y++){
				Node other = sets[i].get(y);
				reps.put(other.value, best.value);
			}
		}
		
		List<MultiClassProgram> temp_list = new ArrayList<MultiClassProgram>();
		for(MultiClassProgram n: reps.keySet()){
			if(!temp_list.contains(reps.get(n))){
				temp_list.add(reps.get(n));
			}
		}

		return reps;
	}
	
	
	private List<Edge> kruskals(List<Node> nodes, List<Edge> edges, UnionFind<Node> uf){
		List<Edge> spanningTree = new ArrayList<Edge>();
		
		
		Collections.sort(edges);
		int i = 0;
		while(spanningTree.size() < nodes.size()-1){
			Edge e = edges.get(i);
			if(!uf.connected(e.n1, e.n2)){
				spanningTree.add(e);
				uf.connect(e.n1, e.n2);
			}
			i++;
		}
		
		return spanningTree;
	}
	
	/**v1 and v2 must be of equal length. Otherwise we are faced with the problem of finding
	 * an optimal arrangement THEN finding hamming distance*/
	public static double hammingDistance(int[] v1, int[] v2){
		int distance = 0;
		for(int i = 0; i < v1.length; i++){
			if(v1[i] != v2[i])
				distance++;
		}
		return distance;
	}
	
	public static double hammingDistance(MultiClassProgram mcp1, MultiClassProgram mcp2){
		int[] h1 = vHash.hash(mcp1);
		int[] h2 = vHash.hash(mcp2);
		return hammingDistance(h1, h2);
	}
	
}
