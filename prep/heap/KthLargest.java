package prep.heap;

import java.util.PriorityQueue;

/**
 * Kth Largest Element — the canonical "min-heap of size K" template.
 *
 * Key insights / interview points:
 *  - To track the K LARGEST, keep a MIN-heap of size K. The heap's root is
 *    the "weakest admitted member" — the threshold you must beat to get in.
 *  - Symmetric rule: top-K SMALLEST → MAX-heap of size K.
 *    The heap's root is always what you want to evict.
 *  - After the sweep, the heap holds exactly the top-K elements, and its
 *    root is the Kth extremum (Kth largest here).
 *  - Complexity: O(n log K). Each of n elements triggers at most one
 *    O(log K) heap op. Beats O(n log n) full sort when K ≪ n.
 *  - Quickselect alternative: O(n) average, O(n²) worst, mutates input,
 *    only works on in-memory arrays. Heap is the right choice when the
 *    input is streaming OR you need the K elements preserved.
 *
 * Java API cheat-sheet (to stop tripping on these):
 *  - Queue / PriorityQueue: peek() / poll() / offer()
 *    (null/false-returning siblings of element() / remove() / add())
 *  - Deque-as-stack:        push() / pop() / peek()
 *  - Never use `new Queue<>()` — interface. Instantiate ArrayDeque or PQ.
 *
 * Pattern transfer:
 *  - Top K Frequent Elements — HashMap count + min-heap of size K keyed by freq
 *  - K Closest Points to Origin — MAX-heap of size K keyed by distance
 *    (evict the farthest when a closer point arrives)
 *  - Kth Largest in Stream (design) — same min-heap, preserved across add() calls
 *  - Merge K Sorted Lists — different pattern: heap of K list HEADS, pop min
 *    and push the popped node's successor.
 */
public class KthLargest {

    public static int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> heap = new PriorityQueue<>();   // default = min-heap

        for (int n : nums) {
            if (heap.size() < k) {
                heap.offer(n);
            } else if (n > heap.peek()) {    // beats the weakest admitted — swap it in
                heap.poll();
                heap.offer(n);
            }
        }
        return heap.peek();   // root of a min-heap sized K = the Kth largest overall
    }

    public static void main(String[] args) {
        System.out.println(findKthLargest(new int[]{3, 2, 1, 5, 6, 4}, 2));            // 5
        System.out.println(findKthLargest(new int[]{3, 2, 3, 1, 2, 4, 5, 5, 6}, 4));   // 4
        System.out.println(findKthLargest(new int[]{1}, 1));                           // 1
    }
}
