package prep.graphs;

import java.util.*;

/**
 * Iterative DFS on a directed graph represented as an adjacency list.
 *
 * Key insights / interview points:
 *  - Mark visited AT PUSH TIME, not at pop time. Same rule as BFS enqueue-time
 *    marking: if you mark on pop, a node can be pushed many times before it's
 *    finally popped, blowing up the stack and causing revisits.
 *  - To make the iterative order match RECURSIVE DFS order, push neighbors in
 *    REVERSE. Reason: a stack is LIFO — the last neighbor pushed is the first
 *    visited, so pushing [2, 3] would visit 3 before 2. Reversing to [3, 2]
 *    puts 2 on top, matching left-to-right recursion.
 *  - The reverse-to-match-recursion trick also shows up in iterative tree
 *    pre-order and is worth keeping in the toolkit.
 *  - `visited.add(nei)` returns false if already present — one call does the
 *    check-and-insert, cleaner than contains() + add().
 *  - Use a reverse index loop instead of Collections.reverse() — no allocation,
 *    doesn't mutate the input list.
 *
 * When to prefer iterative over recursive DFS:
 *  - Deep graphs / linked-list-shaped trees where recursion might stack-overflow
 *  - When you want to pause / resume traversal, or expose it as an iterator
 *  - When you need explicit control over the frontier (e.g. bounded-depth search)
 * Prefer recursive DFS when backtracking is involved (N-Queens, Word Search) —
 * the call stack naturally unwinds state.
 */
public class DFSGraph {

    public static List<Integer> dfs(Map<Integer, List<Integer>> graph, int source) {
        List<Integer> result = new ArrayList<>();
        if (graph == null) return result;

        Deque<Integer> stack = new ArrayDeque<>();
        Set<Integer> visited = new HashSet<>();

        stack.push(source);
        visited.add(source);

        while (!stack.isEmpty()) {
            int cur = stack.pop();
            result.add(cur);

            List<Integer> neighbors = graph.getOrDefault(cur, List.of());
            // iterate in reverse so the first neighbor ends up on top of the
            // stack — this makes the traversal order match recursive DFS.
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                int nei = neighbors.get(i);
                if (visited.add(nei)) {   // returns false if already seen
                    stack.push(nei);
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
        System.out.println(dfs(graph, 1)); // [1, 2, 4, 3, 5] — matches recursive DFS
    }
}
