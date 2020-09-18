package interviewcamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FourSum {
	public static void main(String[] args) {
		System.out.println(new FourSum().fourSum(new int[]{1, 0, -1, 0, -2, 2}, 0));
	}

	public List<List<Integer>> fourSum(int[] nums, int target) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		List<List<Integer>> result = new ArrayList<>();
		for (int i = 0; i < nums.length; i++) {
			List<List<Integer>> c = new ArrayList<>(threeSum(nums, target - nums[i], i+1));
			int j = i;
			c.forEach(a -> a.add(nums[j]));
			result.addAll(c);
		}
		return result;
	}

	public List<List<Integer>> threeSum(int[] nums, int target, int from) {
		List<List<Integer>> result = new ArrayList<>();
		for (int i = from; i < nums.length; i++) {
			List<List<Integer>> c = new ArrayList<>(twoSum(nums, target - nums[i], i+1));
			int j = i;
			c.forEach(a -> a.add(nums[j]));
			result.addAll(c);
		}
		return result;
	}

	public List<List<Integer>> twoSum(int[] nums, int target, int from) {
		HashMap<Integer, Integer> ints = new HashMap<>();
		List<List<Integer>> result = new ArrayList<>();
		for (int i = from; i < nums.length; i++) {
			int aa = nums[i];

			if (ints.containsKey(aa)) {
				result.add(new ArrayList<>(List.of(aa, ints.get(aa))));
			}
			ints.put(target - aa, aa);
		}
		return result;
	}
}
