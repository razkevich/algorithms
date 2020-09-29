package interviewcamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LongestIncreasingSubseq {

	public static void main(String[] args) {
		ArrayList<Integer> soFar = new ArrayList<>();
		System.out.println(longestIncreasingSubseq(List.of(1, 3, 2, 5, 3, 5, 6), soFar));
	}

	private static List<Integer> longestIncreasingSubseq(List<Integer> ints, List<Integer> soFar) {
		if (ints.isEmpty() || (!soFar.isEmpty() && ints.stream().mapToInt(a -> a).max().orElse(Integer.MAX_VALUE) <= soFar.get(soFar.size() - 1))) {
			return soFar;
		}
		List<Integer> longestSubseq = new ArrayList<>();
		for (int j = 0; j < ints.size(); j++) {
			int i = ints.get(j);
			if (soFar.isEmpty() || i > soFar.get(soFar.size() - 1)) {
				List<Integer> newSoFar = new ArrayList<>(soFar);
				newSoFar.add(i);
				List<Integer> recurse = longestIncreasingSubseq(ints.subList(j, ints.size()), newSoFar);
				if (recurse.size() > longestSubseq.size()) {
					longestSubseq = recurse;
				}
			}
		}
		return longestSubseq;
	}
}