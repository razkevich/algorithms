package prep.unionfind;

/**
 * Disjoint Set Union (Union-Find) with path compression + union by rank.
 *
 * Key insights / interview points:
 *  - Each set is represented as an upward-pointing tree: parent[i] is the
 *    node above i, and roots point to themselves. Two nodes are in the same
 *    set iff they share a root.
 *  - Two optimizations are both needed for the O(α(n)) amortized bound:
 *      • Path compression in find — on the way up, rewire every visited
 *        node directly to the root, flattening the tree.
 *      • Union by rank — attach the shorter tree under the taller one so
 *        height barely grows. Rank is an UPPER BOUND on height; path
 *        compression may make the real height smaller, but rank doesn't
 *        shrink (and that's fine — it's still a valid upper bound).
 *  - Rank only grows when unioning two trees of equal rank. That tie-break
 *    is the whole point of the optimization; drop it and you fall back to
 *    O(log n) amortized.
 *  - union returns false when the endpoints were already connected — this
 *    is the canonical undirected-graph cycle detector and drives Kruskal's
 *    MST (skip any edge whose union returns false).
 *  - The components counter is free bookkeeping: decrement on successful
 *    union, and "how many groups?" becomes O(1).
 *
 * Complexity:
 *  - find / union: O(α(n)) amortized — inverse Ackermann, effectively O(1).
 *  - Without rank OR without compression: O(log n) amortized (still fine).
 *  - Without either: O(n) worst case.
 *
 * Pattern transfer: connected components, cycle detection in undirected
 * graphs, Kruskal's MST, Number of Provinces, Accounts Merge,
 * Redundant Connection, "are these two nodes in the same group?"
 */
public class DSU {

    private final int[] parent;
    private final int[] rank;   // upper bound on tree height
    private int components;

    public DSU(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;   // every node is its own root
        components = n;
    }

    /** Return the root of x's set, compressing the path on the way up. */
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);   // rewire x directly to the root
        }
        return parent[x];
    }

    /** Merge the sets containing x and y. Returns true if a merge happened. */
    public boolean union(int x, int y) {
        int rx = find(x);
        int ry = find(y);
        if (rx == ry) return false;        // already connected — caller can detect a cycle

        // Attach the shorter tree under the taller one. Link ROOTS, not the original nodes.
        if (rank[rx] < rank[ry])      parent[rx] = ry;
        else if (rank[rx] > rank[ry]) parent[ry] = rx;
        else { parent[ry] = rx; rank[rx]++; }   // equal height — pick one, bump rank

        components--;
        return true;
    }

    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }

    public int components() { return components; }

    public static void main(String[] args) {
        DSU dsu = new DSU(6);
        dsu.union(0, 1);
        dsu.union(2, 3);
        dsu.union(4, 5);
        System.out.println(dsu.components());        // 3
        System.out.println(dsu.connected(0, 1));     // true
        System.out.println(dsu.connected(0, 2));     // false

        dsu.union(1, 3);                              // merge {0,1} and {2,3}
        System.out.println(dsu.connected(0, 2));     // true
        System.out.println(dsu.components());        // 2

        System.out.println(dsu.union(0, 3));         // false — already connected (cycle edge)
    }
}
