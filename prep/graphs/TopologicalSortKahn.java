package prep.graphs;

import java.util.*;

/**
 * Topological sort via Kahn's BFS algorithm.
 *
 * Key insights / interview points:
 *  - Works on DAGs. If the graph has a cycle, result.size() < indegree.size().
 *  - Mental model: edge u → v = "u must precede v".
 *    Indegree = "number of prereqs left". A node is READY when indegree hits 0.
 *    Processing a node satisfies a prereq for each dependent → decrement.
 *  - Cycle detection is free: any node stuck in a cycle never drains to 0,
 *    so it never enters the queue and never makes it into result.
 *  - Complexity: O(V + E). Each edge is walked once while computing indegrees
 *    and once more during the BFS drain.
 *
 * Kahn's vs DFS topo sort:
 *  - Kahn's: BFS, iterative, natural level-by-level order (great for
 *    build graphs, course plans, anything with "same-depth" semantics).
 *  - DFS topo: recursive post-order, push on exit, reverse the list. Shorter
 *    to code; cycle detection reuses the 3-color trick. Order is depth-first.
 *  - Interviewers accept either — mention you know both.
 *
 * Pattern transfer: Course Schedule I/II, Alien Dictionary,
 * spreadsheet recalc, build-system ordering, package install resolution.
 */
public class TopologicalSortKahn {

    public static List<Integer> topoSort(Map<Integer, List<Integer>> graph) {
        // 1. Compute indegree for every node in O(V + E).
        //    Walk each edge once: for (u → v), bump indegree[v].
        Map<Integer, Integer> indegree = new HashMap<>();
        for (int u : graph.keySet()) {
            indegree.putIfAbsent(u, 0);
            for (int v : graph.get(u)) {
                indegree.merge(v, 1, Integer::sum);
            }
        }

        // 2. Seed the queue with every indegree-0 node.
        Deque<Integer> q = new ArrayDeque<>();
        for (var e : indegree.entrySet()) {
            if (e.getValue() == 0) q.offer(e.getKey());
        }

        // 3. BFS drain: pop, record, decrement dependents, enqueue newly-ready.
        List<Integer> result = new ArrayList<>();
        while (!q.isEmpty()) {
            int cur = q.poll();
            result.add(cur);
            for (int nei : graph.getOrDefault(cur, List.of())) {
                indegree.merge(nei, -1, Integer::sum);
                if (indegree.get(nei) == 0) q.offer(nei);
            }
        }

        // 4. Anything undrained is in a cycle.
        if (result.size() < indegree.size()) {
            throw new IllegalStateException("Graph has a cycle");
        }
        return result;
    }

    public static void main(String[] args) {
        // DAG:  5 → 2 → 3 → 1
        //       5 → 0
        //       4 → 0, 4 → 1
        Map<Integer, List<Integer>> dag = new HashMap<>();
        dag.put(5, List.of(2, 0));
        dag.put(4, List.of(0, 1));
        dag.put(2, List.of(3));
        dag.put(3, List.of(1));
        dag.put(0, List.of());
        dag.put(1, List.of());
        System.out.println(topoSort(dag));      // e.g. [4, 5, 0, 2, 3, 1]

        // Cycle: 1 → 2 → 1
        Map<Integer, List<Integer>> cyclic = new HashMap<>();
        cyclic.put(1, List.of(2));
        cyclic.put(2, List.of(1));
        try {
            topoSort(cyclic);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage()); // Graph has a cycle
        }
    }
}
