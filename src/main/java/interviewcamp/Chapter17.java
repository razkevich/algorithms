package interviewcamp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Chapter17 {
	public static void main(String[] args) {
		System.out.println(overlap(new int[][]{new int[]{5, 6}, new int[]{1, 3}, new int[]{7, 9}}));
		System.out.println(merge(new int[][]{new int[]{1, 3}, new int[]{3, 5}, new int[]{6, 8}, new int[]{7, 9}}));
	}

	static class Interval {
		Point start;
		Point end;

		public Interval(Point start, Point end) {
			this.start = start;
			this.end = end;
		}

		@Override
		public String toString() {
			return start + " - " + end;
		}
	}

	private static List<Interval> merge(int[][] intervals) {
		PriorityQueue<Point> priorityQueue = new PriorityQueue<>(Comparator.comparing(a -> ((Point) a).value).thenComparing(a -> ((Point) a).type));
		List<Interval> result = new ArrayList<>();
		for (int[] interval : intervals) {
			priorityQueue.offer(new Point(interval[0], Type.START));
			priorityQueue.offer(new Point(interval[1], Type.END));
		}
		int counter = 0;
		int begin = 0;
		int end = 0;
		while (!priorityQueue.isEmpty()) {
			Point point = priorityQueue.poll();
			if (point.type == Type.START && counter == 0) {
				begin = point.value;
			}
			if (point.type == Type.START) counter++;
			if (point.type == Type.END) counter--;

			if (point.type == Type.END && counter == 0) {
				end = point.value;
				result.add(new Interval(new Point(begin, Type.START), new Point(end, Type.END)));
			}
		}
		return result;
	}

	enum Type {START, END}

	static class Point {
		int value;
		Type type;

		public Point(int value, Type type) {
			this.value = value;
			this.type = type;

		}

		@Override
		public String toString() {
			return value + "(" + type + ")";
		}
	}

	private static boolean overlap(int[][] intervals) {
		PriorityQueue<Point> priorityQueue = new PriorityQueue<>(Comparator.comparing(a -> a.value));
		for (int[] interval : intervals) {
			priorityQueue.offer(new Point(interval[0], Type.START));
			priorityQueue.offer(new Point(interval[1], Type.END));
		}
		int count = 0;
		while (!priorityQueue.isEmpty()) {
			Point point = priorityQueue.poll();
			if (point.type == Type.START) count++;
			if (point.type == Type.END) count--;
			if (count > 1) {
				return true;
			}

		}
		return false;
	}


}
