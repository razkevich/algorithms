package prep.graphs;

import java.util.*;

/*
 * Dijkstra — single-source shortest paths, non-negative weights.
 *
 * Pattern: **BFS with a priority queue instead of a FIFO queue**.
 * BFS guarantees shortest hop-count because the queue is sorted by insertion
 * order (= hops). Dijkstra generalizes: sort the frontier by cumulative edge
 * weight, and the first time a node pops you've got its minimum distance.
 * (Proof sketch: any later path to that node goes through some *other* node
 *  already in the PQ at equal-or-greater distance + a non-negative edge →
 *  can't be shorter. Breaks if edges can be negative — use Bellman-Ford.)
 *
 * Variant used here: **lazy Dijkstra**.
 *   - No decrease-key. Instead, push a fresh (node, dist) entry every time
 *     we find an improvement, and on pop skip nodes already finalized.
 *   - PQ may hold stale duplicates; that's fine — they get filtered on pop.
 *   - O((V+E) log E). The alternative (eager: separate tentative-dist map
 *     + indexed heap) is O((V+E) log V) but uglier in Java without a library.
 *
 * Invariants:
 *   - `dist` holds FINAL distances only (set once, never updated).
 *   - PQ entries are tentative; the first pop of a node wins.
 *
 * Graph shape: Map<node, List<int[]>> where each int[] = {neighbor, weight}.
 *
 * Transfer / family:
 *   - LC 743  Network Delay Time         — vanilla Dijkstra from a source.
 *   - LC 787  Cheapest Flights ≤ K Stops — add a "stops" dimension to the state.
 *   - LC 1631 Path With Minimum Effort   — cost = max(edge) along path (not sum);
 *                                          same template, different relax rule.
 *   - LC 778  Swim in Rising Water       — same as above with grid edges.
 *   - 0/1 weights → BFS on a deque (push-front for 0, push-back for 1) is faster.
 *   - Negative weights → Bellman-Ford; negative cycles → detect with V-th relax pass.
 *
 * Java API traps:
 *   - `Comparator.comparingInt` over `Comparator.comparing` on int[] — avoids boxing.
 *   - `getOrDefault(key, List.of())` over null-checks or external utility libs.
 *   - PriorityQueue: offer/poll/peek (not add/remove/element).
 */
public class Dijkstra {

    public static Map<Integer, Integer> dijkstra(Map<Integer, List<int[]>> graph, int source) {
        Map<Integer, Integer> dist = new HashMap<>();
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[]{source, 0});

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int node = cur[0], d = cur[1];
            if (dist.containsKey(node)) continue;   // stale entry — already finalized
            dist.put(node, d);

            for (int[] nei : graph.getOrDefault(node, List.of())) {
                int next = nei[0], w = nei[1];
                if (dist.containsKey(next)) continue; // small optimization: don't push finalized
                pq.offer(new int[]{next, d + w});
            }
        }
        return dist;
    }
}
