/*
 * Serialize and Deserialize Binary Tree (LC 297) — Null-Marker Encoding
 *
 * Pattern: a traversal WITH null markers is an invertible encoding of
 * tree shape. One walk serializes, the mirror walk rebuilds.
 *
 *   Without nulls:  "1,2,3,4,5"  is ambiguous — many trees fit.
 *   With nulls:     "1,2,#,#,3,4,#,#,5,#,#"  is uniquely one tree.
 *
 * The nulls act as TERMINATORS — "this subtree is empty, return up."
 * Which is exactly the thing the serializer's recursion does on the
 * way down. One token per recursive call → symmetric by construction.
 *
 * Transfers to:
 *   - Subtree of Another Tree (572)     — serialize both, substring / hash match
 *   - Find Duplicate Subtrees (652)     — serialize each subtree, group by key
 *   - Tree hashing / equality           — any need to compare or persist trees
 *   - AST / expression-tree persistence — same template for any rooted tree
 *   - K-ary trees: emit k children per node, or use a closing marker ')'
 *
 * Traps:
 *   - Without null markers, you need TWO traversals (pre+in or post+in)
 *     to disambiguate. Single traversal + nulls is the shortcut.
 *   - Don't try to compute positional indices (2^level, heap-style).
 *     Real trees skip null subtrees — the index math doesn't line up.
 *   - BFS variant must keep the serializer/deserializer symmetric:
 *     "pop parent, consume 2 tokens for its children, push real ones."
 */

import java.util.*;

public class SerializeDeserializeBinaryTree {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int v) { this.val = v; }
    }

    // ======================================================================
    // Template A (PRIMARY): Pre-order DFS with null markers
    //
    // Shortest code. Call stack IS the position tracker — no bookkeeping.
    // ======================================================================

    public static class Codec {

        public String serialize(TreeNode root) {
            StringBuilder sb = new StringBuilder();
            ser(root, sb);
            return sb.toString();
        }

        private void ser(TreeNode n, StringBuilder sb) {
            if (n == null) { sb.append("#,"); return; }
            sb.append(n.val).append(',');
            ser(n.left, sb);
            ser(n.right, sb);
        }

        public TreeNode deserialize(String data) {
            Deque<String> tokens = new ArrayDeque<>(Arrays.asList(data.split(",")));
            return des(tokens);
        }

        private TreeNode des(Deque<String> tokens) {
            String t = tokens.poll();
            if (t.equals("#")) return null;
            TreeNode n = new TreeNode(Integer.parseInt(t));
            n.left  = des(tokens);    // recursion pairs calls with tokens automatically
            n.right = des(tokens);
            return n;
        }
    }

    // ======================================================================
    // Template B (ALTERNATIVE): Level-order BFS (LeetCode's own format)
    //
    // Matches LC's [1,2,3,null,null,4,5] exactly. Harder to get right —
    // you need the symmetry: "only real nodes enter the queue; each popped
    // parent consumes exactly 2 tokens as its children."
    // ======================================================================

    public static class CodecBFS {

        public String serialize(TreeNode root) {
            if (root == null) return "";
            StringBuilder sb = new StringBuilder();
            Queue<TreeNode> q = new ArrayDeque<>();
            q.offer(root);
            sb.append(root.val);
            while (!q.isEmpty()) {
                TreeNode cur = q.poll();
                for (TreeNode child : new TreeNode[]{ cur.left, cur.right }) {
                    sb.append(',');
                    if (child == null) {
                        sb.append("null");
                    } else {
                        sb.append(child.val);
                        q.offer(child);              // only real children enter the queue
                    }
                }
            }
            return sb.toString();
        }

        public TreeNode deserialize(String data) {
            if (data.isEmpty()) return null;
            String[] t = data.split(",");
            TreeNode root = new TreeNode(Integer.parseInt(t[0]));
            Queue<TreeNode> q = new ArrayDeque<>();
            q.offer(root);
            int i = 1;
            while (!q.isEmpty()) {
                TreeNode cur = q.poll();
                if (!t[i].equals("null")) {
                    cur.left = new TreeNode(Integer.parseInt(t[i]));
                    q.offer(cur.left);
                }
                i++;
                if (!t[i].equals("null")) {
                    cur.right = new TreeNode(Integer.parseInt(t[i]));
                    q.offer(cur.right);
                }
                i++;
            }
            return root;
        }
    }

    // ---------- Demo ----------

    public static void main(String[] args) {
        // Tree:      1
        //           / \
        //          2   3
        //             / \
        //            4   5
        TreeNode t = new TreeNode(1);
        t.left  = new TreeNode(2);
        t.right = new TreeNode(3);
        t.right.left  = new TreeNode(4);
        t.right.right = new TreeNode(5);

        Codec dfs = new Codec();
        String dfsEnc = dfs.serialize(t);
        System.out.println("DFS:  " + dfsEnc);              // 1,2,#,#,3,4,#,#,5,#,#,
        TreeNode dfsDec = dfs.deserialize(dfsEnc);
        System.out.println("DFS roundtrip: " + dfs.serialize(dfsDec));

        CodecBFS bfs = new CodecBFS();
        String bfsEnc = bfs.serialize(t);
        System.out.println("BFS:  " + bfsEnc);              // 1,2,3,null,null,4,5
        TreeNode bfsDec = bfs.deserialize(bfsEnc);
        System.out.println("BFS roundtrip: " + bfs.serialize(bfsDec));
    }
}
