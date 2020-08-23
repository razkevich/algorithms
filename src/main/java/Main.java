package main.java;

public class Main {

	public static void main(String[] args) {
		Node n1 = new Node(1);
		Node n2 = new Node(2);
		Node n3 = new Node(3);
		Node n4 = new Node(4);
		Node n5 = new Node(5);
		Node n6 = new Node(6);
		Node n7 = new Node(7);
		n4.left = n2;
		n4.right = n6;
		n2.right = n3;
		n2.left = n1;
		n6.left = n5;
		n6.right = n7;

		System.out.println(search(n4, 1));

	}

	private static Node search(Node parent, int data) {
		if (data == parent.data) {
			return parent;
		}
		if (data > parent.data) {
			return search(parent.right, data);
		} else {
			return search(parent.left, data);
		}
	}

	static int getMax(Node node, int maxSoFar) {
		if (node == null) return maxSoFar;
		return Math.max(node.data, maxSoFar);
	}

	static int getMin(Node node, int minSoFar) {
		if (node == null) return minSoFar;
		return Math.min(node.data, minSoFar);
	}


	static class Node {
		Node left;
		Node right;
		int data;
		boolean visited;

		public Node(int data) {
			this.data = data;
		}

		@Override
		public String toString() {
			return String.valueOf(data);
		}
	}
}
