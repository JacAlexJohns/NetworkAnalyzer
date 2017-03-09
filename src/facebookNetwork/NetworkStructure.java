package facebookNetwork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import search.SearchUtilities;

import com.google.gson.JsonIOException;

public class NetworkStructure {
	
	public static void main(String[] args) throws InterruptedException, JsonIOException, IOException {
		/*
		 * This runs the facebook crawl with the given username, password.
		 * It takes in an integer (multiple of 20) which is used to determine how
		 * many friends should be collected at each level.
		 */
		Node me = crawl(40);
		
		// This runs the program on test data, to verify that all algorithms are correct
		//Node me = testSetup();
		
		// This turns the node tree, with me as the root, into a list of all of the nodes in the tree
		List<Node> allNodes = getAllNodes(me);
		
		int nodes = getTotalNumberOfNodes(allNodes);
		int es = getTotalNumberOfEdges(allNodes);
		double[] diameterAndPathLength = getDiameterAndPath(allNodes);
		int diameter = (int) diameterAndPathLength[0];
		double pathLength = diameterAndPathLength[1];
		double density = getDensity(allNodes);
		List<List<Node>> clusters = getClusters(allNodes);
		List<Node> topFiveClustering = getTopFive(clusteringCoefficient(allNodes), 'c');
		List<Node> topFiveBetweenness = getTopFive(allNodes, 'b');
		
		System.out.println(nodes + " nodes, " + es + " edges, undirected");
		System.out.println();
		System.out.println("Number of clusters = " + clusters.size());
		System.out.println("Diameter: " + diameter);
		System.out.println("Density: " + density);
		System.out.println("Average Path Length: " + pathLength);
		System.out.println();
		System.out.println("Top 5 clustering coefficients:");
		int a = 0;
		for (Node node : topFiveClustering) { 
			System.out.println(++a + " ('" + node.getName() + ", " + node.getClustering() + ")");
		} 
		System.out.println("\nTop 5 friends:");
		a = 0;
		for (Node node : topFiveBetweenness) { 
			System.out.println(++a + " ('" + node.getName() + ", " + node.getCB()/2 + ")");
		}
		
		Edge max = SearchUtilities.getMaxEdge(); // Retrieves the edge from the getDiameterAndPath run
		System.out.println("\nGirvan-Newman algorithm");
		girvanNewman(allNodes, max);
	}
	
	/*
	 * This is a test setup, with only a few, controlled nodes and edges
	 */
	public static Node testSetup() {
		Node A = new Node("A", "A");
		Node B = new Node("B", "B");
		Node C = new Node("C", "C");
		Node D = new Node("D", "D");
		Node E = new Node("E", "E");
		Node F = new Node("F", "F");
		Node G = new Node("G", "G");
		Node H = new Node("H", "H");
		
		A.addAFriend(B); A.addAFriend(C);
		B.addAFriend(A); B.addAFriend(C);
		C.addAFriend(A); C.addAFriend(B); C.addAFriend(D); C.addAFriend(G);
		D.addAFriend(C); D.addAFriend(E); D.addAFriend(F);
		E.addAFriend(D);
		F.addAFriend(G); F.addAFriend(H); F.addAFriend(D);
		G.addAFriend(C); G.addAFriend(F);
		H.addAFriend(F);
		
		return A;
	}
	
	/*
	 * This is the code to crawl facebook for the friendship data
	 * which will make up the network
	 */
	public static Node crawl(int width) throws InterruptedException {
		// Driver setup
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--disable-notifications");
		WebDriver driver = new ChromeDriver(options);
		driver.get("http://www.facebook.com");
		
		// Login as user
		WebElement email = driver.findElement(By.id("email"));
		WebElement password = driver.findElement(By.id("pass"));
		email.sendKeys("user_name");
		Thread.sleep(5000);
		password.sendKeys("password");
		Thread.sleep(5000);
		WebElement login = driver.findElement(By.id("loginbutton"));
		login.click();
		Thread.sleep(5000);
		
		// Go to friends page
		driver.get("https://www.facebook.com/person/friends?lst=100001353948322%3A100001353948322%3A1486678307&source_ref=pb_friends_tl");
		Thread.sleep(2500);
		
		// Scroll down a certain amount
		JavascriptExecutor jsx = (JavascriptExecutor)driver;
		// The number of scrolls will be (width / 20) * 2
		for (int i = 0; i < (width / 20)*2; i++) {
			jsx.executeScript("window.scrollBy(0,1000)", "");
			Thread.sleep(250);
		}
		
		// Set the root node to the logged in user
		Node me = new Node("person", "https://www.facebook.com/person/friends_mutual");
		// Empty list to store first-level friends
		List<Node> firstLevel = new ArrayList<Node>();
		List<WebElement> friends = driver.findElements(By.xpath("//div[@class='fsl fwb fcb']/a"));
		// Go through and create a list of all of the friends
		for (WebElement friend : friends) {
			String name = friend.getText();
			String[] url = friend.getAttribute("href").split("\\?");
			String userTag = url[0].split("/")[3];
			// If the userTag = "profile.php" then you cannot access the person's profile
			if (!userTag.equals("profile.php")) {
				String href = url[0] + "/friends_mutual";
				Node person = new Node(name, href);
				firstLevel.add(person);
			}
		}
		
		/*
		 * Go back through the first-level keep 'width' number of friends
		 * who have at least 'width' number of mutual friends
		 */
		int counter = 0;
		for (Node someone : firstLevel) {
			if (counter >= width) break;
			driver.get(someone.getHref());
			Thread.sleep(5000);
			List<WebElement> friendZones = driver.findElements(By.xpath("//span[@class='_3d0']"));
			// This checks if their privacy does not allow you to see any of their friends
			if (friendZones.equals(null)) continue;
			WebElement friendZone;
			// This checks if they only allow you to see mutual friends
			if (friendZones.size() <= 1) { friendZone = friendZones.get(0); } 
			else { friendZone = friendZones.get(1); } 
			int mutualFriends = Integer.parseInt(friendZone.getText());
			// If they pass the mutual friends test, they are added to the network
			if (!(mutualFriends < width)) {
				someone.addAFriend(me);
				me.addAFriend(someone);
				counter++;
			}
		}
		
		// Go through each person in the first level and get 'width' number of their friends
		firstLevel = me.getFriendsAndClear();
		me.addFriends(firstLevel);
		for (Node friend: firstLevel) {
			driver.get(friend.getHref());
			Thread.sleep(500);
			// The number of scrolls will be (width / 20) * 2
			for (int i = 0; i < (width/20)*2; i++) {
				jsx.executeScript("window.scrollBy(0,1000)", "");
				Thread.sleep(250);
			}
			Thread.sleep(2500);
			friends = driver.findElements(By.xpath("//div[@class='fsl fwb fcb']/a"));
			// Add 'width' number of friends to the first-level node
			counter = 0;
			for (WebElement friend2 : friends) {
				if (counter >= width) break;
				String name = friend2.getText();
				String[] url = friend2.getAttribute("href").split("\\?");
				String userTag = url[0].split("/")[3];
				// If the userTag = "profile.php" then you cannot access the person's profile
				if (!userTag.equals("profile.php")) {
					String href = url[0] + "/friends_mutual";
					// Create a new node and check if the new node is already in the network
					Node person = new Node(name, href);
					// If it is, then retrieve it and add an edge from the current node to it
					if (me.getFriends().contains(person)) {
						Node oldPerson = me.getFriends().get(me.getFriends().indexOf(person));
						oldPerson.addAFriend(me); oldPerson.addAFriend(friend);
						me.addAFriend(oldPerson); friend.addAFriend(oldPerson);
					} 
					// Otherwise add all necessary edges for the node
					else {
						person.addAFriend(me); person.addAFriend(friend);
						me.addAFriend(person); friend.addAFriend(person);
					} counter++;
				}
			} Thread.sleep(2500);
		}
		
		driver.close();
		
		return me;
	}
	
	/*
	 * Takes in a root node and retrieves all of the nodes in the node tree
	 */
	public static List<Node> getAllNodes(Node root) {
		Stack<Node> stack = new Stack<Node>();
		List<Node> allFriends = new ArrayList<Node>();
		stack.push(root); allFriends.add(root);
		while (!(stack.isEmpty())) {
			Node newNode = stack.pop();
			for (Node person : newNode.getFriends()) {
				if (!(allFriends.contains(person)))	{
					allFriends.add(person);
					stack.push(person);
				}
			}
		} return allFriends;
	}
	
	// Returns the total number of nodes in the network
	public static int getTotalNumberOfNodes(List<Node> allNodes) {
		return allNodes.size();
	}
	
	// Returns the total number of edges in the network
	public static int getTotalNumberOfEdges(List<Node> allNodes) {
		int edges = 0;
		for (Node person : allNodes) {
			edges += person.getFriends().size();
		} return edges/2;
	}
	
	// Get the total number of possible edges in the network, given the number of nodes
	public static int allPossibleEdges(List<Node> allNodes) {
		int numNodes = getTotalNumberOfNodes(allNodes);
		int possibleNumbers = (numNodes * (numNodes-1))/2;
		return possibleNumbers;
	}
	
	/*
	 * This method gets the diameter and path-length of the network,
	 * as well as calculating the betweenness-centrality for the nodes,
	 * and getting the first edge to be deleted in the girvan-newman algorithm
	 */
	public static double[] getDiameterAndPath(List<Node> allNodes) {
		double[] depthAndPath = new double[2]; double maxDepth = 0; double pathLength = 0;
		for (Node person : allNodes) {
			// Breadth-First Search
			Node node = SearchUtilities.BreadthFirstGraphSearch(person);
			// Find the max of the network
			maxDepth = Math.max(maxDepth, node.getD());
			// Accumulate the path-length
			pathLength += clearDValues(allNodes);
			// Clearing all of the values for the next breadth-first search
			clearSigmaValues(allNodes); clearDeltaValues(allNodes); clearPValues(allNodes);
		} 
		// Calculate the average path length
		double averagePathLength = pathLength/(allNodes.size()*(allNodes.size()-1));
		// Set the depth and path values into an array to be returned
		depthAndPath[0] = maxDepth; depthAndPath[1] = averagePathLength;
		return depthAndPath;
	}
	
	// Clears the node D value for all of the nodes in the list
	// Also accumulates and returns for the average path length
	public static int clearDValues(List<Node> allNodes) {
		int acc = 0;
		for (Node person : allNodes) {
			acc += person.getD();
			person.setD(-1);
		} return acc;
	}
	
	// Clears the node CB value for all of the nodes in the list
	public static void clearCBValues(List<Node> allNodes) {
		for (Node person : allNodes) { System.out.println(person.getName() + " " + (person.getCB()));person.setCB(0); }
	}
	
	// Clears the node Sigma value for all of the nodes in the list
	public static void clearSigmaValues(List<Node> allNodes) {
		for (Node person : allNodes) { person.setSigma(0); }
	}
	
	// Clears the node Delta value for all of the nodes in the list
	public static void clearDeltaValues(List<Node> allNodes) {
		for (Node person : allNodes) { person.setDelta(0); }
	}
	
	// Clears the node P values for all of the nodes in the list
	public static void clearPValues(List<Node> allNodes) {
		for (Node person : allNodes) { person.clearP(); }
	}
	
	// Calculates the density of the network
	public static double getDensity(List<Node> allNodes) {
		double density = (double)getTotalNumberOfEdges(allNodes)/allPossibleEdges(allNodes);
		return density;
	}
	
	/*
	 *  Calculates the clustering coefficient for each node in the list of nodes
	 *  This is done for each node, by checking the percentage of that node's friends
	 *  that are friends with each other with the possible interconnectedness of said friends
	 */
	public static List<Node> clusteringCoefficient(List<Node> allNodes) {
		clearClusteringValues(allNodes);
		List<Node> explored = new ArrayList<Node>();
		// Each node's clustering coefficient is calculated
		for (Node person : allNodes) {
			int acc = 0; 
			int numFriends = person.getFriends().size();
			// For each friend, add that person's friends to the explored set
			for (Node friend : person.getFriends()) {
				for (Node friendsOfFriend : friend.getFriends()) {
					// If the explored set contains the friend in question, increase the accumulator
					if (explored.contains(friendsOfFriend)) acc ++;
				} explored.add(friend);
			} 
			double clustering = 0;
			// Calculate the number of possible connections
			int possible = (numFriends * (numFriends - 1));
			// Clustering value is 2 times the accumulator divided by the possible combinations
			if (possible != 0) clustering = (double) (2 * acc) / possible;
			person.setClustering(clustering);
			explored = new ArrayList<Node>();
		} return allNodes;
	}
	
	/*
	 * This method implements the Girvan-Newman algorithm for removal
	 * of the edge with the greatest betweenness value. The algorithm
	 * stops when enough edges have been removed to result in 6 separate
	 * clusters.
	 */
	public static void girvanNewman(List<Node> allNodes, Edge e) {
		
		// Check for number of clusters and sort by cluster
		List<List<Node>> clusters = getClusters(allNodes);
		System.out.println("Number of clusters = " + clusters.size());
		System.out.println("Deleting edge " + e.getFrom().getName() + " - " + e.getTo().getName() + 
				" with betweenness " +  e.getWeight()/2);
		
		/*
		 * Remove edge from graph
		 */
		Node person1 = e.getFrom();
		Node person2 = e.getTo();
		person1.removeAFriend(person2);
		person2.removeAFriend(person1);
		
		// Clear all of the relevant values
		SearchUtilities.clearEdges();
		clearClusteringValues(allNodes);
		clusters = getClusters(allNodes);
		
		// Continue to delete edges until the number of clusters is 6
		while (clusters.size() < 6) {
			
			System.out.println("Number of clusters = " + clusters.size());
			
			// Run the Girvan-Newman algorithm to accumulate edge betweenness for each root node
			for (List<Node> cluster : clusters) {
				if (cluster.size() <= 1) continue;
				for (Node n : cluster) {
					SearchUtilities.BreadthFirstGraphSearch(n);
					clearSigmaValues(allNodes); clearDeltaValues(allNodes); clearPValues(allNodes); clearDValues(allNodes);
				}
			} 
			// Get the maximum edge 
			Edge max = SearchUtilities.getMaxEdge();
			System.out.println("Deleting edge " + max.getFrom().getName() + " - " + max.getTo().getName() + 
					" with betweenness " +  max.getWeight()/2);
			
			/*
			 * Remove edge from graph
			 */
			person1 = max.getFrom();
			person2 = max.getTo();
			person1.removeAFriend(person2);
			person2.removeAFriend(person1);
			
			// Clear all of the relevant values
			SearchUtilities.clearEdges();
			clearClusteringValues(allNodes);
			clusters = getClusters(allNodes);
		} System.out.println("Number of clusters = " + clusters.size());
	}
	
	/*
	 * This method calculates the number of clusters and separates the nodes
	 * by cluster, returning a list of clusters (List<Node>)
	 */
	public static List<List<Node>> getClusters(List<Node> allNodes) {
		clearClusteringValues(allNodes);
		List<List<Node>> clusters = new ArrayList<List<Node>>();
		int cluster = 1;
		// Check the cluster that each node is currently in and update if necessary
		for (Node node : allNodes) {
			// If not currently part of a cluster, then run a depth first search on it to get its cluster
			if (node.getClustering() == -1) {
				SearchUtilities.ClusteringSearch(node, cluster);
				cluster++;
			} 
			// The if-else separate each node into their clusters to be returned
			if (clusters.size() >= (node.getClustering())) {
				List<Node> clusterNodes = clusters.get((int) node.getClustering() - 1);
				clusterNodes.add(node);
			} else {
				List<Node> clusterNodes = new ArrayList<Node>();
				clusterNodes.add(node);
				clusters.add((int)(node.getClustering()-1), clusterNodes);
			}
		} return clusters;
	}
	
	// Clear the clustering values for each node
	public static void clearClusteringValues(List<Node> allNodes) {
		for (Node person : allNodes) { person.setClustering(-1); }
	}
	
	// Get the top five nodes based on either clustering coefficient or node betweenness
	public static List<Node> getTopFive(List<Node> allNodes, char type) {
		List<Node> topFive = new ArrayList<Node>(); 
		Node first = new Node(null, null);
		Node second = new Node(null, null); Node third = new Node(null, null);
		Node fourth = new Node(null, null); Node fifth = new Node(null, null);
		double n=-1; double f=-1; double s=-1; double t=-1; double fo=-1; double fi=-1;
		for (Node node : allNodes) {
			// If the input is 'c', then use the clustering values
			if (type == 'c') {
				n = node.getClustering();
				f = first.getClustering();
				s = second.getClustering();
				t = third.getClustering();
				fo = fourth.getClustering();
				fi = fifth.getClustering();
			} 
			// If the input is 'b', then use the CB values
			else if (type == 'b') {
				n = node.getCB();
				f = first.getCB();
				s = second.getCB();
				t = third.getCB();
				fo = fourth.getCB();
				fi = fifth.getCB();
			}
			// Get the top five
			if (n > f) {
				Node temp = first; first = node;
				Node temp1 = second; second = temp;
				temp = third; third = temp1;
				temp1 = fourth; fourth = temp;
				fifth = temp1;
			} else if (n > s) {
				Node temp1 = second; second = node;
				Node temp = third; third = temp1;
				temp1 = fourth; fourth = temp;
				fifth = temp1;
			} else if (n > t) {
				Node temp = third; third = node;
				Node temp1 = fourth; fourth = temp;
				fifth = temp1;
			} else if (n > fo) {
				Node temp1 = fourth; fourth = node;
				fifth = temp1;
			} else if (n >= fi) {
				fifth = node;
			}
		} topFive.add(0, first); topFive.add(1, second);
		topFive.add(2, third); topFive.add(3, fourth); 
		topFive.add(4, fifth);
		return topFive;
	}
}
