package local;

import graph.Edge;
import graph.Node;
import hash.VectorHash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mapping.SpanningTreeMapping;
import core.MultiClassProgram;

public class Localiser {
	
	private static VectorHash<MultiClassProgram> vh = new VectorHash<MultiClassProgram>();
	
	public static List<MultiClassProgram> order(List<MultiClassProgram> input){
		boolean TEST=false;
		Map<MultiClassProgram, int[]> vecMap = new HashMap<MultiClassProgram, int[]>();
		
		/**pre-compute all vectors and add to the map*/
		for(MultiClassProgram mcp : input){
			int[] vector = vh.hash(mcp);
			vecMap.put(mcp, vector);
		}
		
		/**calculate all distances and make a graph*/
		List<Node> nodes = new ArrayList<Node>();
		
		Map<MultiClassProgram, Node> nodeMap = new HashMap<MultiClassProgram, Node>();
		
		for(MultiClassProgram mcp : input){
			Node node = new Node(mcp);
			nodes.add(node);
			nodeMap.put(mcp, node);
		}
		
		List<Edge> edges = new ArrayList<Edge>();
		MultiClassProgram mcp1, mcp2;
		
		for(int i = 0; i < input.size(); i++){
			mcp1 = input.get(i);
			Node node1 = nodeMap.get(mcp1);
			for(int j = 0; j < input.size(); j++){
				
				//no loops in graph
				if(i == j){
					continue;
				}
				
				mcp2 = input.get(j);
				Node node2 = nodeMap.get(mcp2);
				
				double distance = SpanningTreeMapping.hammingDistance(vecMap.get(mcp1), vecMap.get(mcp2));
				
				Edge e1 = new Edge(node1, node2, distance);
				node1.addEdge(e1);
				edges.add(e1);

				Edge e2 = new Edge(node2, node1, distance);
				node2.addEdge(e2);
				edges.add(e2);
			}
		}
		
		for(Node n: nodes){
			Collections.sort(n.edges);
		}
		
		/**use the graph to find a better ordering. Will be suboptimal but hopefully
		 * will be at least close. ATM using nearest neighbour. Might use twice 
		 * around the tree*/
		
		//USE NEAREST NEIGHBOR ORDERING
		List<MultiClassProgram> ret = nearestNeighbor(nodes);
		
		//print savings
		if(TEST)System.out.println("start:" + localDist(input));
		if(TEST)System.out.println("finish:" + localDist(ret));
		
		return ret;
	}
	
	/**
	 * Finds a semi-optimal ordering of the programs according to the nearest neighbor heuristic.
	 * 
	 * @param a list of the nodes to order
	 * @return the same list of objects, but ordered using the nearest neighbour heuristic
	 */
	public static List<MultiClassProgram> nearestNeighbor(List<Node> nodes){

		List<MultiClassProgram> ret = new ArrayList<MultiClassProgram>();
		
		Node n = nodes.get(0);
		ret.add(n.value);
		
		//find a path through all nodes using nearest neighbour selection
		while(ret.size() < nodes.size()){
			
			//find the nearest unvisited neighbour
			int count = 0;
			Edge e = n.edges.get(count);
			while(ret.contains(e.n2.value)){
				count++;
				e = n.edges.get(count);
			}
			
			//add this neighbour to list and repeat
			ret.add(e.n2.value);
			n = e.n2;
		}
		
		return ret;
	}
	
	
	public static int localDist(List<MultiClassProgram> input){
		int dist = 0;
		for(int i = 0; i < input.size()-1; i++){
			dist += SpanningTreeMapping.hammingDistance(input.get(i), input.get(i+1));
		}
		return dist;
	}

}
