/*
 * Validate BST — Tree Recursion with State (bounds DOWN half)
 *
 * Pattern: bounds-down tree recursion.
 *   Caller narrows the valid range each child must satisfy.
 *     Left subtree:  every node must be < parent  → tighten `max`
 *     Right subtree: every node must be > parent  → tighten `min`
 *   A node never checks its own children locally — the recursive call
 *   with updated bounds enforces it at the next level. That locality
 *   is what makes this pattern compose.
 *
 * Transfers to:
 *   - Range Sum of BST       (bounds = [low, high], prune whole subtrees)
 *   - Recover BST            (bounds identify the two swapped nodes)
 *   - Count BST nodes in range
 *   - Balanced-BST validation (down bounds + up height = both halves)
 *
 * Traps (interview-famous):
 *   1. `null` IS a valid BST → return true, not false.
 *   2. Strict BST (LC 98) → use <= / >=, not < / > (duplicates forbidden).
 *   3. `Integer.MIN_VALUE` / `Integer.MAX_VALUE` sentinels break when a
 *      real node equals them. Use `long` bounds OR boxed `Integer` with
 *      null = unbounded.
 *   4. Don't add redundant child-value checks — the recursion already
 *      enforces the BST property via the bounds. Lines that don't pull
 *      weight obscure the pattern.
 */

public class ValidateBST {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int v) { this.val = v; }
    }

    // ---------- Primary: long bounds (quickest to write) ----------

    public boolean isValidBST(TreeNode root) {
        return isValidBST(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private boolean isValidBST(TreeNode root, long min, long max) {
        if (root == null) return true;
        if (root.val <= min || root.val >= max) return false;
        return isValidBST(root.left,  min, root.val)
            && isValidBST(root.right, root.val, max);
    }

    // ---------- Alternative: Integer + null = unbounded ----------
    // Avoids the sentinel cheat; works for any int range without
    // widening to long. The "I thought about it" answer in interviews.

    public boolean isValidBST_boxed(TreeNode root) {
        return check(root, null, null);
    }

    private boolean check(TreeNode root, Integer min, Integer max) {
        if (root == null) return true;
        if (min != null && root.val <= min) return false;
        if (max != null && root.val >= max) return false;
        return check(root.left,  min, root.val)
            && check(root.right, root.val, max);
    }

    // ---------- Demo ----------

    public static void main(String[] args) {
        ValidateBST s = new ValidateBST();

        // [2, 1, 3] → true
        TreeNode t1 = new TreeNode(2);
        t1.left  = new TreeNode(1);
        t1.right = new TreeNode(3);
        System.out.println(s.isValidBST(t1));        // true

        // [5, 1, 4, null, null, 3, 6] → false
        // (node 3 sits in 5's right subtree; bound says it must be > 5)
        TreeNode t2 = new TreeNode(5);
        t2.left  = new TreeNode(1);
        t2.right = new TreeNode(4);
        t2.right.left  = new TreeNode(3);
        t2.right.right = new TreeNode(6);
        System.out.println(s.isValidBST(t2));        // false

        // Single node with Integer.MIN_VALUE → true
        // (would break with MIN_VALUE sentinels; fine with long)
        TreeNode t3 = new TreeNode(Integer.MIN_VALUE);
        System.out.println(s.isValidBST(t3));        // true
    }
}
