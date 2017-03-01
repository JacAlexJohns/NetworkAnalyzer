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
	
	public String getName() {
		return this.name;
	}
	
	public String getHref() {
		return this.href;
	}
	
	public double getClustering() {
		return this.clustering;
	}
	
	public void setClustering(double newC) {
		this.clustering = newC;
	}
	
	public int getD() {
		return this.d;
	}

	public void setD(int d) {
		this.d = d;
	}
	
	public double getSigma() {
		return this.sigma;
	}

	public void setSigma(double sigma) {
		this.sigma = sigma;
	}
	
	public double getDelta() {
		return this.delta;
	}

	public void setDelta(double delta) {
		this.delta = delta;
	}
	
	public double getCB() {
		return this.CB;
	}

	public void setCB(double CB) {
		this.CB = CB;
	}
	
	public List<Node> getP() {
		return this.p;
	}
	
	public void addP(Node node) {
		this.p.add(node);
	}

	public void clearP() {
		this.p = new ArrayList<Node>();
	}
	
	public void setNumMutualFriends(int n) {
		this.numMutualFriends = n;
	}
	
	public int getNumMutualFriends() {
		return this.numMutualFriends;
	}
	
	public void addAFriend(Node n) {
		if (!(friends.contains(n))) friends.add(n);
	}
	
	public void addFriends(List<Node> ns) {
		for (Node friend : ns) addAFriend(friend);
	}
	
	public List<Node> getFriends() {
		return this.friends;
	}
	
	public List<Node> getFriendsAndClear() {
		List<Node> friends = this.getFriends();
		this.friends = new ArrayList<Node>();
		return friends;
	}
	
	public void print() {
		System.out.println(this.name + ": " + this.friends.size() + " edges");
		for (Node friend : this.friends) {
			System.out.print(friend.name + ", ");
		} System.out.println();
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
