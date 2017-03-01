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
		//Node me = crawl(20);
		
		Node me = testSetup();
		
		List<Node> allNodes = getAllNodes(me);
		
		int nodes = getTotalNumberOfNodes(allNodes);
		int edges = getTotalNumberOfEdges(allNodes);
		double[] diameterAndPathLength = getDiameterAndPath(allNodes);
		int diameter = (int) diameterAndPathLength[0];
		double pathLength = diameterAndPathLength[1];
		double density = getDensity(allNodes);
		List<Node> topFiveClustering = getTopFive(clusteringCoefficient(allNodes), 'c');
		List<Node> topFiveBetweenness = getTopFive(allNodes, 'b');
		
		System.out.println(nodes + " nodes, " + edges + " edges, undirected");
		System.out.println();
		System.out.println("Number of Components: 1");
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
			System.out.println(++a + " ('" + node.getName() + ", " + node.getCB() + ")");
		}
		
		/*List<Node> allFriends = new ArrayList<Node>();
		allFriends.add(me);
		for (Node person : me.getFriends()) {
			if (!(allFriends.contains(person))) allFriends.add(person);
			for (Node oPerson : person.getFriends()) {
				if (!(allFriends.contains(oPerson))) allFriends.add(oPerson);
			}
		}*/
		
		/*for (Node person : allNodes) {
			person.print();
		}*/
	}
	
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
	
	public static Node crawl(int width) throws InterruptedException {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--disable-notifications");
		WebDriver driver = new ChromeDriver(options);
		driver.get("http://www.facebook.com");
		
		WebElement email = driver.findElement(By.id("email"));
		WebElement password = driver.findElement(By.id("pass"));
		
		email.sendKeys("enter_user_name");
		Thread.sleep(5000);
		password.sendKeys("enter_password");
		Thread.sleep(5000);
		WebElement login = driver.findElement(By.id("loginbutton"));
		login.click();
		Thread.sleep(5000);
		
		driver.get("https://www.facebook.com/jacalexjohnson/friends?lst=100001353948322%3A100001353948322%3A1486678307&source_ref=pb_friends_tl");
		Thread.sleep(2500);
		
		JavascriptExecutor jsx = (JavascriptExecutor)driver;
		// The i value is the multiple of 20 friends (i.e. 2 = 40, 3 = 60)
		for (int i = 0; i <= (width / 20); i++) {
			jsx.executeScript("window.scrollBy(0,1000)", "");
			Thread.sleep(250);
		}
		
		Node me = new Node("Jacob Johnson", "https://www.facebook.com/jacalexjohnson/friends_mutual");
		List<Node> firstLevel = new ArrayList<Node>();
		List<WebElement> friends = driver.findElements(By.xpath("//div[@class='fsl fwb fcb']/a"));
		for (WebElement friend : friends) {
			String name = friend.getText();
			String[] url = friend.getAttribute("href").split("\\?");
			String userTag = url[0].split("/")[3];
			if (!userTag.equals("profile.php")) {
				String href = url[0] + "/friends_mutual";
				Node person = new Node(name, href);
				firstLevel.add(person);
			}
		}
		
		int counter = 0;
		for (Node someone : firstLevel) {
			if (counter >= width) break;
			driver.get(someone.getHref());
			Thread.sleep(5000);
			List<WebElement> friendZones = driver.findElements(By.xpath("//span[@class='_3d0']"));
			if (friendZones.equals(null)) continue;
			WebElement friendZone;
			if (friendZones.size() <= 1) { friendZone = friendZones.get(0); } 
			else { friendZone = friendZones.get(1); } 
			int mutualFriends = Integer.parseInt(friendZone.getText());
			if (!(mutualFriends < width)) {
				someone.addAFriend(me);
				me.addAFriend(someone);
				counter++;
			}
		}
		
		firstLevel = me.getFriendsAndClear();
		me.addFriends(firstLevel);
		for (Node friend: firstLevel) {
			driver.get(friend.getHref());
			Thread.sleep(500);
			for (int i = 0; i <= (width/20); i++) {
				jsx.executeScript("window.scrollBy(0,1000)", "");
				Thread.sleep(250);
			}
			Thread.sleep(2500);
			friends = driver.findElements(By.xpath("//div[@class='fsl fwb fcb']/a"));
			counter = 0;
			for (WebElement friend2 : friends) {
				if (counter >= width) break;
				String name = friend2.getText();
				String[] url = friend2.getAttribute("href").split("\\?");
				String userTag = url[0].split("/")[3];
				if (!userTag.equals("profile.php")) {
					String href = url[0] + "/friends_mutual";
					Node person = new Node(name, href);
					if (me.getFriends().contains(person)) {
						Node oldPerson = me.getFriends().get(me.getFriends().indexOf(person));
						oldPerson.addAFriend(me); oldPerson.addAFriend(friend);
						me.addAFriend(oldPerson); friend.addAFriend(oldPerson);
					} else {
						person.addAFriend(me); person.addAFriend(friend);
						me.addAFriend(person); friend.addAFriend(person);
					} counter++;
				}
			} Thread.sleep(2500);
		}
		
		driver.close();
		
		return me;
	}
	
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
	
	public static int getTotalNumberOfNodes(List<Node> allNodes) {
		return allNodes.size();
	}
	
	public static int getTotalNumberOfEdges(List<Node> allNodes) {
		int edges = 0;
		for (Node person : allNodes) {
			edges += person.getFriends().size();
		} return edges/2;
	}
	
	public static int allPossibleEdges(List<Node> allNodes) {
		int numNodes = getTotalNumberOfNodes(allNodes);
		int possibleNumbers = (numNodes * (numNodes-1))/2;
		return possibleNumbers;
	}
	
	public static double[] getDiameterAndPath(List<Node> allNodes) {
		double[] depthAndPath = new double[2]; double maxDepth = 0; double pathLength = 0;
		for (Node person : allNodes) {
			Node node = SearchUtilities.BreadthFirstGraphSearch(person);
			maxDepth = Math.max(maxDepth, node.getD());
			pathLength += clearDValues(allNodes);
			clearSigmaValues(allNodes); clearDeltaValues(allNodes); clearPValues(allNodes);
		} for (Node node : allNodes) node.setCB(node.getCB()/2);
		double averagePathLength = pathLength/(allNodes.size()*(allNodes.size()-1));
		depthAndPath[0] = maxDepth; depthAndPath[1] = averagePathLength;
		return depthAndPath;
	}
	
	public static int clearDValues(List<Node> allNodes) {
		int acc = 0;
		for (Node person : allNodes) {
			acc += person.getD();
			person.setD(-1);
		} return acc;
	}
	
	public static void clearCBValues(List<Node> allNodes) {
		for (Node person : allNodes) { System.out.println(person.getName() + " " + (person.getCB()));person.setCB(0); }
	}
	
	public static void clearSigmaValues(List<Node> allNodes) {
		for (Node person : allNodes) { person.setSigma(0); }
	}
	
	public static void clearDeltaValues(List<Node> allNodes) {
		for (Node person : allNodes) { person.setDelta(0); }
	}
	
	public static void clearPValues(List<Node> allNodes) {
		for (Node person : allNodes) { person.clearP(); }
	}
	
	public static double getDensity(List<Node> allNodes) {
		double density = (double)getTotalNumberOfEdges(allNodes)/allPossibleEdges(allNodes);
		return density;
	}
	
	public static List<Node> clusteringCoefficient(List<Node> allNodes) {
		List<Node> explored = new ArrayList<Node>();
		for (Node person : allNodes) {
			int acc = 0; 
			int numFriends = person.getFriends().size();
			for (Node friend : person.getFriends()) {
				for (Node friendsOfFriend : friend.getFriends()) {
					if (explored.contains(friendsOfFriend)) acc ++;
				} explored.add(friend);
			} 
			double clustering = 0;
			int possible = (numFriends * (numFriends - 1));
			if (possible != 0) clustering = (double) (2 * acc) / possible;
			person.setClustering(clustering);
			explored = new ArrayList<Node>();
		} return allNodes;
	}
	
	public static List<Node> getTopFive(List<Node> allNodes, char type) {
		List<Node> topFive = new ArrayList<Node>(); 
		Node first = new Node(null, null);
		Node second = new Node(null, null); Node third = new Node(null, null);
		Node fourth = new Node(null, null); Node fifth = new Node(null, null);
		double n=-1; double f=-1; double s=-1; double t=-1; double fo=-1; double fi=-1;
		for (Node node : allNodes) {
			if (type == 'c') {
				n = node.getClustering();
				f = first.getClustering();
				s = second.getClustering();
				t = third.getClustering();
				fo = fourth.getClustering();
				fi = fifth.getClustering();
			} else if (type == 'b') {
				n = node.getCB();
				f = first.getCB();
				s = second.getCB();
				t = third.getCB();
				fo = fourth.getCB();
				fi = fifth.getCB();
			}
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
