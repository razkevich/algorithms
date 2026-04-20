package prep.intervals;

import java.util.*;

/**
 * Merge Intervals via SWEEP-LINE with events.
 *
 * Not the idiomatic solution for Merge Intervals — sort-by-start + single pass
 * is simpler. But this sweep-line template is the RIGHT tool for problems
 * where what matters is the STATE at each point in time:
 *  - Meeting Rooms II (max concurrent = peak `active`)
 *  - Skyline problem
 *  - Car Pooling, "how many active at time t"
 *  - Range counting / time-weighted concurrency
 *
 * Algorithm:
 *  1. For each interval, emit two events: (start, START), (end, END).
 *  2. Sort events by value, tie-breaking START BEFORE END. This tie-break
 *     is what keeps touching endpoints like [1,4] and [4,5] merging into
 *     [1,5] rather than splitting. Without it, END at 4 closes the interval
 *     before START at 4 reopens it.
 *  3. Sweep events in order, tracking an `active` counter.
 *      - On START: if active was 0, we are opening a NEW merged interval;
 *        record the start. Then increment active.
 *      - On END: decrement active first. If it just dropped to 0, the
 *        current merged interval has ended; record the end.
 *
 * Complexity: O(n log n) for the PQ + sort, O(n) for the sweep.
 *
 * Gotcha list:
 *  - Tie-break START-before-END is mandatory for merge semantics.
 *  - Count opens/closes separately; don't try to close on every END (nested
 *    intervals would close too early).
 *  - The canonical sort-by-start + sweep variant is simpler for this exact
 *    problem; keep both patterns in your toolkit.
 */
public class MergeIntervalsSweepLine {

    private enum Kind { START, END }

    private record Event(int val, Kind kind) {}

    public static List<List<Integer>> merge(int[][] intervals) {
        PriorityQueue<Event> pq = new PriorityQueue<>(
            // primary: by value; tie-break: START before END
            Comparator.<Event>comparingInt(e -> e.val)
                      .thenComparing(e -> e.kind)   // START (ordinal 0) < END (ordinal 1)
        );

        for (int[] i : intervals) {
            pq.offer(new Event(i[0], Kind.START));
            pq.offer(new Event(i[1], Kind.END));
        }

        List<List<Integer>> result = new ArrayList<>();
        int active = 0;

        while (!pq.isEmpty()) {
            Event e = pq.poll();
            if (e.kind == Kind.START) {
                if (active == 0) {                     // opening a NEW merged interval
                    result.add(new ArrayList<>(List.of(e.val)));
                }
                active++;
            } else {                                   // END
                active--;
                if (active == 0) {                     // this END closes the current merged interval
                    result.get(result.size() - 1).add(e.val);
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(merge(new int[][]{{1,3},{2,6},{8,10},{15,18}}));
        // [[1, 6], [8, 10], [15, 18]]

        System.out.println(merge(new int[][]{{1,4},{4,5}}));
        // [[1, 5]]  — touching endpoints must merge; depends on START-before-END tie-break

        System.out.println(merge(new int[][]{{1,4},{2,3}}));
        // [[1, 4]]  — contained; end does not shrink

        System.out.println(merge(new int[][]{{1,4},{0,4}}));
        // [[0, 4]]
    }
}
