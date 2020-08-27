package interviewcamp;

import java.util.Arrays;
import java.util.Random;

public class Chapter19 {
	public static void main(String[] args) {
		int[] a = {4, 5, 2, 6, 6, 23, 4, 56, 2, 23, 3, 5, 6};
		quickSort(a, 0, a.length - 1);
		System.out.println(Arrays.toString(a));
	}

	private static void quickSort(int[] a, int start, int end) {

		if (start < 0 || end >= a.length || end <= start) {
			return;

		}
		int rand = start + (new Random().nextInt(end - start + 1));
		int pivot = a[rand];

		int lower = start - 1, upper = end + 1, mid = start - 1;
		while (mid + 1 < end) {
			if (a[mid + 1] > pivot) {
				swap(a, upper - 1, mid + 1);
				upper--;
			} else if (a[mid + 1] == pivot) {
				mid++;
			} else {
				swap(a, mid + 1, lower + 1);
				mid++;
				lower++;
			}
		}
		quickSort(a, start, lower);
		quickSort(a, upper, end);
	}

	private static void swap(int[] a, int current, int lower) {
		int temp = a[current];
		a[current] = a[lower];
		a[lower] = temp;
	}

}
