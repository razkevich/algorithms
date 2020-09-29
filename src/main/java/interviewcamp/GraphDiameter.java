package interviewcamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GraphDiameter {

	public static void main(String[] args) {
		Node a = new Node("a");
		Node b = new Node("b");
		Node c = new Node("c");
		Node d = new Node("d");
		Node e = new Node("e");
		Node f = new Node("f");
		Node g = new Node("g");
		Node h = new Node("h");
		Node i = new Node("i");
		a.addNeighbor(d);
		a.addNeighbor(b);
		b.addNeighbor(c);
		b.addNeighbor(f);
		d.addNeighbor(e);
		e.addNeighbor(i);
		f.addNeighbor(h);
		f.addNeighbor(g);
		h.addNeighbor(i);

		Stack<Node> sorted = topoSort(a, b, c, d, e, f, g, h, i);

		int longest = 0;
		for (int j = sorted.size() - 1; j >= 0; j--) {
			Node node = sorted.get(j);
			for (Node nei : node.neighbors) {
				nei.longestPath = Math.max(nei.longestPath, node.longestPath + 1);
				longest = Math.max(longest, nei.longestPath);
			}
		}
		System.out.println(longest);
	}

	private static Stack<Node> topoSort(Node... nodes) {
		Stack<Node> stack = new Stack<>();
		for (Node node : nodes) {
			if (node.getState() == State.UNVISITED)
				dfsVisit(node, stack);
		}
		return stack;
	}

	public static void dfsVisit(Node node, Stack<Node> stack) {
		node.setState(State.VISITING);
		for (Node nei : node.neighbors) {
			if (nei.getState() == State.UNVISITED) {
				dfsVisit(nei, stack);
			}
		}
		stack.push(node);
		node.setState(State.VISITED);
	}


	public enum State {UNVISITED, VISITING, VISITED;}

	public static class Node {
		List<Node> neighbors;
		String data;
		State state;
		int longestPath;

		@Override
		public String toString() {
			return "Node{" +
					"data='" + data + '\'' +
					'}';
		}

		public Node(String data) {
			super();
			this.data = data;
			state = State.UNVISITED;
			this.longestPath = 0;
			neighbors = new ArrayList<Node>();
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
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
