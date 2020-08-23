package main.java;

import java.util.Random;

public class Main2 {

	public static void main(String[] args) {
		System.out.println(selectionAlgorithm(new int[]{5, 7, 4, 6, 5, 3, 3}, 0, 6, 0));
		System.out.println(selectionAlgorithm(new int[]{5, 7, 4, 6, 5, 3, 3}, 0, 6, 1));
		System.out.println(selectionAlgorithm(new int[]{5, 7, 4, 6, 5, 3, 3}, 0, 6, 2));
		System.out.println(selectionAlgorithm(new int[]{5, 7, 4, 6, 5, 3, 3}, 0, 6, 3));
		System.out.println(selectionAlgorithm(new int[]{5, 7, 4, 6, 5, 3, 3}, 0, 6, 4));
		System.out.println(selectionAlgorithm(new int[]{5, 7, 4, 6, 5, 3, 3}, 0, 6, 5));
		System.out.println(selectionAlgorithm(new int[]{5, 7, 4, 6, 5, 3, 3}, 0, 6, 6));
//		System.out.println(selectionAlgorithm(new int[]{5, 7, 4, 6, 5, 3, 3}, 0, 6, 7));
	}

	// 3 3 4 5 5 6 7

	public static int selectionAlgorithm(int[] a, int start, int end, int targetIndex) {
		int result = singlePlacementPartition(a, start, end);

		if (result == targetIndex) {
			return a[result];
		}
		if (result > targetIndex) {
			return selectionAlgorithm(a, start, result - 1, targetIndex);
		}
		if (result < targetIndex) {
			return selectionAlgorithm(a, result + 1, end, targetIndex);
		}

		return -1;
	}

	private static int singlePlacementPartition(int[] a, int start, int end) {
		int randomIndex = new Random().nextInt(end - start + 1) + start;
		int less = start;
		swap(a, randomIndex, start);
		for (int i = start + 1; i <= end; i++) {
			if (a[i] <= a[start]) {
				swap(a, i, less + 1);
				less++;
			}
		}
		swap(a, less, 0);
		return less;
	}


	private static void swap(int[] a, int i, int j) {
		int temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}


}
