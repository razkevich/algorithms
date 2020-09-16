package interviewcamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Chapter15 {

	public static void main(String[] args) {

		Node n1 = new Node(1);
		Node n2 = new Node(2);
		Node n3 = new Node(3);
		Node n4 = new Node(4);
		Node n5 = new Node(5);

		n1.addNeighbor(n2);
		n1.addNeighbor(n4);


		n2.addNeighbor(n4);
		n2.addNeighbor(n3);
		n2.addNeighbor(n5);

		n3.addNeighbor(n5);

		Stack<Node> stack = new Stack<>();
		dfsVisitRecursive(n1, stack);
//		while (!stack.isEmpty()) System.out.println(stack.pop());
	}

	private static void dfsVisitRecursive(Node start, Stack<Node> stack) {
		start.setState(State.VISITING);
		Collections.reverse(start.neighbors);
		for (Node nei : start.neighbors) {
			if (nei.state == State.UNVISITED)
				dfsVisitRecursive(nei, stack);
		}
		start.state = State.VISITED;
		System.out.println(start);
		stack.push(start);
	}

	private static Node dfsVisit(Node start, int value) {
		Stack<Path> queue = new Stack<>();
		queue.add(new Path(start, 0));

		while (!queue.isEmpty()) {
			Path current = queue.pop();
			System.out.println(current.lastNode.data + ":" + current.level);
			for (Node neighbor : current.lastNode.neighbors) {
				if (neighbor.state == State.UNVISITED) {
					neighbor.setState(State.VISITING);
					queue.push(new Path(neighbor, current.level + 1));
				}
			}
		}
		return null;
	}

	static class Path {
		Node lastNode;
		int level;

		public Path(Node lastNode, int level) {
			this.lastNode = lastNode;
			this.level = level;
		}
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
