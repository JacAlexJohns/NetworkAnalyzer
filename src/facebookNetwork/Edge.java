package facebookNetwork;

public class Edge {

	private Node node1;
	private Node node2;
	private double weight;
	
	// Initializes and edge with a from node and a two node, weight initially 0
	public Edge(Node node1, Node node2) {
		this.node1 = node1;
		this.node2 = node2;
		this.weight = 0;
	}

	// Get the from node
	public Node getFrom() {
		return node1;
	}

	// Get the to node
	public Node getTo() {
		return node2;
	}

	// Get the edge weight
	public double getWeight() {
		return weight;
	}

	// Set the edge weight
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	// Print the aspects of the node
	public void print() {
		System.out.println(this.getFrom().getName() + " - " + this.getTo().getName() + " : " + this.getWeight());
	}
	
	/*
	 * Equality and hash code checked for node1 and node2, regardless of order
	 */
	public boolean equals(Object that) {
		if (this == that) return true;
		if (!(this instanceof Edge)) return false;
		Edge aThat = (Edge) that;
		return
			((this.node1.equals(aThat.node1)) && (this.node2.equals(aThat.node2)) ||
			 (this.node1.equals(aThat.node2)) && (this.node2.equals(aThat.node1)));
	}
	public int hashCode() {
		return this.node1.hashCode() + this.node2.hashCode();
	}
	
}
