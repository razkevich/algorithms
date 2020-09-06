package interviewcamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chapter23 {
	public static void main(String[] args) {

		Node n1 = new Node(1);
		Node n2 = new Node(2);
		Node n3 = new Node(3);
		Node n4 = new Node(4);
		Node n5 = new Node(5);
		Node n6 = new Node(6);
		Node n7 = new Node(7);

		n4.setLeft(n2);
		n4.setRight(n6);
		n2.setLeft(n1);
		n2.setRight(n3);
		n6.setLeft(n5);
		n6.setRight(n7);

		n1.setParent(n2);
		n3.setParent(n2);
		n2.setParent(n4);
		n5.setParent(n6);
		n7.setParent(n6);
		n6.setParent(n4);


		System.out.println(findNextSuccessor(n6).value);
		System.out.println(searchRange(n4, 1, 6));

		System.out.println(lca(n4, 1, 5));
		System.out.println();
	}

	private static int lca(Node node, int v1, int v2) {
		while (!(v1 < node.value && v2 > node.value) && node != null) {
			if (node.value > v1 && node.value > v2) {
				node = node.left;
			} else if (node.value < v1 && node.value < v2) {
				node = node.right;
			} else break;

		}
		return node.value;
	}

	private static List<Integer> searchRange(Node node, int start, int end) {
		List<Integer> result = new ArrayList<>();
		Node current = findElement(node, start);
		if (current.value <= end && current.value >= start) {
			result.add(current.value);
		}
		while (current.value <= end) {
			current = findNextSuccessor(current);
			if (current.value <= end) {
				result.add(current.value);
			}
		}
		return result;
	}

	private static Node findElement(Node node, int value) {
		Node current = node;
		Map<Node, Integer> map = new HashMap<>();
		while (current != null) {
			map.put(current, value - current.value);
			if (current.value > value) {
				current = current.getLeft();

			} else if (current.value < value) {
				current = current.getRight();
			} else {
				return current;
			}
		}
		return map.entrySet().stream().min(Map.Entry.comparingByValue()).get().getKey();
	}

	private static Node findNextSuccessor(Node node) {
		if (node.right != null) {
			return leftMost(node.right);
		} else {
			Node current = getRoot(node);
			Node result = null;
			while (current != null) {
				if (current.value > node.value) {
					result = current;
					current = current.getLeft();
				} else if (current.value < node.value) {
					current = current.getRight();
				} else break;
			}
			return result;
		}
	}

	private static Node getRoot(Node node) {
		while (node.parent != null) {
			node = node.parent;
		}
		return node;
	}

	private static Node leftMost(Node node) {
		while (node.left != null) {
			node = node.left;
		}
		return node;
	}

	static class Node {
		Node left;
		Node right;
		Node parent;
		int value;

		public Node(int value) {
			this.value = value;
		}

		public Node getLeft() {
			return left;
		}

		public void setLeft(Node left) {
			this.left = left;
		}

		public Node getRight() {
			return right;
		}

		public void setRight(Node right) {
			this.right = right;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public Node getParent() {
			return parent;
		}

		public void setParent(Node parent) {
			this.parent = parent;
		}
	}
}
