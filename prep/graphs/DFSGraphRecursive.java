package prep.graphs;

import java.util.*;

/**
 * Recursive DFS on a directed graph represented as an adjacency list.
 *
 * Key insights / interview points:
 *  - Separate the public entry point from the recursive helper. The caller
 *    shouldn't have to build or pass the visited set — that's an implementation
 *    concern. Public wrapper creates mutable state, private helper recurses.
 *  - `visited.add(node)` at the TOP of the helper is the idiomatic guard.
 *    Returns false if already present, so one call does check + insert and
 *    also serves as the base case.
 *  - PRE-order vs POST-order is just WHERE you call `result.add(node)`:
 *      - before the children loop → pre-order  → [1,2,4,3,5]
 *      - after the children loop  → post-order → [4,2,5,3,1]
 *    Post-order is the heavy hitter for directed graphs: reverse(post-order)
 *    is a topological sort, and post-order timestamps are the backbone of
 *    Kosaraju's SCC.
 *
 * Space complexity:
 *  - O(V) for visited + O(V) for recursion stack in the worst case (a path-
 *    shaped graph of V nodes recurses V levels deep).
 *  - On adversarial inputs (very deep graphs, linked-list-shaped trees) this
 *    can blow the JVM stack — that's when you reach for the iterative version.
 *
 * Relationship to 3-color DFS:
 *  - For plain reachability, a single visited Set is enough.
 *  - 3-color (WHITE / GRAY / BLACK) distinguishes "on current recursion stack"
 *    (GRAY) from "fully finished" (BLACK). That distinction is what detects
 *    back edges, i.e. cycles, in a directed graph. Same recursion skeleton,
 *    richer bookkeeping.
 */
public class DFSGraphRecursive {

    public static List<Integer> dfs(Map<Integer, List<Integer>> graph, int source) {
        List<Integer> result = new ArrayList<>();
        if (graph == null) return result;
        Set<Integer> visited = new HashSet<>();
        dfs(graph, source, visited, result);
        return result;
    }

    private static void dfs(Map<Integer, List<Integer>> graph,
                            int node,
                            Set<Integer> visited,
                            List<Integer> result) {
        if (!visited.add(node)) return;   // check-and-insert; also the base case

        result.add(node);                 // PRE-order: record on entry

        for (int nei : graph.getOrDefault(node, List.of())) {
            dfs(graph, nei, visited, result);
        }

        // To produce POST-order instead, move `result.add(node)` to HERE and
        // remove it from above. Reverse(post-order) is a topological sort.
    }

    public static void main(String[] args) {
        Map<Integer, List<Integer>> graph = Map.of(
                1, List.of(2, 3),
                2, List.of(4),
                3, List.of(4, 5),
                4, List.of(),
                5, List.of()
        );
        System.out.println(dfs(graph, 1)); // [1, 2, 4, 3, 5]
    }
}
