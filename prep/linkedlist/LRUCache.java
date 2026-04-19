package prep.linkedlist;

import java.util.HashMap;
import java.util.Map;

/**
 * LRU Cache — fixed-capacity cache with O(1) get and put.
 *
 * Key insights / interview points:
 *  - Composition of two data structures:
 *      HashMap<Integer, Node>  → O(1) key → node lookup
 *      Doubly-linked list      → O(1) move-to-MRU, O(1) LRU eviction
 *    Neither structure alone solves both needs; the composition is the trick.
 *  - Doubly-linked (not singly-linked) so any node can be unlinked in O(1)
 *    without walking the list from the head to find its predecessor.
 *  - Dummy head + tail sentinels eliminate all null-at-the-ends special cases.
 *    Every pointer update is unconditional.
 *  - `remove(Node)` + `addFirst(Node)` are the only places that touch
 *    `.next` / `.prev`. Every higher-level operation composes those two.
 *
 * Orientation used here:
 *     tail (sentinel) ⇄ LRU ⇄ ... ⇄ MRU ⇄ head (sentinel)
 *  - addFirst → insert just BEFORE head (MRU end).
 *  - Eviction → remove tail.next (LRU end).
 *
 * Pattern transfer: LFU cache (two-tier DLL), Time-based KV (TreeMap),
 * "Design" problems that need O(1) access + O(1) ordered bookkeeping.
 *
 * Java shortcut worth mentioning: LinkedHashMap(cap, 0.75f, true) +
 * override removeEldestEntry gives LRU in ~15 lines — but the manual
 * HashMap + DLL version is the interview gold standard.
 */
public class LRUCache {

    private final Map<Integer, Node> map = new HashMap<>();
    private final int cap;
    private final Node head = new Node(0, 0);   // MRU-side sentinel (rightmost)
    private final Node tail = new Node(0, 0);   // LRU-side sentinel (leftmost)

    public LRUCache(int capacity) {
        this.cap = capacity;
        tail.next = head;
        head.prev = tail;
    }

    public int get(int key) {
        Node node = map.get(key);
        if (node == null) return -1;
        remove(node);
        addFirst(node);
        return node.val;
    }

    public void put(int key, int value) {
        Node existing = map.get(key);
        if (existing != null) {
            existing.val = value;
            remove(existing);
            addFirst(existing);
            return;
        }
        Node n = new Node(key, value);
        map.put(key, n);
        addFirst(n);

        if (map.size() > cap) {
            Node lru = tail.next;
            remove(lru);
            map.remove(lru.key);
        }
    }

    // Insert just before `head` — that's the MRU end in this orientation.
    private void addFirst(Node node) {
        node.prev = head.prev;
        node.next = head;
        head.prev.next = node;
        head.prev = node;
    }

    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private static class Node {
        int key, val;
        Node prev, next;
        Node(int key, int val) { this.key = key; this.val = val; }
    }

    public static void main(String[] args) {
        LRUCache cache = new LRUCache(2);
        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println(cache.get(1));   // 1, promotes 1 to MRU
        cache.put(3, 3);                    // evicts 2 (LRU)
        System.out.println(cache.get(2));   // -1 (evicted)
        cache.put(4, 4);                    // evicts 1
        System.out.println(cache.get(1));   // -1
        System.out.println(cache.get(3));   // 3
        System.out.println(cache.get(4));   // 4
    }
}
