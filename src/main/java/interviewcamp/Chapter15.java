package interviewcamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Chapter15 {

	public static void main(String[] args) {

		Node n1 = new Node(1);
		Node n2 = new Node(2);
		Node n3 = new Node(3);
		Node n4 = new Node(4);
		Node n5 = new Node(5);
		Node n6 = new Node(6);

		n1.addNeighbor(n2);
		n1.addNeighbor(n3);
		n2.addNeighbor(n4);
		n3.addNeighbor(n4);
		n3.addNeighbor(n5);
		n4.addNeighbor(n6);

		n5.addNeighbor(n6);

		Graph graph = new Graph(Arrays.asList(n1, n2, n3, n4, n5, n6));
		dfs(graph, 6);

	}

	public static Node dfs(Graph graph, int value) {
		for (Node node : graph.nodes) {
			if (node.state == State.UNVISITED) {
				Node found = dfsVisit(node, value);
				if (found != null) {
					return found;
				}
			}
		}
		return null;
	}

	private static Node dfsVisit(Node start, int value) {
		Stack<Node> queue = new Stack<>();
		queue.add(start);

		while (!queue.isEmpty()) {
			Node current = queue.pop();
			System.out.println(current.data);
			current.setState(State.VISITED);
			for (Node neighbor : current.neighbors) {
				if (neighbor.state == State.UNVISITED)
					neighbor.setState(State.VISITING);
					queue.push(neighbor);
			}
		}
		return null;
	}

	public static class Graph {
		List<Node> nodes;

		public Graph(List<Node> nodes) {
			super();
			this.nodes = nodes;
		}

		public void addNode(Node node) {
			nodes.add(node);
		}

		public List<Node> getNodes() {
			return nodes;
		}
	}

	public enum State {UNVISITED, VISITING, VISITED;}

	public static class Node {
		List<Node> neighbors;
		int data;
		State state;
		int longestPath;

		@Override
		public String toString() {
			return "Node{" +
					"data=" + data +
					'}';
		}

		public Node(int data) {
			super();
			this.data = data;
			state = State.UNVISITED;
			this.longestPath = 0;
			neighbors = new ArrayList<>();
		}

		public int getData() {
			return data;
		}

		public void setData(int data) {
			this.data = data;
		}

		public void setState(State state) {
			this.state = state;
		}

		public State getState() {
			return state;
		}

		public int getLongestPath() {
			return longestPath;
		}

		public void setLongestPath(int longestPath) {
			this.longestPath = longestPath;
		}

		public void addNeighbor(Node node) {
			neighbors.add(node);
		}

		public List<Node> getNeighbors() {
			return neighbors;
		}
	}
}
