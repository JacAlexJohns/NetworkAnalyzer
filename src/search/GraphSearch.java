package search;

import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import facebookNetwork.Edge;
import facebookNetwork.Node;

public class GraphSearch implements Search {
	Frontier frontier;
	HashSet<Node> explored;
	Stack<Node> S;
	Node s;
	List<Edge> edges;
	Edge edge;
	
	
	public GraphSearch(Frontier f, List<Edge> edges) {
		this.frontier = f;
		this.explored = new HashSet<Node>();
		this.S = new Stack<Node>();
		s = new Node(null, null);
		this.edges = edges;
		edge = new Edge(null, null);
	}
	
	/*
	 * Run a Breath-First Search to find Sigma, D, P, and S values
	 * which will then be used to determine the edge and node betweenness values,
	 * as well as the average path length and diameter of the network
	 */
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
	
	/*
	 * This takes the values set in the Breadth-First Search and uses
	 * them to determine the edge and node betweenness values
	 */
	public void calculateDeltas() {
		while (!(S.isEmpty())) {
			Node w = S.pop();
			for (Node v : w.getP()) {
				// This is the increase value for the edges and nodes
				double increase = (v.getSigma()/w.getSigma()) * (1 + w.getDelta());
				v.setDelta(v.getDelta() + increase); 
				Edge e = new Edge(v, w);
				// If the edge already exists, add the increase to its current weight
				if (edges.contains(e)) {
					e = edges.get(edges.indexOf(e));
					e.setWeight(e.getWeight() + increase);
				}
				// Otherwise just set its weight to the increase
				else {
					e.setWeight(increase);
					edges.add(e);
				} 
				// This keeps track of the maximum edge
				if (e.getWeight() >= edge.getWeight()) {
					edge = e;
				}
			} 
			// This accumulates CB values for the nodes
			if (!(w.equals(s))) { w.setCB(w.getCB() + w.getDelta()); }
		}
	}
	
	// This returns the list of all edges created
	public List<Edge> getEdges() {
		return this.edges;
	}
	
	// This returns the maximum edge
	public Edge getMaxEdge() {
		return this.edge;
	}
}
