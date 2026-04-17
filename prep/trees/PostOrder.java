package prep.trees;

import java.util.*;

/**
 * Post-order traversal of a binary tree (recursive + iterative).
 *
 * Key insights / interview points:
 *  - Post-order = process node AFTER both children (left, right, root).
 *  - The core problem: when you pop a node, how do you know its children are
 *    done? Two clean approaches:
 *
 *    1. "seen flag" — first visit pushes children ahead; second visit means
 *       children are done (they were above on the stack and already popped).
 *       Simple to reason about, but mutates the tree nodes.
 *
 *    2. "lastVisited pointer" — peek at top; if its right child == lastVisited,
 *       both subtrees are done, safe to pop. No mutation, standard interview answer.
 *
 *    Both are structurally equivalent — same stack behavior, different place to
 *    store the "children finished" signal (node flag vs external variable).
 *
 *  - Post-order is the most useful DFS variant for directed graphs:
 *      reverse(post-order) = topological sort
 *      post-order + transpose graph = Kosaraju's SCC
 */
public class PostOrder {

    // --- Recursive ---

    public static List<Integer> postorderRecursive(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        postorderHelper(root, result);
        return result;
    }

    private static void postorderHelper(TreeNode node, List<Integer> result) {
        if (node == null) return;
        postorderHelper(node.left, result);
        postorderHelper(node.right, result);
        result.add(node.val);              // POST-order: record on exit
    }

    // --- Iterative (lastVisited approach, no mutation) ---

    public static List<Integer> postorderIterative(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;

        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode cur = root;
        TreeNode lastVisited = null;

        while (cur != null || !stack.isEmpty()) {
            // Drill all the way left
            while (cur != null) {
                stack.push(cur);
                cur = cur.left;
            }

            TreeNode peek = stack.peek();
            // If right child exists and hasn't been visited yet, go right
            if (peek.right != null && peek.right != lastVisited) {
                cur = peek.right;
            } else {
                // Both children done — safe to process this node
                stack.pop();
                result.add(peek.val);
                lastVisited = peek;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        //        1
        //       / \
        //      2   3
        //     / \   \
        //    4   5   6
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        root.right.right = new TreeNode(6);

        System.out.println(postorderRecursive(root));  // [4, 5, 2, 6, 3, 1]
        System.out.println(postorderIterative(root));   // [4, 5, 2, 6, 3, 1]
    }
}
