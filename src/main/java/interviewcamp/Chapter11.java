package interviewcamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Chapter11 {
	public static void main(String[] args) {
		System.out.println(countWaysJumpStairs(new int[]{1, 3, 5}, 5));
		System.out.println(longestSubsequence(new int[]{1, 3, 2, 5, 3, 5, 6}, new ArrayList<>()));
//		System.out.println(coins(new int[]{1, 3, 2, 5, 3, 5, 6}, new ArrayList<>()));


	}

	private static List<Integer> longestSubsequence(int[] a, List<Integer> currentPath) {
		if (a.length == 0) {
			return currentPath;
		}
		List<Integer> best = new ArrayList<>(currentPath);
		for (int i = 0; i < a.length; i++) {
			if (currentPath.isEmpty() || a[i] > currentPath.get(currentPath.size() - 1)) {
				List<Integer> currentPathCopy = new ArrayList<>(currentPath);
				currentPathCopy.add(a[i]);
				List<Integer> newList = longestSubsequence(Arrays.copyOfRange(a, i + 1, a.length), currentPathCopy);
				if (newList.size() > best.size()) {
					best = newList;
				}
			}
		}
		return best;
	}

	private static int countWaysJumpStairs(int[] jumps, int target) {
		int[] result = new int[target];
		for (int i = -1; i < target; i++) {
			for (int jump : jumps) {
				if (i + jump < target) {
					result[i + jump] += i == -1 ? 1 : result[i];
				}
			}
		}
		return result[target - 1];
	}
}
