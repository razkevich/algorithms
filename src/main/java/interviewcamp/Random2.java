package interviewcamp;

import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

public class Random2 {

	public static void main(String[] args) {
		System.out.println(new Random2().minKnightMoves(2, 112));
	}

	class Path {
		int steps;
		int x;
		int y;

		public Path(int steps, int x, int y) {
			this.steps = steps;
			this.x = x;
			this.y = y;
		}

		public Path(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}



	public int majorityElement(int[] nums) {
		Map<Integer, Integer> map=new TreeMap<>();
		for (int n:nums){
			map.put(n,map.getOrDefault(n,0)+1);
		}
		return map.entrySet().stream().max(Comparator.comparing(a->a.getValue())).get().getKey();
	}

	public int minKnightMoves(int x, int y) {
		Map<Integer, Integer> map=new TreeMap<>();
		PriorityQueue<Path> q = new PriorityQueue<>(Comparator.comparing(a -> a.steps + getDistance(a, new Path(x, y))));
		q.add(new Path(0, 0, 0));
		while (!q.isEmpty()) {
			Path current = q.poll();
			int x1 = current.x;
			if (x1 == x && current.y == y) {
				return current.steps;
			}
			for (Path path : getPaths(x1, current.y)) {
				path.steps = current.steps + 1;
				q.add(path);
			}
		}
		return -1;
	}

	private Path[] getPaths(int x, int y) {
		return new Path[]{
				new Path(x - 2, y + 1),
				new Path(x - 1, y + 2),
				new Path(x + 1, y + 2),
				new Path(x + 2, y + 1),
				new Path(x + 2, y - 1),
				new Path(x + 1, y - 2),
				new Path(x - 1, y - 2),
				new Path(x - 2, y - 1)};
	}

	private double getDistance(Path path, Path path1) {
		int deltaX = path.x - path1.x;
		int deltaY = path.y - path1.y;
		return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
	}
}
