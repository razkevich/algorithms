package prep.binary_search;

import java.util.Arrays;

/**
 * Koko Eating Bananas (LC 875) — binary search on the ANSWER.
 *
 * The template this teaches:
 *  - You're NOT searching a sorted array for a value. You're searching a
 *    range of candidate answers [lo, hi] for the smallest one that
 *    satisfies a MONOTONE yes/no predicate.
 *  - The "sorted-ness" lives in the predicate: as k increases, "can Koko
 *    finish in h hours?" goes N N N Y Y Y Y. Binary search finds the flip.
 *
 * The mental model that prevents bugs:
 *  - Predicate is a THRESHOLD (`cur <= h`), not an EQUALITY (`cur == h`).
 *    For many (k, h) combos no k hits h exactly — the answer is the
 *    smallest k whose hour-count is ≤ h.
 *  - Two branches, not three:
 *      passes  → right = mid     (maybe a smaller k also passes)
 *      fails   → left  = mid + 1 (need faster)
 *    Loop `while (left < right)`, return `left`. No "found it" sentinel.
 *
 * Bounds:
 *  - lo = 1 (Koko must eat at least 1/hour).
 *  - hi = max(piles). At this speed every pile takes exactly 1 hour, so
 *    total = piles.length, the absolute min possible. Speeds above max
 *    can never beat that — so max is the tight upper bound.
 *    (sum(piles) works too, but is O(n) times larger for no gain.)
 *
 * Small Java traps:
 *  - Integer ceiling of `p / k` without doubles: `(p + k - 1) / k`.
 *    Faster and no float-precision concerns vs `Math.ceil((double)p/k)`.
 *  - `(left + right) / 2` can overflow near Integer.MAX_VALUE.
 *    Prefer `left + (right - left) / 2`.
 *
 * Pattern transfer — same template, different predicate:
 *  - Capacity to Ship Packages in D Days — x = capacity;
 *    predicate: "can we ship within D days at this capacity?"
 *  - Split Array Largest Sum — x = max-allowed-subarray-sum;
 *    predicate: "can we partition into ≤ m parts?"
 *  - Minimum Days to Make Bouquets — x = day;
 *    predicate: "by day x, can we make m bouquets?"
 *  - Aggressive Cows / Magnetic Force — x = minimum spacing;
 *    predicate: "can we place all k cows with gap ≥ x?"
 *  - Median of Two Sorted Arrays (advanced variant).
 *
 * Complexity: O(n · log(max(piles))).
 */
public class KokoEatingBananas {

    public int minEatingSpeed(int[] piles, int h) {
        int left = 1;
        int right = Arrays.stream(piles).max().getAsInt();

        while (left < right) {
            int mid = left + (right - left) / 2;
            int cur = howManyHours(piles, mid);
            if (cur > h) {
                // too slow — need faster (bigger k)
                left = mid + 1;
            } else {
                // cur <= h: passes; maybe a smaller k also passes
                right = mid;
            }
        }
        return left;
    }

    int howManyHours(int[] piles, int k) {
        int h = 0;
        for (int pile : piles) {
            h += Math.ceil((double) pile / (double) k);
            // idiomatic int-only alternative: h += (pile + k - 1) / k;
        }
        return h;
    }

    public static void main(String[] args) {
        KokoEatingBananas s = new KokoEatingBananas();
        System.out.println(s.minEatingSpeed(new int[]{3, 6, 7, 11}, 8));          // 4
        System.out.println(s.minEatingSpeed(new int[]{30, 11, 23, 4, 20}, 5));    // 30
        System.out.println(s.minEatingSpeed(new int[]{30, 11, 23, 4, 20}, 6));    // 23
        System.out.println(s.minEatingSpeed(new int[]{312884470}, 968709470));    // 1
    }
}
