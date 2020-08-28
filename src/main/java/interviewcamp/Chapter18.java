package interviewcamp;

import java.util.Arrays;

public class Chapter18 {
	public static void main(String[] args) {
		int[] a = {7, 50, 49, 15, 27, 91, 36, 53, 95, 43, 22, 88, 50, 14, 84, 29, 78, 34, 73, 52, 27, 65, 2, 86, 92, 44, 12, 68, 23, 77, 10, 54, 89,
				51, 50, 83, 38, 26, 86, 25, 88, 47, 71, 49, 62, 19, 75, 13, 11, 66, 40, 39, 61, 2, 73, 12, 6, 66, 72, 80, 19, 18, 33, 30, 69, 63, 33
				, 1, 35, 90, 44, 59, 86, 38, 47, 97, 60, 24, 11, 5, 71, 7, 36, 44, 28, 61, 10, 44, 33, 83, 12, 87, 19, 91, 95, 68, 44, 34, 70, 71};
		for (int i=0;i<100;i++)
		System.out.println(Arrays.toString(kOrderStat(a, a.length-1, 0, a.length)));
	}

	private static int[] kOrderStat(int[] a, int k, int low, int high) {
		if (high <= low) {
			return a;
		}
		int rand = low + (int) (Math.random() * (high - low));
		int randVal = a[rand];
		int i = low;
		int innerLow = low, innerHigh = high;
		while (i < innerHigh) {
			if (a[i] > randVal) {
				swap(a, i, innerHigh - 1);
				innerHigh--;
			} else if (a[i] < randVal) {
				swap(a, i, innerLow);
				innerLow++;
				i++;
			} else {
				i++;
			}
		}
		if (innerLow + 1 > k) {
			kOrderStat(a, k, low, innerLow);
		}
		if (innerHigh - 1 < k) {
			kOrderStat(a, k, innerHigh, high);
		}
		return a;
	}

	private static void swap(int[] a, int current, int lower) {
		int temp = a[current];
		a[current] = a[lower];
		a[lower] = temp;
	}
}
