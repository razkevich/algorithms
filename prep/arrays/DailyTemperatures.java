package prep.arrays;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Daily Temperatures — the cleanest intro to the monotonic stack pattern.
 *
 * For each day i, return the number of days to wait for a strictly warmer
 * temperature. If none, 0.
 *
 * Key insights / interview points:
 *  - Maintain a DECREASING stack of INDICES. When a warmer day walks in,
 *    pop every index whose temperature is strictly less — that incoming
 *    day is their "next warmer."
 *  - Store INDICES, not values. Distances fall out as (i - popped).
 *  - Unanswered indices are exactly the ones still on the stack at the end.
 *    Default-zero int[] init handles them for free — no drain pass needed.
 *  - Amortization: each index pushed once, popped at most once → O(n) total,
 *    despite the nested while.
 *  - Java trap: Deque is an interface. Instantiate with `new ArrayDeque<>()`.
 *
 * Direction cheat-sheet for the monotonic-stack family:
 *  - Next GREATER to the right → decreasing stack, pop on incoming > top
 *  - Next SMALLER to the right → increasing stack, pop on incoming < top
 *  - Info from the LEFT → walk right-to-left, same rules
 *  - First smaller on BOTH sides (histogram, subarray min sum) → one
 *    increasing stack; on pop, the element now on top is the left-smaller,
 *    the current i is the right-smaller.
 *
 * Pattern transfer:
 *  - Next Greater Element I / II (II uses a 2n iteration to fake circularity)
 *  - Largest Rectangle in Histogram — same code shape, increasing stack,
 *    compute width from both neighbors on pop
 *  - Sum of Subarray Minimums — contribution technique: each element is
 *    the min over (leftSmaller, rightSmaller); count subarrays it dominates
 *  - Trapping Rain Water — monotonic-stack variant as alternative to
 *    prefix-max/suffix-max
 */
public class DailyTemperatures {

    public static int[] dailyTemperatures(int[] t) {
        int n = t.length;
        int[] result = new int[n];                     // default 0 handles unanswered days
        Deque<Integer> stack = new ArrayDeque<>();     // decreasing stack of indices

        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && t[stack.peek()] < t[i]) {
                int popped = stack.pop();
                result[popped] = i - popped;
            }
            stack.push(i);
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(java.util.Arrays.toString(
            dailyTemperatures(new int[]{73, 74, 75, 71, 69, 72, 76, 73})));
        // [1, 1, 4, 2, 1, 1, 0, 0]
    }
}
