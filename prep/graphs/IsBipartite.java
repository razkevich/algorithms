package prep.graphs;

/*
 * LC 785 — Is Graph Bipartite?
 *
 * Given an undirected graph as adjacency lists (graph[i] = neighbors of node i),
 * decide whether the nodes can be split into two sets A and B such that every
 * edge has one endpoint in A and one in B. Equivalently: can the graph be
 * 2-colored so that no edge connects same-colored nodes?
 *
 * Pattern: BFS from each unvisited node, alternating colors (0 / 1) as you
 * expand. If a neighbor is already colored AND its color matches the current
 * node's color, the graph isn't bipartite.
 *
 * Why it works (the deep reason): a graph is bipartite iff it has no
 * odd-length cycle. BFS coloring discovers an odd cycle exactly when it tries
 * to assign a node a color it already has — that conflict IS the odd cycle.
 *
 * Edge cases:
 *   - Disconnected graph: must run BFS from every uncolored node (one BFS
 *     only covers one component).
 *   - Empty graph (n=0): vacuously bipartite, return true.
 *   - Self-loop (graph[i] contains i): instant false (LC 785 inputs don't
 *     include these, but real graphs might).

 * Examples:
 *   graph = [[1,3],[0,2],[1,3],[0,2]]  → true   (square: A-B-A-B)
 *   graph = [[1,2,3],[0,2],[0,1,3],[0,2]] → false (triangle 0-1-2 is odd cycle)
 */
class IsBipartite {

    public boolean isBipartite(int[][] graph) {
        return false;
    }
}
