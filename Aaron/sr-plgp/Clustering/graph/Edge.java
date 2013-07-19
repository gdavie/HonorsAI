package graph;


public class Edge implements Comparable<Edge>{

	public Node n1;
	public Node n2;
	public double weight;
	
	public Edge(Node n1, Node n2, double weight){
		this.n1 = n1;
		this.n2 = n2;
		this.weight = weight;
	}

	public int compareTo(Edge e) {
		return new Double(weight).compareTo( new Double(e.weight) );
	}
}