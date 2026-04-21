/*
 * Binary Tree Maximum Path Sum (LC 124) — Values-UP Tree Recursion
 *
 * Pattern: values-UP tree recursion.
 *   Each call RETURNS one thing to the parent, and SIDE-EFFECTS a global
 *   with a different thing. That tension — "return one, update another"
 *   — is the load-bearing idea.
 *
 *   Locally at each node:
 *     oneArm   = val + max(leftGain, rightGain)     ← returned upward (parent keeps ONE side)
 *     bothArms = val + leftGain + rightGain         ← global candidate (path BENDS here, can't be extended)
 *
 *   The clamp `max(0, ...)` on each child's gain is the "skip negative
 *   subtrees" trick: if a subtree would hurt the path, don't include it.
 *
 * Transfers to (tree DP family):
 *   - Diameter of Binary Tree (543)         — return height, update left_h + right_h
 *   - Longest Univalue Path (687)           — same, constrained to equal values
 *   - House Robber III (337)                — return int[]{rob, skip} tuple (tuple-return variant)
 *   - Binary Tree Cameras (968)             — return 3-state tuple (tree DP with states)
 *   - LCA of Binary Tree (236)              — return node-or-null, short-circuit at meeting point
 *
 * Traps:
 *   - globalMax must start at Integer.MIN_VALUE, not 0 — paths can be
 *     fully negative (e.g. [-3] → answer is -3, not 0).
 *   - Don't forget to clamp each child's gain to 0 before adding.
 *   - `oneArm` the RETURNED value includes val; some templates use the
 *     name for just max(L, R) before adding val. Pick one convention.
 */

public class MaxPathSum {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int v) { this.val = v; }
    }

    // ---------- Primary: preserved shape, typos fixed ----------

    int globalMax = Integer.MIN_VALUE;

    public int maxPathSum(TreeNode root) {
        explore(root);
        return globalMax;
    }

    private int explore(TreeNode root) {
        if (root == null) return 0;

        int leftArm  = Math.max(0, explore(root.left));
        int rightArm = Math.max(0, explore(root.right));

        // best child-gain to carry upward; parent keeps ONE side only
        int oneArm = Math.max(leftArm, rightArm);

        // bothArms candidate: path BENDS through this node (can't extend further up)
        globalMax = Math.max(globalMax, leftArm + rightArm + root.val);

        return root.val + oneArm;
    }

    // ---------- Alternative naming (what LC community usually writes) ----------
    // Same logic, but `oneArm` here means the full returned value (includes val).
    // Pick whichever reads clearer to you.
    //
    // int gainFrom(TreeNode n) {
    //     if (n == null) return 0;
    //     int L = Math.max(0, gainFrom(n.left));
    //     int R = Math.max(0, gainFrom(n.right));
    //     best = Math.max(best, n.val + L + R);   // bothArms: global side effect
    //     return n.val + Math.max(L, R);          // oneArm:   pick one side, return up
    // }

    // ---------- Demo ----------

    public static void main(String[] args) {
        MaxPathSum s = new MaxPathSum();

        // [-10, 9, 20, null, null, 15, 7]  →  42  (15 → 20 → 7)
        TreeNode t1 = new TreeNode(-10);
        t1.left  = new TreeNode(9);
        t1.right = new TreeNode(20);
        t1.right.left  = new TreeNode(15);
        t1.right.right = new TreeNode(7);
        System.out.println(s.maxPathSum(t1));   // 42

        // [-3]  →  -3  (globalMax starting at MIN_VALUE matters here)
        s.globalMax = Integer.MIN_VALUE;
        TreeNode t2 = new TreeNode(-3);
        System.out.println(s.maxPathSum(t2));   // -3

        // [2, -1]  →  2  (skipping the -1 child via the 0-clamp)
        s.globalMax = Integer.MIN_VALUE;
        TreeNode t3 = new TreeNode(2);
        t3.left = new TreeNode(-1);
        System.out.println(s.maxPathSum(t3));   // 2
    }
}
