package interviewcamp;

public class Chapter11 {
	public static void main(String[] args) {
		System.out.println(countWaysJumpStairs(new int[]{1, 3, 5}, 5));
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
