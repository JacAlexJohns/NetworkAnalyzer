<b>Network Analyzer</b>

This project was meant to parse Facebook friends for a logged-in user and build a network based on mutual friends. Selenium WebDriver is used to collect the Facebook network, which is collected at two depth-levels from the logged-in user and collects n friends at each level (where n is an adjustable number given by the programmer). Once the network is build, a series of network analysis tools are then used on the network.

<b>Analysis Tools</b>

Once the network is build, a series of network analysis tools are used:
- Number of Clusters: This analyzes the number of clusters that the initial network is split into. Based on how the Facebook data is crawled, this should initially only be one.
- Diameter: This tells the maximum length from one node to another in the network, or in other words the length between the farthest apart nodes in the network.
- Density: This gives the number of edges in the network divided by the number of edges if the network were fully connected (every node has an edge between every other node).
- Average Path Length: The average length between any every pair of nodes in the network.
- Clustering Coefficient: The percentage of a node's friends that are friends with each other over the possible interconnectedness of said friends. Put more simply, if a node has n friends, then the clustering coefficient is the number of edges between all of those friends divided by (n)*(n-1)/2, the possible interconnectedness of the n friends.
- Betweenness Centrality: A numeric representation of the importance of a node in a network. Calculated by finding the shortest paths to a node given a start node, calculating betweenness values for all nodes except the root node, and then accumulating those values for each node over all possible root nodes.
- Girvan-Newman Algorithm: A method for calculating betweenness values for nodes as well as edges in a graph. Can then be used to remove edges with maximum betweenness, recalculate, and continue to remove until the network is broken into multiple clusters.

<b>Classes</b>

- Search Classes: The search package contains classes for running a traditional BreadthFirstSearch on the network as well as modified BreadthFirstSearches which also calculate values for the network analysis tools, such as the Girvan-Newman Algorithm.
- Node: This class allows the NetworkStructure class to store the required data for each friend in the network. The data is then used by the analysis tools to get the desired results.
- Edge: This class is used during the Girvan-Newman Algorithm to check for edge betweenness centrality, and is then used for deletion in the Girvan-Newman Algorithm to try and split the network into 6 clusters.
- NetworkStructure: This class uses all of the other classes in the program to crawl Facebook data, store it in the edges and nodes, and run the analysis tools on the network.
