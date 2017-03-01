package search;

import facebookNetwork.Node;

public class SearchUtilities {
	
	public static Node BreadthFirstGraphSearch(Node root) {
		BreadthFirstFrontier bff = new BreadthFirstFrontier();
		GraphSearch graph = new GraphSearch(bff);
		Node solution = graph.searchForSolution(root);
		bff.clearFrontier();
		return solution;
	}
	
}
