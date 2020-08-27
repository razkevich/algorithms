package interviewcamp;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Chapter16 {
	public static void main(String[] args) {
		System.out.println(kMin(new int[]{5, 4, 7, 2, 9, 8, 1, 0, -1}, 4));
	}

	private static Collection<Integer> kMin(int[] a, int count) {
		PriorityQueue<Integer> q = new PriorityQueue<>(Comparator.reverseOrder());
		Arrays.stream(a).forEach(b -> {
			if (q.isEmpty()) {
				q.offer(b);
				return;
			}
			int max = q.peek();
			if (b < max && q.size() >= count) {
				q.remove();
				q.offer(b);
			} else {
				q.offer(b);
			}
		});


		return q;
	}

}
