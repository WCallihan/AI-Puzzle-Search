import java.util.*;
import java.lang.Math;
import java.io.File;
import java.io.FileNotFoundException;

public class Homework1 {
	
	private String inputState; //the initial input state given by the user
	private Node treeRoot; //serves as root for the tree
	private int MAX_DEPTH = 10; //cannot search at a depth beyond this limit
	
	public Homework1(String state) {
		inputState = state;
		treeRoot = new Node(null, inputState);
	}
	
	private static class Node implements Comparable<Node>{
		private Node parentNode;
		private ArrayList<Node> childrenNodes = null;
		private String puzzleState;
		private int depth;
		private boolean goalState; //true if the state of the node meats the goal
		private int cost;
		
		public Node(Node parent, String state) {
			parentNode = parent;
			puzzleState = state;
			if(parentNode != null) {
				depth = parentNode.getDepth()+1;
			} else {
				depth = 0; //root's depth is 0
			}
			goalState = puzzleState.equals("1 2 3 4 * 5 6 7 8");
		}
		public Node getParent() { return parentNode; }
		public ArrayList<Node> getChildren() {
			if(childrenNodes == null) {
				childrenNodes = generateChildren(puzzleState); //generates them the first time they are requested
			}
			return childrenNodes;
		}
		public String getState() { return puzzleState; }
		public int getDepth() { return depth; }
		public boolean getGoal() { return goalState; }
		public int getCost() { return cost; }
		public void setCost(int c) { cost = c; }
		
		//used to generated all of the node's children based on the blank's location
		private ArrayList<Node> generateChildren(String state) {
			ArrayList<Node> newChildren = new ArrayList<>();
			int indexOfBlank = state.indexOf('*');
			ArrayList<String> newStates = new ArrayList<>();
			//index numbers assume spaces between each tile
			if(indexOfBlank == 0) {
				newStates = generateSwitches(state, new int[]{2,6});
			} else if(indexOfBlank == 2) {
				newStates = generateSwitches(state, new int[]{0, 4, 8});
			} else if(indexOfBlank == 4) {
				newStates = generateSwitches(state, new int[]{2, 10});
			} else if(indexOfBlank == 6) {
				newStates = generateSwitches(state, new int[]{0, 8, 12});
			} else if(indexOfBlank == 8) {
				newStates = generateSwitches(state, new int[]{2, 6, 10, 14});
			} else if(indexOfBlank == 10) {
				newStates = generateSwitches(state, new int[]{4, 8, 16});
			} else if(indexOfBlank == 12) {
				newStates = generateSwitches(state, new int[]{6, 14});
			} else if(indexOfBlank == 14) {
				newStates = generateSwitches(state, new int[]{8, 12, 16});
			} else if(indexOfBlank == 16) {
				newStates = generateSwitches(state, new int[]{10, 14});
			} else{
				System.out.println("INCORRECT INDEX: indexOfBlank = " + indexOfBlank);
			}
			for (String newState : newStates) {
				newChildren.add(new Node(this, newState));
			}
			return newChildren;
		}
		//used by generateChildren to generate the switches given the indexes next to the blank
		private ArrayList<String> generateSwitches(String currentState, int[] indexesBeingSwitched) {
			ArrayList<String> newStates = new ArrayList<>();
			char[] c;
			for (int i : indexesBeingSwitched) {
				c = currentState.toCharArray();
				c[currentState.indexOf('*')] = c[i];
				c[i] = '*';
				String newState = new String(c);
				newStates.add(newState);
			}
			return newStates;
		}
	
		//used to by the PriorityQueue in the A* algorithm to sort the nodes based on their cost
		public int compareTo(Node n) {
	        if(this.cost < n.getCost()){
	            return -1;
	        } else if(this.cost > n.getCost()){
	            return 1;
	        } else
	            return 0;
	    }
		
		//used to print the node when printing the tree's solution
		public void print() {
			//index positions assume spaces between each item
			System.out.println(puzzleState.substring(0, 5));
			System.out.println(puzzleState.substring(6, 11));
			System.out.println(puzzleState.substring(12));
		}
	}
	
	//depth first search algorithm
	public void depthFirstSearch() {
		Set<String> visitedNodes = new HashSet<>(); //keeps track of visited states to avoid repeats
		Node currentNode = treeRoot;
		Stack<Node> nodesToVisit = new Stack<>();
		
		while(currentNode != null && !currentNode.getGoal()) {
			visitedNodes.add(currentNode.getState());
			//makes sure nothing is searched beyond the maximum allowed depth
			if(currentNode.getDepth() <= MAX_DEPTH) {
				//puts the current node's children on the top of the stack
				ArrayList<Node> currentNodeChildren = currentNode.getChildren();
				for (Node child : currentNodeChildren) {
					//does not add duplicate states to the stack to avoid looping
					if(!visitedNodes.contains(child.getState())) {
						nodesToVisit.push(child);
					}
				}
			}
			if(nodesToVisit.isEmpty())
				currentNode = null;
			else
				currentNode = nodesToVisit.pop(); //removes the latest child and makes it the current node
		}
		//double checks if the current node has the goal state
		if(currentNode != null && currentNode.getGoal()) {
			printTreeSolution(currentNode, visitedNodes.size());
		} else {
			System.out.println("Input state:");
			treeRoot.print();
			System.out.println("No solution was found in " + MAX_DEPTH + " steps or less.");
		}
	}
	
	//iterative deepening search algorithm
	public void iterativeDeepeningSearch() {
		Set<String> visitedNodes = new HashSet<>();
		int totalNodesVisited = 0;
		Node currentNode = treeRoot;
		Stack<Node> nodesToVisit = new Stack<>();
		
		//goes through iterations of maximum depths from 0 to the MAX_DEPTH
		for(int currentDepthLimit=0; currentDepthLimit<=MAX_DEPTH; currentDepthLimit++) {
			currentNode = treeRoot;
			//each iteration is a normal depth-first search with a different depth limit every time
			while(currentNode != null && !currentNode.getGoal()) {
				visitedNodes.add(currentNode.getState());
				//makes sure nothing is searched beyond the depth currently allowed
				if(currentNode.getDepth() < currentDepthLimit) {
					//puts the current node's children on the top of the stack
					ArrayList<Node> currentNodeChildren = currentNode.getChildren();
					for (Node child : currentNodeChildren) {
						//does not add duplicate states to the stack to avoid looping
						if(!visitedNodes.contains(child.getState())) {
							nodesToVisit.push(child);
						}
					}
				}
				if(nodesToVisit.isEmpty())
					currentNode = null;
				else
					currentNode = nodesToVisit.pop(); //removes the latest child and makes it the current node
			}
			//clears the visitedNodes so that duplicate nodes aren't blocked across iterations
			totalNodesVisited = visitedNodes.size();
			visitedNodes.clear();
		}
		//double checks if the current node has the goal state
		if(currentNode != null && currentNode.getGoal()) {
			printTreeSolution(currentNode, totalNodesVisited);
		} else {
			System.out.println("Input state:");
			treeRoot.print();
			System.out.println("No solution was found in " + MAX_DEPTH + " steps or less.");
		}
	}
	
	//A* search algorithm with two possible heuristics:
	//1. the total number of tiles that are not in their goal positions
	//2. the sum of the distance of each tile from their goal positions
	public void astar(int heuristic) {
		//double checks that a valid heuristic code was inputed
		if(heuristic != 1 && heuristic != 2) {
			System.out.println("INVALID HEURISTIC CODE: " + heuristic);
			return;
		}
		
		ArrayList<Node> visitedNodes = new ArrayList<>();
		Node currentNode = treeRoot;
		PriorityQueue<Node> nodesToVisit = new PriorityQueue<>();
		
		while(currentNode != null && !currentNode.getGoal()) {
			visitedNodes.add(currentNode);
			//makes sure nothing is searched beyond the maximum allowed depth
			if(currentNode.getDepth() <= MAX_DEPTH) {
				//puts the current node's children on the top of the queue
				ArrayList<Node> currentNodeChildren = currentNode.getChildren();
				for (Node child : currentNodeChildren) {
					if(heuristic == 1) {
						//a node's cost is its depth(required moves) plus the number of misplaced tiles
						child.setCost(child.getDepth() + outOfPlaceTiles(child.getState()));
					} else if(heuristic == 2) {
						//a node's cost is its depth(required moves) plus the Manhattan distance of their tiles from their goal positions
						child.setCost(child.getDepth() + tileDistances(child.getState()));
					}
					nodesToVisit.add(child);
				}
			}
			if(nodesToVisit.isEmpty())
				currentNode = null;
			else
				currentNode = nodesToVisit.poll(); //removes the child with the lowest cost and makes it the current node
		}
		//double checks if the current node has the goal state
		if(currentNode != null && currentNode.getGoal()) {
			printTreeSolution(currentNode, visitedNodes.size());
		} else {
			System.out.println("Input state:");
			treeRoot.print();
			System.out.println("No solution was found in " + MAX_DEPTH + " steps or less.");
		}
	}
	//returns the number of tiles not in their desired positions; helps with heuristic 1
	private int outOfPlaceTiles(String state) {
		return outOfPlaceHelper(state.charAt(0), '1') + outOfPlaceHelper(state.charAt(2), '2')
				+ outOfPlaceHelper(state.charAt(4), '3') + outOfPlaceHelper(state.charAt(6), '4')
				+ outOfPlaceHelper(state.charAt(10), '5') + outOfPlaceHelper(state.charAt(12), '6')
				+ outOfPlaceHelper(state.charAt(14), '7') + outOfPlaceHelper(state.charAt(16), '8');
	}
	private int outOfPlaceHelper(char currentChar, char desiredChar) {
		if(currentChar != desiredChar)
			return 1;
		else
			return 0;
	}
	//returns the sum of the tiles' distances away from their goal positions; helps with heuristic 2
	private int tileDistances(String state) {
		return oneTileDistance(state.indexOf('1'), 0) + oneTileDistance(state.indexOf('2'), 2)
				+ oneTileDistance(state.indexOf('3'), 4) + oneTileDistance(state.indexOf('4'), 6)
				+ oneTileDistance(state.indexOf('5'), 10) + oneTileDistance(state.indexOf('6'), 12)
				+ oneTileDistance(state.indexOf('7'), 14) + oneTileDistance(state.indexOf('8'), 16);
	}
	private int oneTileDistance(int currentIndex, int desiredIndex) {
		if(currentIndex == desiredIndex) {
			return 0;
		} else {
			return Math.abs(findRow(currentIndex) - findRow(desiredIndex)) + Math.abs(findColumn(currentIndex) - findColumn(desiredIndex));
		}
	}
	private int findRow(int index) {
		if(index == 0 || index == 2 || index == 4){
			return 1;
		} else if(index == 6 || index == 8 || index == 10) {
			return 2;
		} else if(index == 12 || index == 14 || index == 16) {
			return 3;
		} else {
			System.out.println("INVALID INDEX: " + index);
			return -1;
		}
	}
	private int findColumn(int index) {
		if(index == 0 || index == 6 || index == 12){
			return 1;
		} else if(index == 2 || index == 8 || index == 14) {
			return 2;
		} else if(index == 4 || index == 10 || index == 16) {
			return 3;
		} else {
			System.out.println("INVALID INDEX: " + index);
			return -1;
		}
	}
	
	//used to print the full solution of a tree if one is found
	private void printTreeSolution(Node goalNode, int nodesVisited) {
		Stack<Node> solutionStack = new Stack<>();
		solutionStack.push(goalNode);
		Node currentNode = goalNode.getParent();
		while(currentNode != null) {
			solutionStack.push(currentNode);
			currentNode = currentNode.getParent();
		}
		
		System.out.println("Input state:");
		while(!solutionStack.isEmpty()) {
			currentNode = solutionStack.pop();
			currentNode.print();
			System.out.println("");
		}
		System.out.println("Goal Reached");
		System.out.println("Number of moves = " + currentNode.getDepth());
		System.out.println("Number of states enqueued = " + nodesVisited);
		System.out.println("");
	}
	
	
	public static void main(String args[]) throws FileNotFoundException {
		//assigns the search type and input state from the command line arguments
		String searchType = args[0];
		File file = new File(args[1]);
		Scanner inFile = new Scanner(file);
		
		//accepts multiple initial states for multiple searches
		while(inFile.hasNextLine()) {
			String inputState = inFile.nextLine();			
			Homework1 hw1 = new Homework1(inputState);
			
			//chooses which search to use and calls it with the input state
			if(searchType.equals("depth-first")) {
				hw1.depthFirstSearch();
			} else if(searchType.equals("iterative-deepening")) {
				hw1.iterativeDeepeningSearch();
			} else if(searchType.equals("astar1")) {
				hw1.astar(1);
			} else if(searchType.equals("astar2")) {
				hw1.astar(2);
			}
		}
		
		inFile.close();
	}
}