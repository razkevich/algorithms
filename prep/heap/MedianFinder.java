package prep.heap;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Find Median from Data Stream — two heaps straddling the median.
 *
 * Heap roles:
 *  - `max` (max-heap) holds the LOWER half. Its root is the largest of the lower half.
 *  - `min` (min-heap) holds the UPPER half. Its root is the smallest of the upper half.
 *
 * Invariants maintained after every addNum:
 *  1. Ordering: every element in `max` ≤ every element in `min`.
 *  2. Balance: |min.size() - max.size()| ≤ 1.
 *
 * Each heap's root is always a median boundary, so findMedian is O(1).
 *
 * Key insights / interview points:
 *  - Two heaps turn "middle element of a growing set" into O(log n) add, O(1) query.
 *  - Pattern transfer: Sliding Window Median (same + lazy deletion of expired roots),
 *    IPO / capital-scheduling (affordable vs not-yet-affordable buckets), rolling-window analytics.
 *  - `Comparator.reverseOrder()` is the idiomatic max-heap comparator.
 *  - Subtle bug magnet: when growing one side, check `num` against the OTHER side's
 *    peek — the boundary that's relevant lives on the heap you're NOT currently adding to.
 *    Using the wrong peek creates an invariant break only when `num` lands in the gap
 *    between the two heap roots.
 *
 * Cleaner alternative — the "push-through" implementation (2 heaps, 3 lines):
 *    max.offer(num);                       // tentative lower
 *    min.offer(max.poll());                // push through to upper (enforces ordering)
 *    if (min.size() > max.size()) max.offer(min.poll());   // rebalance
 *  This class keeps the explicit case-by-case version because the user wrote it that
 *  way and it's more explicit about invariants, but both are equally correct.
 */
public class MedianFinder {

    private final PriorityQueue<Integer> min = new PriorityQueue<>();                      // upper half
    private final PriorityQueue<Integer> max = new PriorityQueue<>(Comparator.reverseOrder()); // lower half

    public void addNum(int num) {
        // seeding: first element goes to upper half
        if (min.size() == 0) {
            min.offer(num);
            return;
        }

        // seeding: place second element correctly relative to the first
        if (max.size() == 0) {
            if (num <= min.peek()) {
                max.offer(num);
            } else {
                max.offer(min.poll());
                min.offer(num);
            }
            return;
        }

        // equal sizes — grow whichever side num belongs to
        if (min.size() == max.size()) {
            if (num > max.peek()) min.offer(num);
            else                 max.offer(num);
        }
        // upper half is heavier — need to grow lower (max).
        // direct-place iff num fits in lower half, i.e. num <= min.peek().
        else if (min.size() > max.size()) {
            if (num > min.peek()) {
                int old = min.poll();
                min.offer(num);
                max.offer(old);
            } else {
                max.offer(num);
            }
        }
        // lower half is heavier — need to grow upper (min).
        // direct-place iff num fits in upper half, i.e. num > max.peek().
        else {
            if (num > max.peek()) {
                min.offer(num);
            } else {
                int old = max.poll();
                max.offer(num);
                min.offer(old);
            }
        }
    }

    public double findMedian() {
        if (min.size() == 0 && max.size() == 0) {
            throw new IllegalStateException("stream empty");
        }
        if (min.size() == max.size()) {
            return (max.peek() + min.peek()) / 2.0;
        }
        return min.size() > max.size() ? min.peek() : max.peek();
    }

    public static void main(String[] args) {
        MedianFinder mf = new MedianFinder();
        mf.addNum(1);  System.out.println(mf.findMedian());   // 1.0
        mf.addNum(2);  System.out.println(mf.findMedian());   // 1.5
        mf.addNum(3);  System.out.println(mf.findMedian());   // 2.0
        mf.addNum(4);  System.out.println(mf.findMedian());   // 2.5

        // Regression case for the min>max branch:
        // Previously with `num > max.peek()` the invariant broke when num landed in the gap.
        MedianFinder mf2 = new MedianFinder();
        mf2.addNum(10); mf2.addNum(20); mf2.addNum(5);       // state: min={10,20}, max={5}
        mf2.addNum(8);                                        // num=8 is in the gap
        System.out.println(mf2.findMedian());                 // should print 9.0 (median of 5,8,10,20)
    }
}
