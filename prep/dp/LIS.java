package prep.dp;

/**
 * Longest Increasing Subsequence (LC 300).
 *
 * WHY THIS PROBLEM: it has two canonical solutions that teach different things.
 *   - O(n²) DP: the "every suffix is a subproblem" template (transfers to LCS, Coin Change shape).
 *   - O(n log n) patience sort: a derived-structure trick whose *length* is the answer even though
 *     the structure itself is NOT a valid LIS. That duality is the real lesson — same trick shows
 *     up in "number of LIS" and various binary-search-on-derived-state problems.
 *
 * ── O(n²) DP (this file's primary solution) ──────────────────────────────────────────────────
 * Suffix formulation: lis[i] = length of the longest increasing subsequence STARTING at index i.
 *   lis[i] = 1 + max(lis[j])  for all j > i with nums[j] > nums[i],  else 1.
 * Iterate i from right to left so lis[j] is ready when we compute lis[i]. Answer = max(lis).
 *
 * The equivalent prefix formulation (lis[i] = LIS ENDING at i, iterate left-to-right) is more
 * common in textbooks but produces the same recurrence — pick whichever matches your instinct.
 *
 * ── O(n log n) patience sort (canonical alternative) ─────────────────────────────────────────
 * Maintain tails[], where tails[k] = smallest possible tail of any increasing subsequence of
 * length k+1 seen so far. For each num:
 *   - binary-search the leftmost index idx where tails[idx] >= num
 *   - if found: tails[idx] = num  (we improved the best tail for that length)
 *   - if not:   tails.append(num) (we extended the longest length by 1)
 * Answer = tails.size().
 *
 * CRITICAL INVARIANT: tails is NOT the LIS. Example: [3,1,4,1,5,9,2,6]
 *   tails evolves to [1,2,6,9] (length 4, correct) but [1,2,6,9] isn't a subsequence of the input
 *   in that order. Only the LENGTH is meaningful. Don't reconstruct the LIS from tails.
 *
 * Binary search: this is the "first >= target" flavor (not first >). For strictly increasing LIS
 * we want to replace equal-or-greater; for non-decreasing (≤) we'd want first > target instead —
 * that one-character difference flips the problem.
 */
public class LIS {

    // O(n²) DP — suffix formulation, right-to-left.
    public static int lengthOfLIS(int[] nums) {
        int n = nums.length;
        int[] lis = new int[n];
        int best = 0;
        for (int i = n - 1; i >= 0; i--) {
            lis[i] = 1;
            for (int j = i + 1; j < n; j++) {
                if (nums[j] > nums[i]) {
                    lis[i] = Math.max(lis[i], 1 + lis[j]);
                }
            }
            best = Math.max(best, lis[i]);
        }
        return best;
    }

    // O(n log n) patience sort — canonical alternative. tails[k] = smallest tail of LIS of length k+1.
    public static int lengthOfLISPatience(int[] nums) {
        int[] tails = new int[nums.length];
        int size = 0;
        for (int num : nums) {
            int lo = 0, hi = size;
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (tails[mid] < num) lo = mid + 1;
                else hi = mid;
            }
            tails[lo] = num;
            if (lo == size) size++;
        }
        return size;
    }

    public static void main(String[] args) {
        int[] a = {10, 9, 2, 5, 3, 7, 101, 18};           // LIS = [2,3,7,101] or [2,3,7,18], length 4
        System.out.println(lengthOfLIS(a));               // 4
        System.out.println(lengthOfLISPatience(a));       // 4

        int[] b = {0, 1, 0, 3, 2, 3};                     // LIS = [0,1,2,3], length 4
        System.out.println(lengthOfLIS(b));               // 4
        System.out.println(lengthOfLISPatience(b));       // 4
    }
}
