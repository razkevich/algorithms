package interviewcamp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Chapter12 {
	public static void main(String[] args) {
		System.out.println(maxDiff(new int[]{2, 3, 1, 4, 5, 7, 5, 4}));
		System.out.println(Arrays.deepToString(maxDiff2(new int[]{2, 3, 1, 4, 5, 7, 5, 4})));
		System.out.println(maxDiff3(new int[]{2, 3, 1, 4, 5, 7, 5, 4}));
		System.out.println(Arrays.toString(solve(new int[]{9, 9, 9, 9}, new int[]{9, 9, 9})));
		System.out.println((multiply(new int[]{9, 9, 9, 9}, new int[]{9, 9, 9})));
	}

	private static int[] solve(int[] a, int[] b) {
		a = resize(a, Math.max(a.length, b.length));
		b = resize(b, Math.max(a.length, b.length));
		int memory = 0;
		int intResult = 0;
		int[] result = new int[a.length + 1];
		for (int i = a.length - 1; i >= 0; i--) {
			int temp = a[i] + b[i] + memory;
			if (temp >= 10) {
				result[i + 1] = temp - 10;
				intResult += (temp - 10) * Math.pow(10, a.length - i - 1);
				memory = 1;
			} else {
				result[i + 1] = temp;
				intResult += (temp) * Math.pow(10, a.length - i - 1);
				memory = 0;
			}
		}
		result[0] = memory;
		intResult += memory * Math.pow(10, a.length);
		System.out.println("++++++++" + intResult);
		return result;
	}

	private static int multiply(int[] a, int[] b) {
		a = resize(a, Math.max(a.length, b.length));
		b = resize(b, Math.max(a.length, b.length));
		int memory = 0;
		int intResult = 0;
		for (int i = a.length - 1; i >= 0; i--) {
			int temp = a[i] * b[i] + memory;
			intResult += (temp % 10) * Math.pow(10, a.length - i - 1);
			memory = temp / 10;
		}
		intResult += memory * Math.pow(10, a.length);
		System.out.println("++++++++" + intResult);
		return intResult;
	}

	private static int[] resize(int[] a, int size) {
		int[] newA = new int[size];
		for (int i = size - 1; i >= 0; i--) {
			newA[i] = i - (size - a.length) >= 0 ? a[i - (size - a.length)] : 0;
		}
		return newA;
	}

	private static int maxDiff(int[] a) {
		int minSoFar = Integer.MAX_VALUE;
		int maxTrade = Integer.MIN_VALUE;
		for (int i = 0; i < a.length; i++) {
			if (a[i] < minSoFar) {
				minSoFar = a[i];
			}
			maxTrade = Math.max(maxTrade, a[i] - minSoFar);
		}
		return maxTrade;
	}

	private static Object[] maxDiff2(int[] a) {
		int minSoFar = Integer.MAX_VALUE;
		int minSoFarIdx = -1;
		int maxTrade = Integer.MIN_VALUE;
		Map<Integer, Integer[]> trades = new HashMap<>();
		for (int i = 0; i < a.length; i++) {
			if (a[i] < minSoFar) {
				minSoFar = a[i];
				minSoFarIdx = i;
			}
			trades.put(a[i] - minSoFar, new Integer[]{minSoFarIdx, i});
			maxTrade = Math.max(maxTrade, a[i] - minSoFar);
		}
		Integer[] value = trades.entrySet().stream().min(Map.Entry.comparingByKey()).get().getValue();
		int bestStart = value[0];
		int bestEnd = value[1];

		int secondBestStart = -1;
		int secondBestEnd = -1;
		for (Map.Entry<Integer, Integer[]> ii : trades.entrySet()) {
			if ((ii.getValue()[0] > bestStart && ii.getValue()[1] > bestEnd)
					|| (ii.getValue()[0] < bestStart && ii.getValue()[1] < bestEnd)) {
				secondBestStart = ii.getValue()[0];
				secondBestEnd = ii.getValue()[1];
			}
		}
		return new int[][]{new int[]{bestStart, bestEnd}, new int[]{secondBestStart, secondBestEnd}};
	}


	private static int maxDiff3(int[] a) {
		int[] bestToI = new int[a.length];
		int[] bestFromI = new int[a.length];
		int[] bestTwo = new int[a.length];
		for (int i = 0; i < a.length; i++) {
			bestToI[i] = maxDiff(Arrays.copyOfRange(a, 0, i + 1));
			bestFromI[i] = maxDiff(Arrays.copyOfRange(a, i + 1, a.length));
			bestTwo[i] = bestToI[i] + bestFromI[i];
		}
		return Arrays.stream(bestTwo).max().getAsInt();
	}

}
