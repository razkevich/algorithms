package interviewcamp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class IntervalListIntersection {

	public static void main(String[] args) {
	}

	public int[][] intervalIntersection(int[][] A, int[][] B) {
		List<int[]> result = new ArrayList<>();
		PriorityQueue<Point> q = new PriorityQueue<>(Comparator.comparing(a -> ((Point) a).value).thenComparing(a -> ((Point) a).type));

		for (int[] a : A) {
			q.add(new Point(a[0], Type.BEGIN));
			q.add(new Point(a[1], Type.END));
		}
		for (int[] a : B) {
			q.add(new Point(a[0], Type.BEGIN));
			q.add(new Point(a[1], Type.END));
		}

		Point cur;
		int count = 0;
		int lastStart = -1;
		while (!q.isEmpty()) {
			cur = q.poll();
			if (count == 2 && cur.type == Type.END) {
				result.add(new int[]{lastStart, cur.value});
			}
			if (cur.type == Type.BEGIN) {
				lastStart = cur.value;
				count++;
			}
			if (cur.type == Type.END) count--;

		}

		int[][] res = new int[result.size()][2];
		int i = 0;
		for (int[] x : result) {
			res[i][0] = x[0];
			res[i][1] = x[1];
			i++;
		}

		return res;
	}

	class Point {
		int value;
		Type type;

		@Override
		public String toString() {
			return "Point{" +
					"value=" + value +
					", type=" + type +
					'}';
		}

		public Point(int value, Type type) {
			this.value = value;
			this.type = type;
		}
	}

	enum Type {BEGIN, END}
}
