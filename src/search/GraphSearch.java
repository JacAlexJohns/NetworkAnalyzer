package search;

import java.util.HashSet;
import java.util.Stack;

import facebookNetwork.Node;

public class GraphSearch implements Search {
	Frontier frontier;
	HashSet<Node> explored;
	Stack<Node> S;
	Node s;
	
	
	public GraphSearch(Frontier f) {
		this.frontier = f;
		this.explored = new HashSet<Node>();
		this.S = new Stack<Node>();
		s = new Node(null, null);
	}
	
	public Node searchForSolution(Node root) {
		root.setSigma(1); root.setD(0);
		frontier.addNode(root);
		s = root;
		Node node = new Node(null, null);
		while (!frontier.isEmpty()) {
			node = frontier.removeNode();
			S.push(node);
			if (explored.add(node)) {
				for (Node newNode : node.getFriends()) {
					if (!explored.contains(newNode)) {
						if (newNode.getD() < 0) {
							newNode.setD(node.getD() + 1);
							frontier.addNode(newNode);
						} if (newNode.getD() == (node.getD()+1)) {
							newNode.setSigma(newNode.getSigma() + node.getSigma());
							newNode.addP(node);
						}
					}
				}
			}
		}
		calculateDeltas();
		return node;
	}
	
	public void calculateDeltas() {
		while (!(S.isEmpty())) {
			Node w = S.pop();
			for (Node v : w.getP()) { v.setDelta(v.getDelta() + ((v.getSigma()/w.getSigma()) * (1 + w.getDelta()))); } 
			if (!(w.equals(s))) { w.setCB(w.getCB() + w.getDelta()); }
		}
	}
}
