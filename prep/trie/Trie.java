package prep.trie;

/**
 * Trie (Prefix Tree) — insert / search / startsWith in O(L) per op.
 *
 * Key insights / interview points:
 *  - Edges carry characters, nodes carry no character. Path root → node IS the string.
 *  - `isWord` is the whole difference between search and startsWith. Both walk the
 *    same path; search additionally requires the landing node's isWord flag to be true.
 *  - A single node can be BOTH a word-end AND a passthrough prefix for longer words
 *    (e.g. "car" → "care", "cars").
 *  - Fixed Node[26] is fastest for a-z; swap to HashMap<Character, Node> for
 *    unicode or sparse alphabets. Interviewers ask which and why — articulate
 *    the memory-vs-lookup tradeoff.
 *  - Complexity is O(L) for L = string length. No `n` in the bound — query cost
 *    is independent of how many words are stored.
 *
 * Delete is NOT implemented here. Most interviewers don't ask; if they do, the
 * trick is recursive: after unsetting isWord at the leaf, unlink any ancestor
 * whose subtree becomes empty (no children, not a word).
 *
 * Pattern transfer:
 *  - Word Search II — trie of targets + grid DFS walking trie in lockstep; prune
 *    any DFS branch with no matching child.
 *  - Auto-complete / prefix enumeration — walk(prefix) lands at a subtree;
 *    DFS emits every descendant with isWord.
 *  - Replace Words — walk input, stop at first isWord node encountered.
 *  - Word Break — extend DP transitions through trie instead of iterating the dict.
 *  - Max XOR of Two Numbers — BINARY trie (children[2] on bit paths);
 *    greedy opposite-bit walk turns O(n^2) pairwise into O(n·32).
 */
public class Trie {

    private static class Node {
        Node[] children = new Node[26];
        boolean isWord;
    }

    private final Node root = new Node();

    public void insert(String word) {
        Node cur = root;
        for (char c : word.toCharArray()) {
            int i = c - 'a';
            if (cur.children[i] == null) cur.children[i] = new Node();
            cur = cur.children[i];
        }
        cur.isWord = true;
    }

    public boolean search(String word) {
        Node n = walk(word);
        return n != null && n.isWord;
    }

    public boolean startsWith(String prefix) {
        return walk(prefix) != null;
    }

    private Node walk(String s) {
        Node cur = root;
        for (char c : s.toCharArray()) {
            cur = cur.children[c - 'a'];
            if (cur == null) return null;
        }
        return cur;
    }

    public static void main(String[] args) {
        Trie t = new Trie();
        for (String w : new String[]{"cat", "car", "care", "cars", "do", "dog"}) {
            t.insert(w);
        }

        System.out.println(t.search("car"));        // true  (isWord on 'r')
        System.out.println(t.search("ca"));         // false (reachable, but isWord = false)
        System.out.println(t.startsWith("ca"));     // true
        System.out.println(t.search("card"));       // false (no 'd' child of 'r')
        System.out.println(t.startsWith("card"));   // false
        System.out.println(t.search("do"));         // true
        System.out.println(t.search("dog"));        // true
        System.out.println(t.startsWith("cow"));    // false
    }
}
