package interviewcamp;

import java.util.HashMap;

class LRUCache {

	HashMap<Integer, Node<Integer, Integer>> map;
	Node<Integer, Integer> head;
	Node<Integer, Integer> tail;
	int capacity;

	public LRUCache(int capacity) {
		this.map = new HashMap<>();
		this.capacity = capacity;
	}

	public int get(int key) {
		Node<Integer, Integer> node = map.get(key);
		if (node == null) return -1;
		remove(key);
		add(node.getKey(), node.value);

		return node.value;
	}

	public void put(int key, int value) {
		if (map.size() == capacity) {
			remove(head.getKey());
		}

		add(key, value);
	}

	private void remove(Integer key) {
		if (!map.containsKey(key)) return;
		Node<Integer, Integer> toRemove = map.get(key);
		removeFromLinkedList(toRemove);
		map.remove(key);
	}

	private void add(Integer key, Integer value) {
		Node<Integer, Integer> newNode = new Node<>(key, value);
		appendToLinkedList(newNode);
		map.put(key, newNode);
	}

	public void appendToLinkedList(Node<Integer, Integer> toAdd) {
		if (head == null) {
			head = toAdd;
		} else {
			tail.setNext(toAdd);
			toAdd.setPrev(tail);
		}
		tail = toAdd;
	}


	public void removeFromLinkedList(Node<Integer, Integer> toRemove) {
		if (toRemove.getPrev() != null) toRemove.getPrev().setNext(toRemove.getNext());
		if (toRemove.getNext() != null) toRemove.getNext().setPrev(toRemove.getPrev());
		if (toRemove == head) head = toRemove.getNext();
		if (toRemove == tail) tail = toRemove.getPrev();
	}

	public class Node<K, V> {
		Node<K, V> next;
		Node<K, V> prev;
		K key;
		V value;

		public Node(K key, V value) {
			super();
			this.key = key;
			this.value = value;
		}

		public Node<K, V> getNext() {
			return next;
		}

		public void setNext(Node<K, V> next) {
			this.next = next;
		}

		public Node<K, V> getPrev() {
			return prev;
		}

		public void setPrev(Node<K, V> prev) {
			this.prev = prev;
		}

		public K getKey() {
			return key;
		}
	}
}
