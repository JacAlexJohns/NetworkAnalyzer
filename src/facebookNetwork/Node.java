package facebookNetwork;

import java.util.ArrayList;
import java.util.List;

public class Node {
	
	private String name;
	private String href;
	private int numMutualFriends;
	private List<Node> friends;
	private double clustering;
	private int d;
	private double sigma;
	private double delta;
	private double CB;
	List<Node> p;

	// Node creator : takes only a string name and an href
	public Node(String name, String href) {
		this.name = name;
		this.href = href;
		friends = new ArrayList<Node>();
		clustering = -1;
		d = -1;
		sigma = 0;
		delta = 0;
		CB = 0;
		p = new ArrayList<Node>();
	}
	
	// Get name
	public String getName() {
		return this.name;
	}
	
	// Get href
	public String getHref() {
		return this.href;
	}
	
	// Get the clustering coefficient/value
	public double getClustering() {
		return this.clustering;
	}
	
	// Set the clustering coefficient/value
	public void setClustering(double newC) {
		this.clustering = newC;
	}
	
	// Get the depth
	public int getD() {
		return this.d;
	}

	// Set the depth
	public void setD(int d) {
		this.d = d;
	}
	
	// Get the sigma value
	public double getSigma() {
		return this.sigma;
	}

	// Set the sigma value
	public void setSigma(double sigma) {
		this.sigma = sigma;
	}
	
	// Get the delta value
	public double getDelta() {
		return this.delta;
	}

	// Set the delta value
	public void setDelta(double delta) {
		this.delta = delta;
	}
	
	// Get the CB value (node centrality)
	public double getCB() {
		return this.CB;
	}

	// Set the CB value (node centrality)
	public void setCB(double CB) {
		this.CB = CB;
	}
	
	// Get the list of nodes, P
	public List<Node> getP() {
		return this.p;
	}
	
	// Add a node to the list of nodes, P
	public void addP(Node node) {
		this.p.add(node);
	}

	// Clear the list of nodes, P
	public void clearP() {
		this.p = new ArrayList<Node>();
	}
	
	// Get the number of mutual friends
	public int getNumMutualFriends() {
		return this.numMutualFriends;
	}
		
	// Set the number of mutual friends
	public void setNumMutualFriends(int n) {
		this.numMutualFriends = n;
	}
	
	// Add a node to the friends list
	public void addAFriend(Node n) {
		if (!(friends.contains(n))) friends.add(n);
	}
	
	// Remove a node from the friends list
	public void removeAFriend(Node n) {
		if ((friends.contains(n))) friends.remove(n);
	}
	
	// Add an entire list of friends to the friends list
	public void addFriends(List<Node> ns) {
		for (Node friend : ns) addAFriend(friend);
	}
	
	// Get the friends list
	public List<Node> getFriends() {
		return this.friends;
	}
	
	// Get the friends list and clear the friends list
	public List<Node> getFriendsAndClear() {
		List<Node> friends = this.getFriends();
		this.friends = new ArrayList<Node>();
		return friends;
	}
	
	// Print certain aspects of the node
	public void print() {
		System.out.println(this.name + ": " + this.friends.size() + " edges");
		System.out.println("D: " + this.d + " | CB: " + this.CB + " | Sigma: " + this.sigma + " | Delta: " + this.delta);
	}
	
	/*
	 * Equality and hash code only checked for name and href
	 */
	public boolean equals(Object that) {
		if (this == that) return true;
		if (!(this instanceof Node)) return false;
		Node aThat = (Node) that;
		return
			((this.name.equals(aThat.name)) && (this.href.equals(aThat.href)));
	}
	public int hashCode() {
		return this.name.hashCode() + this.href.hashCode();
	}

}
