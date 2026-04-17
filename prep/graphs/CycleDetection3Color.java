package prep.graphs;

import java.util.*;

/**
 * Cycle detection in a directed graph using 3-color DFS.
 *
 * Key insights / interview points:
 *  - A single visited Set can't distinguish "on the current DFS path" from
 *    "finished in a previous branch." In a diamond graph (1→2, 1→3, 2→4, 3→4),
 *    node 4 is reached twice with no cycle — but a visited Set would say
 *    "already seen 4" and give a false positive if you treated that as a cycle.
 *  - 3-color solves this:
 *      WHITE  = unvisited
 *      GRAY   = on the current recursion stack (VISITING)
 *      BLACK  = fully finished, all descendants explored (VISITED)
 *  - An edge to a GRAY node is a back edge → cycle.
 *    An edge to a BLACK node is a cross/forward edge → safe.
 *  - Must iterate all nodes in the outer loop to handle disconnected graphs.
 *  - Same DFS skeleton as basic recursive DFS, just richer bookkeeping.
 *
 * Time:  O(V + E)
 * Space: O(V) for the color map + recursion stack
 *
 * Transfer: this same 3-color walk is the backbone of topological sort
 * (record in reverse post-order) and Kosaraju's SCC.
 */
public class CycleDetection3Color {

    public static boolean hasCycle(Map<Integer, List<Integer>> graph) {
        Map<Integer, String> state = new HashMap<>();
        for (int node : graph.keySet()) {
            if (state.containsKey(node)) continue;       // already explored
            if (dfsVisit(graph, node, state)) return true;
        }
        return false;
    }

    private static boolean dfsVisit(Map<Integer, List<Integer>> graph,
                                    int node,
                                    Map<Integer, String> state) {
        state.put(node, "VISITING");                      // GRAY — entering

        for (int nei : graph.getOrDefault(node, List.of())) {
            if ("VISITING".equals(state.get(nei))) {      // back edge → cycle
                return true;
            }
            if ("VISITED".equals(state.get(nei))) {       // already done → skip
                continue;
            }
            if (dfsVisit(graph, nei, state)) return true; // propagate cycle
        }

        state.put(node, "VISITED");                       // BLACK — done
        return false;
    }

    public static void main(String[] args) {
        // Cycle: 1 → 2 → 4 → 1
        Map<Integer, List<Integer>> cyclic = Map.of(
                1, List.of(2, 3),
                2, List.of(4),
                3, List.of(),
                4, List.of(1)
        );
        System.out.println(hasCycle(cyclic));  // true

        // No cycle
        Map<Integer, List<Integer>> acyclic = Map.of(
                1, List.of(2, 3),
                2, List.of(4),
                3, List.of(4, 5),
                4, List.of(),
                5, List.of()
        );
        System.out.println(hasCycle(acyclic)); // false
    }
}
