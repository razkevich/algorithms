package interviewcamp;

import java.util.Stack;

public class Chapter22 {
	public static void main(String[] args) {
		Node n1 = new Node(10);
		Node n2 = new Node(1);
		Node n3 = new Node(100);
		Node n4 = new Node(1000);


		n3.right = n4;

		n1.left = n2;
		n1.right = n3;

		preOrderVisit(n1);
//
//
//				10
//		1				100
//								1000
//
//
//
//
	}
//
//	public static void preOrderVisitRec(Node n) {
//		System.out.println(n);
//		preOrderVisit(n.left);
//		preOrderVisit(n.right);
//	}

	public static void preOrderVisit(Node n) {
		Stack<Node> stack = new Stack<>();
		stack.push(n);

		while (!stack.isEmpty()) {
			Node peek = stack.peek();
			if (peek.status == Chapter6.Status.VISITED) {
				stack.pop();
				continue;
			}
			System.out.println(peek);
			if (peek.right != null) stack.push(peek.right);
			if (peek.left != null) stack.push(peek.left);
			peek.status = Chapter6.Status.VISITED;
		}
	}

	public static void postOrderVisit(Node n) {
		Stack<Node> stack = new Stack<>();
		stack.push(n);
		while (!stack.isEmpty()) {
			Node peek = stack.peek();
			if (peek.status == Chapter6.Status.VISITED) {
				System.out.println(stack.pop());
				continue;
			}
			if (peek.right != null) stack.push(peek.right);
			if (peek.left != null) stack.push(peek.left);
			peek.status = Chapter6.Status.VISITED;
		}
	}

	public static void inOrderVisit(Node n) {

	}

	public static class Node {
		Node left;
		Node right;
		int value;
		Chapter6.Status status;


		@Override
		public String toString() {
			return "Node{" +
					"value=" + value +
					'}';
		}

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
	}
}
