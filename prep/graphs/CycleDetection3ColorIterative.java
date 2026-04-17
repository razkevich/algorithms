package prep.graphs;

import java.util.*;

/**
 * Iterative cycle detection in a directed graph using 3-color DFS.
 *
 * Key insights / interview points:
 *  - Same 3-color logic as the recursive version, but with an explicit stack.
 *  - The trick: peek (don't pop) on first visit, set a "visiting" flag, push
 *    children. When children are done and the same entry resurfaces at the top,
 *    the flag tells you it's exit time — pop and clean up.
 *  - Maintains one shared currentPath set (= the gray nodes) instead of
 *    copying paths per branch. Enter adds to the set, exit removes — O(V+E).
 *  - A "visited" set tracks fully finished nodes (= black) so we skip them
 *    if they appear again from a different parent.
 *
 * Branch → color mapping:
 *   !visiting          → WHITE → GRAY  (enter, push children)
 *   visiting           → GRAY → BLACK  (exit, remove from path)
 *   visited.contains   → BLACK → skip
 *
 * Time:  O(V + E)
 * Space: O(V)
 */
public class CycleDetection3ColorIterative {

    public static boolean hasCycle(Map<Integer, List<Integer>> graph) {
        Set<Integer> visited = new HashSet<>();
        for (int node : graph.keySet()) {
            if (visited.contains(node)) continue;
            if (dfsHasCycle(graph, node, visited)) return true;
        }
        return false;
    }

    private static boolean dfsHasCycle(Map<Integer, List<Integer>> graph,
                                       int root,
                                       Set<Integer> visited) {
        LinkedHashSet<Integer> currentPath = new LinkedHashSet<>();
        Deque<Value> stack = new ArrayDeque<>();
        stack.push(new Value(root));

        while (!stack.isEmpty()) {
            Value cur = stack.peek();

            if (visited.contains(cur.val)) {
                // BLACK — already fully done, skip
                stack.pop();
                continue;
            } else if (cur.visiting) {
                // GRAY → BLACK — exit, clean up
                stack.pop();
                currentPath.remove(cur.val);
                visited.add(cur.val);
            } else {
                // WHITE → GRAY — enter, push children
                cur.visiting = true;
                currentPath.add(cur.val);

                for (int nei : graph.getOrDefault(cur.val, List.of())) {
                    if (currentPath.contains(nei)) {
                        return true;            // back edge → cycle
                    }
                    stack.push(new Value(nei));
                }
            }
        }
        return false;
    }

    private static class Value {
        int val;
        boolean visiting;
        Value(int val) { this.val = val; }
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
