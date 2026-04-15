package prep.graphs;

import java.util.*;

/**
 * Iterative BFS on a directed graph represented as an adjacency list.
 *
 * Key insights / interview points:
 *  - Mark a node as visited AT ENQUEUE TIME, not at dequeue time. Otherwise
 *    the same node can be enqueued many times before processing, turning the
 *    algorithm quadratic (or worse).
 *  - BFS alone cannot distinguish "cycle" from "alternate path to the same
 *    node". In a DAG like 1->2, 1->3, 2->4, 3->4, node 4 is reached twice
 *    with no cycle. Use DFS 3-color or Kahn's for actual cycle detection.
 *  - Prefer `Deque<Integer> q = new ArrayDeque<>()` over LinkedList: better
 *    cache locality, no per-node allocation, and programming to the interface.
 *  - `visited.add(child)` does check + insert in one call (returns false if
 *    already present) — idiomatic Java for this pattern.
 */
public class BFSGraph {

    public static List<Integer> bfs(Map<Integer, List<Integer>> graph, int source) {
        List<Integer> result = new ArrayList<>();
        if (graph == null) return result;

        Deque<Integer> queue = new ArrayDeque<>();
        Set<Integer> visited = new HashSet<>();

        queue.offer(source);
        visited.add(source);

        while (!queue.isEmpty()) {
            int cur = queue.poll();
            result.add(cur);

            for (int child : graph.getOrDefault(cur, List.of())) {
                if (visited.add(child)) {   // returns false if already seen
                    queue.offer(child);
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Map<Integer, List<Integer>> graph = Map.of(
                1, List.of(2, 3),
                2, List.of(4),
                3, List.of(4, 5),
                4, List.of(),
                5, List.of()
        );
        System.out.println(bfs(graph, 1)); // [1, 2, 3, 4, 5]
    }
}
