package prep.linkedlist;

import java.util.HashMap;
import java.util.Map;

/**
 * Copy List with Random Pointer (LC 138) — deep-clone a linked list
 * where each node has an extra `random` pointer to any node in the list.
 *
 * The core difficulty:
 *  - `next` clones linearly: one forward pass is enough.
 *  - `random` is the chicken-and-egg: while cloning node A, its
 *    `random` may point to a later node C whose clone doesn't exist yet.
 *  - Node `val` is NOT unique — clones must be keyed by reference identity,
 *    not by value. Any approach that identifies nodes by `val` is broken.
 *
 * Two canonical templates for this problem:
 *
 *  (1) Hash map — O(n) time, O(n) extra space.
 *      Pass 1: walk the old list, create a clone for every node,
 *              store `oldToNew: Map<Node, Node>`.
 *      Pass 2: walk the old list again, wire `clone.next = oldToNew.get(old.next)`
 *              and `clone.random = oldToNew.get(old.random)`.
 *      Simple, readable, the "warm-up" answer. Uses reference identity
 *      (Java's default `Object.hashCode/equals` on Node) so duplicate vals
 *      are fine.
 *
 *  (2) Interleave and split — O(n) time, O(1) extra space (beyond output).
 *      This is the trick that makes the problem famous.
 *
 *      Step A — splice clones into the original list in-place:
 *          A → A' → B → B' → C → C'
 *        ```
 *        Node cur = head;
 *        while (cur != null) {
 *            Node clone = new Node(cur.val);
 *            clone.next = cur.next;
 *            cur.next = clone;
 *            cur = clone.next;
 *        }
 *        ```
 *
 *      Step B — wire random pointers. For any old node X, X.next is X';
 *      so X'.random is literally X.random.next:
 *        ```
 *        for (Node cur = head; cur != null; cur = cur.next.next) {
 *            if (cur.random != null) cur.next.random = cur.random.next;
 *        }
 *        ```
 *
 *      Step C — un-interleave back into two lists:
 *        ```
 *        Node cloneHead = head.next;
 *        for (Node cur = head; cur != null; cur = cur.next) {
 *            Node clone = cur.next;
 *            cur.next = clone.next;
 *            clone.next = (clone.next != null) ? clone.next.next : null;
 *        }
 *        return cloneHead;
 *        ```
 *
 *      The O(1)-space wins when the list is big or when you're asked
 *      to avoid auxiliary structures. Conceptually, it's the same
 *      "old → new" lookup as the hashmap, but encoded structurally in
 *      the interleaved list itself: `X.next` IS `X'`.
 *
 * The implementation below preserves the user's two-map shape. A single
 * `oldToNew` map is enough — the `newToOld` map is only used to recover
 * the old node while walking the new list, which is avoidable by walking
 * the old list directly in pass 2.
 *
 * Pattern transfer:
 *  - Any clone / serialize-deserialize problem where references form a
 *    graph, not a tree, is the same shape: you need an identity map
 *    (or interleave trick) to resolve forward references.
 *  - Graph clone (LC 133) uses the same hashmap approach with DFS/BFS.
 */
public class CopyRandomList {

    static class Node {
        int val;
        Node next;
        Node random;
        public Node(int val) { this.val = val; }
        public Node(int val, Node next, Node random) {
            this.val = val;
            this.next = next;
            this.random = random;
        }
    }

    public Node copyRandomList(Node head) {
        if (head == null) return null;

        Map<Node, Node> newToOld = new HashMap<>();
        Map<Node, Node> oldToNew = new HashMap<>();

        Node cur = head;
        while (true) {
            Node newCur = new Node(cur.val, null, null);
            newToOld.put(newCur, cur);
            oldToNew.put(cur, newCur);
            cur = cur.next;
            if (cur == null) break;
        }

        cur = oldToNew.get(head);
        while (true) {
            Node curOld = newToOld.get(cur);
            cur.next = oldToNew.get(curOld.next);
            cur.random = oldToNew.get(curOld.random);
            cur = cur.next;
            if (cur == null) break;
        }

        return oldToNew.get(head);
    }

    // --- Tests ---
    public static void main(String[] args) {
        CopyRandomList s = new CopyRandomList();

        // null head
        assert s.copyRandomList(null) == null;

        // LC example: [[7,null],[13,0],[11,4],[10,2],[1,0]]
        Node n0 = new Node(7), n1 = new Node(13), n2 = new Node(11),
             n3 = new Node(10), n4 = new Node(1);
        n0.next = n1; n1.next = n2; n2.next = n3; n3.next = n4;
        n0.random = null; n1.random = n0; n2.random = n4;
        n3.random = n2;   n4.random = n0;
        verifyDeepCopy(s.copyRandomList(n0), n0);

        // duplicates in val
        Node a = new Node(5), b = new Node(5), c = new Node(5);
        a.next = b; b.next = c;
        a.random = c; b.random = b; c.random = a;  // self-ref and dup vals
        verifyDeepCopy(s.copyRandomList(a), a);

        // single node, self-random
        Node solo = new Node(1);
        solo.random = solo;
        Node cloneSolo = s.copyRandomList(solo);
        assert cloneSolo != solo;
        assert cloneSolo.random == cloneSolo;
        assert cloneSolo.val == 1;

        System.out.println("all cases passed");
    }

    private static void verifyDeepCopy(Node copy, Node orig) {
        Node co = copy, or = orig;
        while (or != null) {
            if (co == null) throw new AssertionError("copy shorter than orig");
            if (co == or)   throw new AssertionError("copy shares node with orig");
            if (co.val != or.val) throw new AssertionError("val mismatch");
            if ((co.random == null) != (or.random == null))
                throw new AssertionError("random null-ness mismatch");
            if (co.random != null && co.random.val != or.random.val)
                throw new AssertionError("random points to wrong val");
            if (co.random != null && co.random == or.random)
                throw new AssertionError("random points into orig list");
            co = co.next; or = or.next;
        }
        if (co != null) throw new AssertionError("copy longer than orig");
    }
}
