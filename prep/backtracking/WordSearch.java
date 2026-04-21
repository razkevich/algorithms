package prep.backtracking;

import java.util.ArrayList;
import java.util.List;

/**
 * Word Search (LC 79) — grid backtracking with mutate-and-restore.
 *
 * Pattern this teaches:
 *  - DFS over a grid where the recursion path IS the "chosen" state. You must
 *    mark a cell as visited BEFORE recursing and un-mark it AFTER, so siblings
 *    in the search tree see a clean board. This is the "restore" step that
 *    Subsets' functional-copy variant can skip — for grid-sized state, copying
 *    per call is infeasible, so mutate-and-restore is mandatory.
 *
 * Transfer to: N-Queens (restore column/diagonal), Sudoku (restore cell),
 * Rat in a Maze, All Paths in a Grid — same shape, different "can I place?" check.
 *
 * This file preserves the user's shape (wordSoFar accumulator + List<String>
 * visited) with minimal bug fixes so the original intent is readable.
 * Notes on what's vestigial / non-idiomatic:
 *  - `wordSoFar` is redundant: if we match char-by-char via `word.charAt(wordIndex)`,
 *    reaching wordIndex == word.length()-1 already implies success. Kept here
 *    because the user's shape used it as a sanity check.
 *  - `List<String> visited` with "x y" keys is O(n) per contains() — prefer
 *    `boolean[][] visited` (O(1) lookup) or the in-place `board[i][j] = '#'`
 *    marker + restore trick (no extra memory at all). See canonical note below.
 *
 * Canonical form (most idiomatic for Word Search specifically):
 *   mark board[i][j] = '#' before recursing; restore original char after.
 *   No visited array needed — the board itself is the visited set.
 *
 * Complexity: O(m·n · 4^L) worst case, where L = word.length().
 */
public class WordSearch {

    public boolean exist(char[][] board, String word) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (exist(board, word, i, j, "", 0, new ArrayList<>())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean exist(char[][] board, String word,
                          int startX, int startY,
                          String wordSoFar, int wordIndex,
                          List<String> visited) {
        // bounds — must come before any board access
        if (startX < 0 || startX >= board.length
                || startY < 0 || startY >= board[0].length) {
            return false;
        }
        String pos = "%s %s".formatted(startX, startY);
        if (visited.contains(pos)) {
            return false;
        }
        // match current cell against the char we need NEXT in `word`
        if (board[startX][startY] != word.charAt(wordIndex)) {
            return false;
        }

        wordSoFar = wordSoFar + board[startX][startY];
        if (wordIndex == word.length() - 1) {
            return word.equals(wordSoFar);
        }

        // mutate-and-restore: mark visited, recurse 4-directionally, un-mark
        visited.add(pos);
        boolean e1 = exist(board, word, startX + 1, startY, wordSoFar, wordIndex + 1, visited);
        boolean e2 = exist(board, word, startX - 1, startY, wordSoFar, wordIndex + 1, visited);
        boolean e3 = exist(board, word, startX, startY + 1, wordSoFar, wordIndex + 1, visited);
        boolean e4 = exist(board, word, startX, startY - 1, wordSoFar, wordIndex + 1, visited);
        visited.remove(pos);

        return e1 || e2 || e3 || e4;
    }

    public static void main(String[] args) {
        char[][] board = {
                {'A', 'B', 'C', 'E'},
                {'S', 'F', 'C', 'S'},
                {'A', 'D', 'E', 'E'}
        };
        WordSearch s = new WordSearch();
        System.out.println(s.exist(board, "ABCCED")); // true
        System.out.println(s.exist(board, "SEE"));    // true
        System.out.println(s.exist(board, "ABCB"));   // false (can't reuse 'B')
    }
}
