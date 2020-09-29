package interviewcamp;

import java.util.Arrays;
import java.util.Random;

public class QuickSort {

	public static void main(String[] args) {
		int[] nums = {1, 3, 5, 7, 4, 6, 5, 3, 2, 6, 7, 7, 3, 3, 2, 34, 5};
		quickSort(nums, 0, nums.length - 1);
		System.out.println(Arrays.toString(nums));
	}

	public static void quickSort(int[] nums, int start, int end) {
		if (start < 0 || end >= nums.length || start >= end) {
			return;
		}
		int pivot = start + (new Random().nextInt(end - start));
		int[] points = dnf(nums, pivot, start, end);
		quickSort(nums, start, points[0]);
		quickSort(nums, points[1], end);

	}

	public static int[] dnf(int[] nums, int pivot, int start, int end) {
		start--;
		int i = start;
		end++;

		while (i + 1 < end) {
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
		return new int[]{start, end};
	}


	private static void swap(int[] a, int i, int j) {
		int temp = a[j];
		a[j] = a[i];
		a[i] = temp;
	}
}
