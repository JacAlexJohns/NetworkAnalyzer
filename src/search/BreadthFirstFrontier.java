package search;

import java.util.LinkedList;
import java.util.Queue;

import facebookNetwork.Node;

public class BreadthFirstFrontier implements Frontier {
	// First-in-first-out
	Queue<Node> frontier;
	
	public BreadthFirstFrontier() {
		frontier = new LinkedList<Node>();
	}
	
	public void addNode(Node node) {
		frontier.add(node);
	}
	public void clearFrontier() {
		frontier.clear();
	}
	public boolean isEmpty() {
		return frontier.isEmpty();
	}
	public Node removeNode() {
		if (!frontier.isEmpty()) {
			return frontier.remove();
		}
		return null;
	}
}
