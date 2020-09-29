package interviewcamp;

import java.util.Comparator;
import java.util.PriorityQueue;

public class KSmallestNumbers {
	public static void main(String[] args) {
		new KSmallestNumbers().kSmallest(new int[]{6, 3, 6, 6, 2, 2, 4}, 4);
	}

	private void kSmallest(int[] ints, int k) {
		PriorityQueue<Integer> q = new PriorityQueue<>(Comparator.reverseOrder());
		for (int i : ints) {
			if (q.size() < k) {
				q.add(i);
			} else if (q.size() < k || (!q.isEmpty() && q.peek() > i)) {
				q.poll();
				q.add(i);
			}
		}
		q.forEach(System.out::println);
	}

}
