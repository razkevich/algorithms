package interviewcamp;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

class SlidingWindowMedian {

	public static void main(String[] args) {
		System.out.println(Arrays.toString(new SlidingWindowMedian().medianSlidingWindow(new int[]{7, 0, 3, 9, 9, 9, 1, 7, 2, 3}, 6)));
	}

	public double[] medianSlidingWindow(int[] nums, int k) {
		double[] result = new double[nums.length - k + 1];
		TwoHeaps twoHeaps = new TwoHeaps(k);
		int j = 0;
		for (int i = 0; i < nums.length; i++) {
			if (i > k - 1) {
				twoHeaps.remove(nums[i - k]);
//				result[j] = twoHeaps.getMedian();
				j++;
			}
			twoHeaps.add(nums[i]);
			if (i >= k - 1) {
				result[j] = twoHeaps.getMedian();
			}
		}
		return result;
	}


	class TwoHeaps {
		int capacity;
		PriorityQueue<Integer> low = new PriorityQueue<>(Comparator.reverseOrder());
		PriorityQueue<Integer> high = new PriorityQueue<>();

		public TwoHeaps(int capacity) {
			this.capacity = capacity;
		}

		public void add(int val) {
			if (high.isEmpty()) {
				high.add(val);
			} else if (high.size() == low.size()) {
				if (val < low.peek()) {
					high.add(low.remove());
					low.add(val);
				} else {
					high.add(val);
				}
			} else {
				if (val > high.peek()) {
					low.add(high.remove());
					high.add(val);
				} else {
					low.add(val);
				}
			}
		}

		public void remove(int val) {
			if (!high.remove(val))
				low.remove(val);
			if (low.size()>high.size()){
				high.add(low.remove());
			}
		}

		public double getMedian() {
			if (high.size() > low.size()) {
				return high.peek();
			} else {
				return (high.peek().doubleValue() + low.peek().doubleValue()) / 2;
			}
		}

	}
}
