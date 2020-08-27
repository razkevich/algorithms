package interviewcamp;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class Chapter10 {
	public static void main(String[] args) {
		System.out.println(Arrays.toString(slidingWindow(new int[]{2, 3, 5, 6, 2, 1})));
	}

	private static int[] slidingWindow(int[] a) {
		Queue<Integer> q = new ArrayDeque<>();
		int[] result = new int[a.length - 2];
		int sum = 0;
		for (int i = 0; i < a.length; i++) {
			q.offer(a[i]);
			sum += a[i];
			if (q.size() > 3) {
				sum -= q.poll();
				result[i - 2] = sum;
			} else if (q.size() == 3) {
				result[i - 2] = sum;
			}
		}
		return result;
	}
}
