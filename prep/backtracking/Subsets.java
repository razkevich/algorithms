package prep.backtracking;

import java.util.ArrayList;
import java.util.List;

/**
 * Subsets (power set) — functional / immutable variant of the backtracking template.
 *
 * Design choice here:
 *  - Immutable-copy style: each recursive call builds a NEW list when "choosing."
 *    No shared mutable state, so no explicit restore step is needed.
 *  - Equivalent to the canonical mutate-and-restore form:
 *        current.add(nums[i]);
 *        backtrack(i + 1, current);
 *        current.remove(current.size() - 1);
 *    Big-O identical (O(n · 2^n) — forced by the size of the output).
 *    Constant factor is higher because of per-call list copies.
 *
 * Why learn both forms:
 *  - Subsets' state (a growing list) is cheap to copy. Functional form is fine.
 *  - Backtracking problems with expensive state (grid for Word Search, board for
 *    N-Queens, 9×9 for Sudoku) REQUIRE mutate-and-restore — copying a grid per
 *    recursive call is infeasible. The "restore" step is what makes those tractable.
 *
 * Pattern transfer (by changing only the meaning of "choose" and when to record):
 *  - Subsets II (duplicates): sort + skip `if i > start && nums[i] == nums[i-1]`
 *  - Permutations: replace `start` with a `used[]` bitmap
 *  - Combinations (n choose k): record only when `current.size() == k`
 *  - Combination Sum: recurse with same `i` (not `i+1`) to allow repetition
 *  - N-Queens / Word Search / Sudoku: state is a grid; use mutate-and-restore
 */
public class Subsets {

    public static List<List<Integer>> subsets(int[] nums) {
        return subsets(nums, 0, new ArrayList<>());
    }

    private static List<List<Integer>> subsets(int[] nums, int start, List<Integer> soFar) {
        List<List<Integer>> result = new ArrayList<>();
        result.add(soFar);                          // record every node on the way
        for (int i = start; i < nums.length; i++) {
            List<Integer> next = new ArrayList<>(soFar);
            next.add(nums[i]);                      // "choose" via copy-then-extend
            result.addAll(subsets(nums, i + 1, next));
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(subsets(new int[]{1, 2, 3}));
        // [[], [1], [1, 2], [1, 2, 3], [1, 3], [2], [2, 3], [3]]  (8 = 2^3)

        System.out.println(subsets(new int[]{0}));
        // [[], [0]]
    }
}
