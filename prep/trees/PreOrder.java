package prep.trees;

import java.util.*;

/**
 * Pre-order traversal of a binary tree (recursive + iterative).
 *
 * Key insights / interview points:
 *  - Pre-order = process node BEFORE its children (root, left, right).
 *  - Recursive version is trivial — the interesting one is iterative.
 *  - Iterative: push RIGHT child first, then LEFT, so left ends up on top of
 *    the stack. This is the binary-tree specialization of the reverse-neighbors
 *    trick from iterative graph DFS.
 *  - No `visited` set needed — trees are acyclic by definition, so a node is
 *    only reachable via one path. If an interviewer asks "what if there were
 *    cycles?" you'd add a visited set, making it exactly the graph DFS template.
 *  - Prefer Deque<TreeNode> + ArrayDeque over the legacy Stack class.
 */
public class PreOrder {

    // --- Recursive ---

    public static List<Integer> preorderRecursive(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        preorderHelper(root, result);
        return result;
    }

    private static void preorderHelper(TreeNode node, List<Integer> result) {
        if (node == null) return;
        result.add(node.val);              // PRE-order: record on entry
        preorderHelper(node.left, result);
        preorderHelper(node.right, result);
    }

    // --- Iterative ---

    public static List<Integer> preorderIterative(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;

        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode cur = stack.pop();
            result.add(cur.val);

            // Push right first so left is on top → visited first.
            if (cur.right != null) stack.push(cur.right);
            if (cur.left != null) stack.push(cur.left);
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

        System.out.println(preorderRecursive(root));  // [1, 2, 4, 5, 3, 6]
        System.out.println(preorderIterative(root));   // [1, 2, 4, 5, 3, 6]
    }
}
