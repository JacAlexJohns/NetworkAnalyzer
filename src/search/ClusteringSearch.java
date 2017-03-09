package search;

import java.util.HashSet;
import facebookNetwork.Node;

public class ClusteringSearch implements Search {
	Frontier frontier;
	HashSet<Node> explored;
	
	
	public ClusteringSearch(Frontier f) {
		this.frontier = f;
		this.explored = new HashSet<Node>();
	}
	
	// Do a Breath-First Search to set all nodes in the tree to the same cluster
	public Node searchForSolution(Node root, int cluster) {
		root.setClustering(cluster);
		frontier.addNode(root);
		Node node = new Node(null, null);
		while (!frontier.isEmpty()) {
			node = frontier.removeNode();
			for (Node newNode : node.getFriends()) {
				if (!explored.contains(newNode)) {
					newNode.setClustering(cluster);
					explored.add(newNode);
					frontier.addNode(newNode);
				}
			}
		}
		return node;
	}
	
	public Node searchForSolution(Node root) {
		return null;
	}

}
