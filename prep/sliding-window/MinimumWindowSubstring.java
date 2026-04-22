package prep.sliding_window;

import java.util.HashMap;
import java.util.Map;

/**
 * Minimum Window Substring (LC 76) — variable-size sliding window,
 * shrink-while-valid template.
 *
 * The template this teaches:
 *  - Expand `end` UNCONDITIONALLY: every character of s is pulled into
 *    the window exactly once (→ O(|s|) amortized).
 *  - Shrink `start` CONDITIONALLY: only while the window stays valid.
 *    Each character is removed at most once (→ still O(|s|)).
 *  - Record the answer when the window is valid, right before you shrink.
 *
 * The invariant framing (what prevents bugs):
 *  - "Valid" = sCounts contains tCounts with at-least multiplicity.
 *  - Expand phase: the window is moving toward validity (or past it).
 *  - Shrink phase: the window is still valid right now; this is when
 *    you record a candidate answer. Shrinking one more step may break
 *    validity, which kicks you back to expanding.
 *
 * Canonical two-loop form (alternative to this file's single-loop shape):
 *
 *     for (int end = 0; end < n; end++) {
 *         addChar(sCounts, s.charAt(end));
 *         while (contains(tCounts, sCounts)) {     // valid window
 *             if (end - start + 1 < best) update;   // record BEFORE shrinking
 *             removeChar(sCounts, s.charAt(start));
 *             start++;
 *         }
 *     }
 *
 * The single-loop form preserved below is equivalent; the two-loop form
 * is more common in write-ups because the "expand/shrink phases" are
 * structurally separated.
 *
 * Performance note (not applied here to preserve shape):
 *  - `notEnoughChars` iterates tCounts every call — O(alphabet) per step.
 *    For ASCII this is effectively O(1), but for large alphabets prefer a
 *    `matched` counter: increment when a char's sCount first reaches its
 *    tCount; decrement when it drops back below. Then "valid?" is a single
 *    `matched == distinct-chars-in-t` check.
 *
 * Pattern transfer — same shrink-while-valid template, different invariant:
 *  - Longest Substring Without Repeating Characters — shrink while duplicate.
 *  - Longest Substring with At Most K Distinct — shrink while distinct > k.
 *  - Minimum Size Subarray Sum — shrink while sum ≥ target.
 *  - Permutation in String / Find All Anagrams — same counts framework,
 *    fixed-size window variant.
 *
 * Complexity: O(|s| + |t|) time, O(alphabet) space.
 */
public class MinimumWindowSubstring {

    public String minWindow(String s, String t) {
        if (s.length() < t.length()) return "";

        String result = null;
        Map<Character, Integer> tCounts = stringToCountsMap(t);
        Map<Character, Integer> sCounts = new HashMap<>();
        int start = 0, end = 0;

        // window is half-open [start, end); length = end - start.
        // outer condition allows one tick past full expansion so a still-valid
        // window can keep shrinking toward its minimum.
        while (end <= s.length()) {
            if (notEnoughChars(tCounts, sCounts)) {
                if (end == s.length()) break;        // can't expand; nothing left
                addChar(sCounts, s.charAt(end));     // read THEN advance
                end++;
            } else {
                // window [start, end) is valid — record before shrinking
                if (result == null || end - start < result.length()) {
                    result = s.substring(start, end);
                }
                removeChar(sCounts, s.charAt(start));
                start++;
            }
        }
        return result == null ? "" : result;
    }

    private static Map<Character, Integer> stringToCountsMap(String t) {
        Map<Character, Integer> m = new HashMap<>();
        for (char c : t.toCharArray()) m.merge(c, 1, Integer::sum);
        return m;
    }

    private static void addChar(Map<Character, Integer> m, char c) {
        m.merge(c, 1, Integer::sum);
    }

    private static void removeChar(Map<Character, Integer> m, char c) {
        Integer v = m.get(c);
        if (v == null) return;
        if (v == 1) m.remove(c);
        else m.put(c, v - 1);
    }

    private static boolean notEnoughChars(Map<Character, Integer> need,
                                          Map<Character, Integer> have) {
        for (var e : need.entrySet()) {
            if (have.getOrDefault(e.getKey(), 0) < e.getValue()) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        MinimumWindowSubstring s = new MinimumWindowSubstring();
        System.out.println(s.minWindow("ADOBECODEBANC", "ABC"));  // BANC
        System.out.println(s.minWindow("a", "a"));                 // a
        System.out.println(s.minWindow("a", "aa"));                // (empty)
        System.out.println(s.minWindow("ab", "b"));                // b
        System.out.println(s.minWindow("aa", "aa"));               // aa
        System.out.println(s.minWindow("cabwefgewcwaefgcf", "cae"));// cwae
    }
}
