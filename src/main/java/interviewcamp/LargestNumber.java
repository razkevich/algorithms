package interviewcamp;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

public class LargestNumber {
	public static void main(String[] args) {
		System.out.println(new LargestNumber().largestNumber(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0}));
	}

	public String largestNumber(int[] nums2) {
		String[] nums = new String[nums2.length];
		for (int i = 0; i < nums.length; i++) {
			nums[i] = String.valueOf(nums2[i]);
		}
		Arrays.sort(nums, (a, b) -> (b + a).compareTo(a + b));
		String join = String.join("", nums);
		if (join.equals("00")) return "0";
		return join;
	}
}
