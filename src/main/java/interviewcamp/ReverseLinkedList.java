package interviewcamp;

public class ReverseLinkedList {


	public static void main(String[] args) {
		Node head = new Node(1, new Node(2, new Node(3, new Node(4, new Node(5, null)))));

		Node reverse = reverse(head);
		System.out.println(reverse);

	}

	private static Node reverse(final Node head) {
		Node current = head;
		Node prev = null;

		while (current != null) {
			Node temp = current.next;
			current.setNext(prev);
			prev = current;
			current = temp;
		}

		return prev;
	}

	static class Node {
		int value;
		Node next;

		public Node(int value, Node next) {
			this.value = value;
			this.next = next;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public Node getNext() {
			return next;
		}

		public void setNext(Node next) {
			this.next = next;
		}

		@Override
		public String toString() {
			return "" + value;
		}
	}
}
