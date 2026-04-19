package prep.arrays;

/**
 * Product of Array Except Self — O(n) time, O(1) extra space, no division.
 *
 * Key insights / interview points:
 *  - For each i, answer[i] = (product of everything LEFT of i)
 *                          * (product of everything RIGHT of i).
 *    Two independent sweeps compute those two halves.
 *  - The "no division" constraint is the whole point: total/nums[i] fails
 *    on zeros and is often banned outright. Prefix/suffix sidesteps it.
 *  - The O(1)-extra trick: you don't need two scratch arrays. Pass 1
 *    writes prefix products directly into the output; pass 2 walks
 *    right-to-left with a scalar suffix and multiplies in place.
 *    Output array doesn't count toward extra space.
 *
 * Pattern transfer:
 *  - Trapping Rain Water — prefix max + suffix max, same "two sweeps,
 *    collapse to O(1) extra by reusing the output" idiom.
 *  - Subarray sum / product queries — prefix arrays for O(1) lookup.
 *  - Candy distribution — two passes with local constraints.
 *  - Any "for each i, something about everything-except-i" problem.
 */
public class ProductExceptSelf {

    public static int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];

        // Pass 1: result[i] = product of everything LEFT of i.
        int prefix = 1;
        for (int i = 0; i < n; i++) {
            result[i] = prefix;
            prefix *= nums[i];
        }

        // Pass 2: fold in the running suffix right-to-left.
        // After this, result[i] = (left product) * (right product).
        int suffix = 1;
        for (int i = n - 1; i >= 0; i--) {
            result[i] *= suffix;
            suffix *= nums[i];
        }

        return result;
    }

    public static void main(String[] args) {
        System.out.println(java.util.Arrays.toString(
            productExceptSelf(new int[]{1, 2, 3, 4})));          // [24, 12, 8, 6]
        System.out.println(java.util.Arrays.toString(
            productExceptSelf(new int[]{-1, 1, 0, -3, 3})));     // [0, 0, 9, 0, 0]
    }
}
