package interviewcamp;

import java.util.Arrays;

public class DutchNationalFlag {

	public static void main(String[] args) {
		System.out.println(Arrays.toString(dnf(new int[]{1, 3, 5, 7, 4, 6, 5, 3, 2, 6, 7, 7, 3, 3, 2, 34, 5}, 4)));
	}


	public static int[] dnf(int[] nums, int pivot) {
		int start = 0, end = nums.length - 1, i = 0;

		while (i <= end) {
			if (nums[i] < pivot) {
				swap(nums, i, start);
				i++;
				start++;
			} else if (nums[i] > pivot) {
				swap(nums, i, end);
				end--;
			} else if (nums[i] == pivot) {
				i++;
			}
		}
		return nums;
	}


	private static void swap(int[] a, int i, int j) {
		int temp = a[j];
		a[j] = a[i];
		a[i] = temp;
	}
}
