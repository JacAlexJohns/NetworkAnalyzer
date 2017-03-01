package search;

import facebookNetwork.Node;

public interface Frontier {
	void addNode(Node node);
	void clearFrontier();
	boolean isEmpty();
	Node removeNode();
}
