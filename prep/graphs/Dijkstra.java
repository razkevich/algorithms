package prep.graphs;

import java.util.*;

/**
 * Single-source shortest paths on a weighted directed graph with
 * NON-NEGATIVE edge weights. Returns map: node -> shortest distance from source.
 *
 * Adjacency format: graph.get(u) = list of int[]{v, weight} for each edge u -> v.
 *
 * Template — "lazy deletion" Dijkstra:
 *  1. Min-heap keyed by tentative distance.
 *  2. Pop (node, d). If node is already finalized, skip (stale entry).
 *  3. Otherwise finalize: the first pop of a node IS its shortest distance.
 *     Proof sketch: min-heap gives us the smallest tentative d across the
 *     frontier; with non-negative weights, no later path can be shorter.
 *  4. Push every neighbor with tentative distance d + w. Don't bother
 *     comparing against existing tentative distances — stale entries get
 *     filtered on pop. This is the "decrease-key-free" trick; the PQ can
 *     hold O(E) entries instead of O(V).
 *
 * Why no separate `visited` set: the result map IS the finalized set.
 * `result.containsKey(node)` answers "is this node done?".
 *
 * When Dijkstra breaks:
 *  - Negative edges → use Bellman-Ford (or SPFA). Dijkstra finalizes on first
 *    pop, so a cheaper-but-later path via a negative edge is missed.
 *  - Need shortest path TREE / actual path → track `parent[v] = u` whenever
 *    you'd push (v, d+w) and v is not yet finalized; then walk back from target.
 *
 * Pattern transfer:
 *  - Network Delay Time — run Dijkstra, return max over all finalized distances.
 *  - Cheapest Flights Within K Stops — Dijkstra variant with a stops budget in
 *    the PQ key, or Bellman-Ford relaxed K+1 times.
 *  - Path With Minimum Effort — grid Dijkstra with edge weight =
 *    |heightA - heightB| and relaxation = max(curEffort, edgeWeight) instead
 *    of sum. Same template, different combiner.
 *  - Swim in Rising Water — same min-of-max relaxation on a grid.
 *
 * Complexity: O((V + E) log V) with binary heap. Space O(V + E).
 *
 * Java API traps:
 *  - PriorityQueue: offer/poll/peek (NOT add/remove/element for the algorithmic flavor).
 *  - `Comparator.comparingInt(a -> a[1])` over `Comparator.comparing(a -> a[1])`
 *    to avoid autoboxing on every compare.
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
            dist.put(node, d);                      // first pop wins: this is the shortest

            for (int[] edge : graph.getOrDefault(node, List.of())) {
                int nei = edge[0], w = edge[1];
                if (!dist.containsKey(nei)) {
                    pq.offer(new int[]{nei, d + w});
                }
            }
        }
        return dist;
    }

    public static void main(String[] args) {
        // Graph:
        //   1 --4--> 2 --1--> 3
        //   1 --2--> 3 --3--> 4
        //   2 --5--> 4
        // Shortest from 1: {1:0, 2:4, 3:2, 4:5}
        Map<Integer, List<int[]>> graph = Map.of(
                1, List.of(new int[]{2, 4}, new int[]{3, 2}),
                2, List.of(new int[]{3, 1}, new int[]{4, 5}),
                3, List.of(new int[]{4, 3}),
                4, List.of()
        );
        System.out.println(dijkstra(graph, 1)); // {1=0, 2=4, 3=2, 4=5}
    }
}
