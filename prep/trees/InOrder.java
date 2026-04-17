package prep.trees;

import java.util.*;

/**
 * In-order traversal of a binary tree (recursive + iterative).
 *
 * Key insights / interview points:
 *  - In-order = left, node, right. On a BST this yields sorted order.
 *  - Iterative version uses a three-state machine per node:
 *      1. Haven't gone left yet  → mark seenLeft, push left child
 *      2. Left done, not right   → record value, mark seenRight, push right child
 *      3. Both done              → pop
 *    This is a consistent framework across all three traversals — just move
 *    where the "record" step happens between states.
 *  - The standard "drill left + pop + go right" approach (no node mutation)
 *    is shorter but harder to arrive at from first principles. The state-
 *    machine version maps directly to the recursive structure.
 *  - In-order on a BST is the backbone of: BST validation (values must be
 *    strictly increasing), BST iterator (LC 173), and kth-smallest (LC 230).
 */
public class InOrder {

    // --- Recursive ---

    public static List<Integer> inorderRecursive(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        inorderHelper(root, result);
        return result;
    }

    private static void inorderHelper(TreeNode node, List<Integer> result) {
        if (node == null) return;
        inorderHelper(node.left, result);
        result.add(node.val);                // IN-order: between left and right
        inorderHelper(node.right, result);
    }

    // --- Iterative (state-machine approach) ---

    public static List<Integer> inorderIterative(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;

        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode cur = stack.peek();

            if (!cur.seenLeft && !cur.seenRight) {
                // State 1: go left first
                cur.seenLeft = true;
                if (cur.left != null) stack.push(cur.left);

            } else if (cur.seenLeft && !cur.seenRight) {
                // State 2: left done — record self, then go right
                cur.seenRight = true;
                result.add(cur.val);
                if (cur.right != null) stack.push(cur.right);

            } else {
                // State 3: both children done — pop
                stack.pop();
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

        System.out.println(inorderRecursive(root));  // [4, 2, 5, 1, 3, 6]
        System.out.println(inorderIterative(root));   // [4, 2, 5, 1, 3, 6]
    }
}
