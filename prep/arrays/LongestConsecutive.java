package prep.arrays;

import java.util.HashSet;
import java.util.Set;

/**
 * Longest Consecutive Sequence — O(n) via HashSet + "start from sequence ends only".
 *
 * Key insights / interview points:
 *  - Dump everything into a Set for O(1) membership checks.
 *  - The "only start from canonical entry points" trick is what keeps it O(n):
 *    walk only from numbers that are sequence ENDS (no num+1 in the set),
 *    or equivalently sequence STARTS (no num-1 in the set). Either direction
 *    works — the invariant is that each number participates in exactly ONE walk.
 *  - Amortization argument: the nested while LOOKS O(n²), but across all outer
 *    iterations the total walking work is bounded by n (each number is visited
 *    by exactly one walk). Worth saying out loud in an interview.
 *  - Iterate the SET, not the input array, to skip duplicates without extra logic.
 *
 * Pattern transfer: the "only work from canonical entry points" idea shows up in
 *  - grid island-count problems (only enter from unvisited cells)
 *  - sweep-line interval problems (only process at event boundaries)
 *  - cycle/component problems where you want exactly one representative per group.
 */
public class LongestConsecutive {

    public static int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int n : nums) set.add(n);

        int best = 0;
        // Walk from each sequence END downward. (Mirror of the "start + walk up" variant.)
        for (int num : set) {
            if (!set.contains(num + 1)) {         // num is the end of its sequence
                int len = 0;
                for (int j = num; set.contains(j); j--) len++;
                best = Math.max(best, len);
            }
        }
        return best;
    }

    public static void main(String[] args) {
        System.out.println(longestConsecutive(new int[]{100, 4, 200, 1, 3, 2}));           // 4
        System.out.println(longestConsecutive(new int[]{0, 3, 7, 2, 5, 8, 4, 6, 0, 1}));   // 9
        System.out.println(longestConsecutive(new int[]{}));                                // 0
    }
}
