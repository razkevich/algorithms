package interviewcamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Chapter20 {
	public static void main(String[] args) {
		Node a = new Node();
		Node b = new Node();
		Node c = new Node();
		Node d = new Node();
		Node e = new Node();

		a.next = Arrays.asList(b);
		b.next = Arrays.asList(e, d);
		c.next = Arrays.asList(b);
		d.next = Arrays.asList(c, e);


		System.out.println(cycle(a));
	}

	private static boolean cycle(Node a) {
		Stack<Path> stack = new Stack<>();
		stack.push(new Path(new ArrayList<>(Arrays.asList(a))));
		while (!stack.isEmpty()) {
			Path current = stack.pop();
			List<Node> next = current.path.get(current.path.size() - 1).next;
			for (Node ch : next == null ? Set.<Node>of() : next) {
				if (current.path.contains(ch)) {
					return true;
				}
				Path currentPlus = new Path(new ArrayList<>(current.path));
				currentPlus.path.add(ch);
				stack.push(currentPlus);
			}
		}
		return false;
	}


	static class Node {
		List<Node> next;

	}

	static class Path {
		public Path(List<Node> path) {
			this.path = path;
		}

		List<Node> path;
	}

}
