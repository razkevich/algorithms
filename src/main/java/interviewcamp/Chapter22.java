package interviewcamp;

import java.util.Stack;

public class Chapter22 {
	public static void main(String[] args) {
		Node n10 = new Node(10);
		Node n1 = new Node(1);
		Node n100 = new Node(100);
		Node n1000 = new Node(1000);
		Node n5 = new Node(5);
		n100.right = n1000;
		n100.left = n10;
		n10.left = n1;
		n10.right = n5;
		inOrderVisit(n100);
	}

	public static void preOrderVisit(Node n) {
		Stack<Node> stack = new Stack<>();
		stack.push(n);

		while (!stack.isEmpty()) {
			Node peek = stack.peek();
			if (peek.status == Chapter6.Status.PUSHED_CHILDREN) {
				stack.pop();
				continue;
			}
			System.out.println(peek);
			if (peek.right != null) stack.push(peek.right);
			if (peek.left != null) stack.push(peek.left);
			peek.status = Chapter6.Status.PUSHED_CHILDREN;
		}
	}

	public static void postOrderVisit(Node n) {
		Stack<Node> stack = new Stack<>();
		stack.push(n);
		while (!stack.isEmpty()) {
			Node peek = stack.peek();
			if (peek.status == Chapter6.Status.PUSHED_CHILDREN) {
				System.out.println(stack.pop());
				continue;
			}
			if (peek.right != null) stack.push(peek.right);
			if (peek.left != null) stack.push(peek.left);
			peek.status = Chapter6.Status.PUSHED_CHILDREN;
		}
	}

	public static void inOrderVisit(Node n) {
		Stack<Node> stack = new Stack<>();
		stack.push(n);
		while (!stack.isEmpty()) {
			Node pop = stack.pop();
			if (pop.status == Chapter6.Status.PUSHED_CHILDREN) {
				System.out.println(pop);
			} else {
				if (pop.right != null) stack.push(pop.right);
				stack.push(pop);
				if (pop.left != null) stack.push(pop.left);
				pop.status = Chapter6.Status.PUSHED_CHILDREN;
			}
		}
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
