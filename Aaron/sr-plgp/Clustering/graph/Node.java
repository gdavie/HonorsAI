package graph;

import java.util.ArrayList;
import java.util.List;

import core.MultiClassProgram;

public class Node{
	
	public MultiClassProgram value;

	public List<Edge> edges = new ArrayList<Edge>();
	
	public Node(MultiClassProgram value){
		this.value = value;
	}
	
	public boolean equals(Object o){
		Node n = (Node)o;
		return value.equals(n.value);
	}
	
	public void addEdge(Edge e){
		edges.add(e);
	}
	
	public List<Edge> getEdges(){
		return edges;
	}
}