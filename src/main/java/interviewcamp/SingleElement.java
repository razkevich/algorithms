package interviewcamp;

class SingleElement {
	public static void main(String[] args) {
		System.out.println(new SingleElement().singleNonDuplicate(new int[]{1}));
	}

	public int singleNonDuplicate(int[] nums) {
		int start = 0, end = nums.length - 1;

		while (start <= end) {

			int mid = start + (end - start) / 2;
			if ((mid + 1 > nums.length - 1 || nums[mid] != nums[mid + 1]) &&
					(mid - 1 < 0 || nums[mid] != nums[mid - 1])) {
				return nums[mid];
			}
			if (((mid + 1) % 2 == 0 && (nums[mid] != nums[mid - 1]))
					|| ((mid + 1) % 2 == 1 && (nums[mid] == nums[mid - 1]))) {
				end = mid ;
			} else {
				start = mid ;
			}
		}
		return -1;
	}

}
