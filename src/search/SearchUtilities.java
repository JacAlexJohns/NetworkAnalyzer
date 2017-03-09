package search;

import java.util.ArrayList;
import java.util.List;

import facebookNetwork.Edge;
import facebookNetwork.Node;

public class SearchUtilities {
	
	// A list to store all of the edges
	private static List<Edge> edges = new ArrayList<Edge>();
	// An edge to store the maximum edge
	private static Edge edge = new Edge(null, null);
	
	// This runs the Breadth-First Search
	public static Node BreadthFirstGraphSearch(Node root) {
		BreadthFirstFrontier bff = new BreadthFirstFrontier();
		GraphSearch graph = new GraphSearch(bff, edges);
		Node solution = graph.searchForSolution(root);
		edges = graph.getEdges();
		if (graph.getMaxEdge().getWeight() > edge.getWeight()) edge = graph.getMaxEdge();
		bff.clearFrontier();
		return solution;
	}
	
	// This runs the clustering search with the given cluster value
	public static void ClusteringSearch(Node root, int cluster) {
		BreadthFirstFrontier bff = new BreadthFirstFrontier();
		ClusteringSearch graph = new ClusteringSearch(bff);
		graph.searchForSolution(root, cluster);
		bff.clearFrontier();
	}
	
	// This returns the list of all edges
	public static List<Edge> GirvanNewmanEdges() {
		return edges;
	}
	
	// This return the maximum edge
	public static Edge getMaxEdge() {
		return edge;
	}
	
	// This clears all of the edges and the maximum edge
	public static void clearEdges() {
		edges = new ArrayList<Edge>();
		edge = new Edge(null, null);
	}
	
}
