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
- [x] **Kosaraju's SCC** — intuition only (full DFS forest + finish stack + transpose + peel); `prep/graphs/scc-visualization.html`

## 2. Union-Find

- [x] **DSU with path compression + union by rank** — `prep/unionfind/DSU.java`

## 3. Trees

- [x] **Pre-order** — `prep/trees/PreOrder.java`
- [x] **In-order** — `prep/trees/InOrder.java`
- [x] **Post-order** — `prep/trees/PostOrder.java`
- [ ] **Tree recursion with state** — bounds **down** (BST validation) and values **up** (Tree DP, LCA, max path sum)
- [ ] **Serialize / deserialize** — pre-order with null markers (the one non-recursion tree skill — encoding/parsing)

## 6. Arrays (pattern-unique)

- [x] **Prefix / suffix products** — `prep/arrays/ProductExceptSelf.java` (two-pass O(1) extra)
- [x] **HashSet for O(n) sequences** — `prep/arrays/LongestConsecutive.java` (longest consecutive trick)

## 7. Linked List

- [x] **LRU Cache** — `prep/linkedlist/LRUCache.java` (HashMap + DLL with sentinels; the canonical "compose two data structures" problem)

## 8. Stack / Monotonic Stack

- [x] **Monotonic stack** — `prep/arrays/DailyTemperatures.java` (next-greater template; histogram is the same shape with "first smaller on both sides")

## 9. Heap / Priority Queue

- [ ] **Top-K / K-way merge with heap** — heap of size K, or quickselect for top-K
- [ ] **Two heaps for running median** — max-heap + min-heap balance

## 11. Backtracking

- [ ] **Subsets / combinations** — choose / not-choose template (with-repetition is the same with an index tweak)
- [ ] **Grid backtracking** — DFS with visited marker and restore

## 12. Greedy / Intervals

- [ ] **Intervals — sort + sweep** — merge intervals, meeting rooms (min-heap of end times or sweep line)

## 13. Tries

- [ ] **Trie implementation** — insert / search / startsWith (+ optional: combine with grid backtracking for multi-word search)

---

## 🟢 Java-specific (add-ons for Databricks / Netflix / Anthropic / staff loops)

### Concurrency — Java's killer area (`java.util.concurrent`)

- [ ] **Bounded Blocking Queue (LC 1188)** — `ReentrantLock` + two `Condition`s
- [ ] **Web Crawler Multithreaded (LC 1242)** — `ExecutorService` + `ConcurrentHashMap`
- [ ] **Thread-safe LRU** — extend LRU with `ReentrantReadWriteLock`
- [ ] **Token Bucket rate limiter** — `ScheduledExecutorService` + `Semaphore`

### Production-flavored design (frontier labs + Shopify)

- [ ] **Time-based Key-Value Store (LC 981)** — `TreeMap.floorEntry`
- [ ] **LFU Cache (LC 460)** — two-tier DLL
- [ ] **Transactional in-memory store** — nested `BEGIN`/`COMMIT`/`ROLLBACK`, overlay stack
- [ ] **Spreadsheet with formula evaluation** — topo sort + cycle detection + recompute
- [ ] **Design Twitter (LC 355)** — heap + hash composition

---

## 🗄️ Confidence-trimmed — patterns dropped because I'm already comfortable

Not active prep — kept here so coverage is documented and I can re-visit any box if I want a refresher. Without these, raw interview coverage is ~65-70%; with them, ~90%+. Each one is top-5 frequency in senior/staff loops.

### Sliding window
- [ ] **Fixed-size window** — max sum of size K, averages
- [ ] **Variable-size window** — longest substring without repeating chars, min window substring (shrink while invariant violated)

### Two pointers
- [ ] **Converging pointers on sorted array** — 3Sum, Container With Most Water, Valid Palindrome
- [ ] **Fast / slow pointers** — Floyd's cycle detection, find middle of linked list, remove Nth from end

### Binary search
- [ ] **Classic binary search on sorted array** — plus rotated-sorted variant, find peak, search 2D matrix
- [ ] **Binary search on the answer space** — Koko Eating Bananas, Split Array Largest Sum, Capacity to Ship Packages (the "is X feasible?" → monotone predicate trick)

### Dynamic programming
- [ ] **1D DP** — House Robber, Coin Change, Longest Increasing Subsequence (O(n log n) with patience-sort variant worth knowing)
- [ ] **2D DP** — Unique Paths, Longest Common Subsequence, Edit Distance, 0/1 Knapsack
- [ ] **DP on strings / intervals** — palindrome partitioning, matrix chain, burst balloons (trickier — only if time allows)

### Shortest path (weighted)
- [ ] **Dijkstra with PQ** — Network Delay Time, Cheapest Flights Within K Stops, Path With Minimum Effort
- [ ] **Bellman-Ford / negative edges** — only if frontier-lab / systems-heavy loop

### Linked list fundamentals
- [ ] **Reverse a linked list** — iterative + recursive; feeds into reverse-in-groups, palindrome check
- [ ] **Merge two sorted lists** — building block for merge-K-lists (heap or divide-conquer)
- [ ] **Reorder list / find middle / detect cycle** — Floyd's tortoise-and-hare
- [ ] **Copy List with Random Pointer** — interleave-and-split trick or HashMap