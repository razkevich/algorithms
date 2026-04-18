# Interview Prep Progress Tracker

Pattern-focused list. The ~10-12 core patterns below cover ~87% of senior/staff coding interviews.
Within each section, problems are sorted **by priority (highest first)**.

Target language: **Java**. Solutions live in `prep/<category>/`.

---

## 1. Graphs — traversal, cycle, topo, shortest path

- [x] **Iterative BFS with queue** — `prep/graphs/BFSGraph.java`
- [x] **Iterative DFS with explicit stack** — `prep/graphs/DFSGraph.java`
- [x] **Recursive DFS** — `prep/graphs/DFSGraphRecursive.java`
- [x] **Cycle detection — DFS with 3-color** — `prep/graphs/CycleDetection3Color.java`
- [ ] **Number of Islands (LC 200)** — grid BFS/DFS template
- [ ] **Course Schedule (LC 207)** — Kahn's BFS topo sort + cycle
- [ ] **Course Schedule II (LC 210)** — topo sort output
- [ ] **Alien Dictionary (LC 269)** — applied topo sort
- [ ] **Clone Graph (LC 133)** — DFS/BFS + hashmap
- [ ] **Word Ladder (LC 127)** — BFS on implicit graph
- [ ] **Network Delay Time (LC 743)** — Dijkstra
- [ ] **Cheapest Flights Within K Stops (LC 787)** — modified Dijkstra / Bellman-Ford
- [ ] **Kosaraju's SCC** — two passes on transpose

## 2. Union-Find

- [ ] **Accounts Merge (LC 721)** — DSU + string processing, staff-canonical
- [ ] **Number of Provinces (LC 547)** — foundational DSU template
- [ ] **Redundant Connection (LC 684)** — cycle via DSU

## 3. Trees

- [x] **Pre-order** — `prep/trees/PreOrder.java`
- [x] **In-order** — `prep/trees/InOrder.java`
- [x] **Post-order** — `prep/trees/PostOrder.java`
- [ ] **Validate BST (LC 98)** — passing bounds down
- [ ] **Binary Tree Level Order Traversal (LC 102)** — BFS on trees
- [ ] **Lowest Common Ancestor (LC 236)**
- [ ] **Binary Tree Maximum Path Sum (LC 124)** — return one, track another
- [ ] **Serialize and Deserialize Binary Tree (LC 297)**

## 4. Two Pointers / Sliding Window

- [ ] **Longest Substring Without Repeating Characters (LC 3)**
- [ ] **3Sum (LC 15)**
- [ ] **Minimum Window Substring (LC 76)** — sliding window template
- [ ] **Trapping Rain Water (LC 42)**

## 5. Binary Search

- [ ] **Find First and Last Position (LC 34)** — lower/upper bound template
- [ ] **Koko Eating Bananas (LC 875)** — binary search on answer
- [ ] **Search in Rotated Sorted Array (LC 33)**
- [ ] **Median of Two Sorted Arrays (LC 4)**

## 6. Arrays (pattern-unique)

- [ ] **Product of Array Except Self (LC 238)** — prefix/suffix product
- [ ] **Longest Consecutive Sequence (LC 128)** — HashSet trick
- [ ] **Single Number (LC 136)** — XOR
- [ ] **Longest Palindromic Substring (LC 5)** — expand around center

## 7. Linked List

- [ ] **LRU Cache (LC 146)** — HashMap + DLL composition
- [ ] **Reverse Linked List (LC 206)** — iterative + recursive
- [ ] **Linked List Cycle II (LC 142)** — Floyd's algorithm
- [ ] **Merge K Sorted Lists (LC 23)** — heap-based merge
- [ ] **Remove Nth Node from End (LC 19)** — fast/slow with offset

## 8. Stack / Monotonic Stack

- [ ] **Largest Rectangle in Histogram (LC 84)** — THE monotonic stack problem
- [ ] **Valid Parentheses (LC 20)**
- [ ] **Min Stack (LC 155)** — auxiliary state

## 9. Heap / Priority Queue

- [ ] **Top K Frequent Elements (LC 347)**
- [ ] **Kth Largest in Array (LC 215)** — heap vs quickselect
- [ ] **Find Median from Data Stream (LC 295)** — two heaps

## 10. Dynamic Programming

- [ ] **Coin Change (LC 322)** — unbounded knapsack
- [ ] **Longest Increasing Subsequence (LC 300)** — patience sort / BS variant
- [ ] **Edit Distance (LC 72)** — 2D string DP
- [ ] **Maximum Subarray (LC 53)** — Kadane's
- [ ] **Word Break (LC 139)**
- [ ] **House Robber (LC 198)** — 1D DP
- [ ] **Longest Common Subsequence (LC 1143)**
- [ ] **Unique Paths II (LC 63)** — 2D grid DP

## 11. Backtracking

- [ ] **Subsets (LC 78)** — choose/not-choose template
- [ ] **Permutations (LC 46)** — swap-in-place template
- [ ] **Word Search (LC 79)** — grid backtracking
- [ ] **Combination Sum (LC 39)** — with-repetition template
- [ ] **N-Queens (LC 51)**

## 12. Greedy / Intervals

- [ ] **Merge Intervals (LC 56)**
- [ ] **Meeting Rooms II (LC 253)**
- [ ] **Jump Game II (LC 45)**

## 13. Tries

- [ ] **Implement Trie (LC 208)**
- [ ] **Word Search II (LC 212)** — Trie + backtracking

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

### Java idioms cheat-sheet (review, no code files needed)

- `TreeMap` — `floorKey` / `ceilingKey` for range queries
- `LinkedHashMap` with `accessOrder=true` → LRU in ~15 lines
- `PriorityQueue` with custom `Comparator`
- `ArrayDeque` as stack/queue (**never** `Stack` or `LinkedList`)
- `ConcurrentHashMap.compute` / `merge` for atomic compound ops
- `Collections.binarySearch` return convention: `-(insertion point) - 1`
