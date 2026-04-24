# Interview Prep Progress Tracker

Pattern-focused list. The ~10-12 core patterns below cover ~87% of senior/staff coding interviews.
Within each section, items are sorted **by priority (highest first)** and listed as **underlying algorithms/techniques** rather than concrete LeetCode problems. (Java-specific section at the bottom keeps concrete problem names, since those are design/concurrency scenarios rather than generic patterns.)

Target language: **Java**. Solutions live in `prep/<category>/`.

---

## 1. Graphs — traversal, cycle, topo, shortest path

- [x] **Iterative BFS with queue** — `prep/graphs/BFSGraph.java`
- [x] **Iterative DFS with explicit stack** — `prep/graphs/DFSGraph.java`
- [x] **Recursive DFS** — `prep/graphs/DFSGraphRecursive.java`
- [x] **Cycle detection — DFS with 3-color** — `prep/graphs/CycleDetection3Color.java`
- [x] **Topological sort — Kahn's BFS** — `prep/graphs/TopologicalSortKahn.java` (DFS reverse post-order covered in file header)
- [ ] **Alien Dictionary (LC 269)** — topo sort over string-derived edges; parsing + graph build + Kahn — staff-loop classic
- [x] **Kosaraju's SCC** — intuition only (full DFS forest + finish stack + transpose + peel); `prep/graphs/scc-visualization.html`
- [x] **Dijkstra — lazy PQ variant** — `prep/graphs/Dijkstra.java` (BFS with a PQ; first-pop-wins invariant; template for Network Delay / Cheapest Flights / Min Effort Path)

## 2. Union-Find

- [x] **DSU with path compression + union by rank** — `prep/unionfind/DSU.java`

## 3. Trees

- [x] **Pre-order** — `prep/trees/PreOrder.java`
- [x] **In-order** — `prep/trees/InOrder.java`
- [x] **Post-order** — `prep/trees/PostOrder.java`
- [x] **Tree recursion — bounds DOWN** — `prep/trees/ValidateBST.java` (caller tightens bounds; transfers to Range Sum of BST / Recover BST / in-range count)
- [x] **Tree recursion — values UP** — `prep/trees/MaxPathSum.java` (return one thing, side-effect a global with another; tuple-return variant unlocks House Robber III / Cameras / tree DP)
- [x] **Serialize / deserialize** — `prep/trees/SerializeDeserializeBinaryTree.java` (pre-order + null markers → invertible encoding; BFS variant for LC's own format; unlocks subtree hashing / duplicate subtrees)

## 6. Arrays (pattern-unique)

- [x] **Prefix / suffix products** — `prep/arrays/ProductExceptSelf.java` (two-pass O(1) extra)
- [x] **HashSet for O(n) sequences** — `prep/arrays/LongestConsecutive.java` (longest consecutive trick)

## 7. Linked List

- [x] **LRU Cache** — `prep/linkedlist/LRUCache.java` (HashMap + DLL with sentinels; the canonical "compose two data structures" problem)

## 8. Stack / Monotonic Stack

- [x] **Monotonic stack** — `prep/arrays/DailyTemperatures.java` (next-greater template; histogram is the same shape with "first smaller on both sides")

## 9. Heap / Priority Queue

- [x] **Top-K / K-way merge with heap** — `prep/heap/KthLargest.java` (min-heap of size K; K-way merge noted in header)
- [x] **Two heaps for running median** — `prep/heap/MedianFinder.java` (max-heap lower half + min-heap upper half, straddle-the-median)

## 11. Backtracking

- [x] **Subsets / combinations** — `prep/backtracking/Subsets.java` (functional-copy variant; mutate-and-restore noted in header as the form needed for grid/N-Queens)
- [x] **Grid backtracking** — `prep/backtracking/WordSearch.java` (mutate-and-restore with visited set; canonical `board[i][j]='#'` in-place marker noted in header)

## 12. Greedy / Intervals

- [x] **Intervals — sweep line with events** — `prep/intervals/MergeIntervalsSweepLine.java` (PQ + START-before-END tie-break; template for Meeting Rooms II / Skyline)

## 13. Tries

- [x] **Trie implementation** — `prep/trie/Trie.java` + `prep/trie/trie-visualization.html` (revisit, not hand-rolled)

---

## 🟢 Java-specific (add-ons for Databricks / Netflix / Anthropic / staff loops)

### Concurrency — Java's killer area (`java.util.concurrent`)

- [x] **Bounded Blocking Queue (LC 1188)** — `prep/concurrency/BoundedBlockingQueue.java` (`ReentrantLock` + `notFull`/`notEmpty`; `while`-loop on predicate; producer signals consumer-condition and vice versa; same shape as `ArrayBlockingQueue`)
- [x] **Parallel Task Runner with deps** — `prep/concurrency/TaskRunner.java` (Kahn + pool, wave-barrier variant; canonical event-driven form + `CompletableFuture` composition in header; cycle detection + try/finally-on-latch are non-optional)
- [ ] **Token Bucket rate limiter** — `ScheduledExecutorService` + `Semaphore`

### Production-flavored design (frontier labs + Shopify)

- [ ] **Time-based Key-Value Store (LC 981)** — `TreeMap.floorEntry`
- [ ] **LFU Cache (LC 460)** — two-tier DLL
- [ ] **Transactional in-memory store** — nested `BEGIN`/`COMMIT`/`ROLLBACK`, overlay stack
- [ ] **Spreadsheet with formula evaluation** — topo sort + cycle detection + recompute

---

## 🗄️ Confidence-trimmed — patterns dropped because I'm already comfortable

Not active prep — kept here so coverage is documented and I can re-visit any box if I want a refresher. Without these, raw interview coverage is ~65-70%; with them, ~90%+. Each one is top-5 frequency in senior/staff loops.

### Sliding window
- [x] **Fixed-size window** — max sum of size K, averages
- [x] **Variable-size window** — `prep/sliding-window/MinimumWindowSubstring.java` (shrink-while-valid template; expand unconditionally, shrink conditionally, record before each shrink)

### Two pointers
- [x] **Converging pointers on sorted array** — 3Sum, Container With Most Water, Valid Palindrome
- [x] **Fast / slow pointers** — Floyd's cycle detection, find middle of linked list, remove Nth from end

### Binary search
- [x] **Classic binary search on sorted array** — plus rotated-sorted variant, find peak, search 2D matrix
- [x] **Binary search on the answer space** — `prep/binary-search/KokoEatingBananas.java` (monotone predicate over candidate answers; template transfers to Capacity to Ship / Split Array Largest Sum / Aggressive Cows)

### Dynamic programming
- [x] **1D DP — House Robber / Coin Change / Word Break / Decode Ways** — confident, skip drilling
- [x] **1D DP — LIS** — `prep/dp/LIS.java` (O(n²) suffix DP primary; O(n log n) patience-sort in header — `tails` is NOT the LIS, only its length matters)

_2D DP (Edit Distance / Unique Paths / Knapsack / DP on strings & intervals) — dropped from prep. Lower frequency at senior/staff than concurrency + production design; carry template shape (`dp[i][j]` on prefix lengths, branch on last-char match vs edit ops) to derive cold if needed._

### Shortest path (weighted)
- [x] **Bellman-Ford / negative edges** — only if frontier-lab / systems-heavy loop

### Linked list fundamentals
- [x] **Reverse a linked list** — iterative + recursive; feeds into reverse-in-groups, palindrome check
- [x] **Merge two sorted lists** — building block for merge-K-lists (heap or divide-conquer)
- [x] **Reorder list / find middle / detect cycle** — Floyd's tortoise-and-hare
- [x] **Copy List with Random Pointer** — `prep/linkedlist/CopyRandomList.java` (hashmap primary; interleave-and-split O(1)-space trick in header)