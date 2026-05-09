# Consistent Hashing — Distributed KV / Cache / LB

> **Pattern home**: any system that maps keys to a set of nodes and must survive node churn cheaply. Distributed caches (Memcached, Redis Cluster), KV stores (Dynamo, Cassandra, ScyllaDB, Riak), sticky-session load balancers (Envoy, Maglev), CDN edge selection, gRPC client-side LB.

> **Direction cheat-sheet**:
> - **Hash mod N**: simple, *every* key reshuffles when N changes. Disqualified for any growable cluster.
> - **Range partitioning**: range scans cheap, hotspots from sequential keys. Good for ordered data (BigTable, HBase, Cockroach).
> - **Consistent hashing (ring)**: only **1/N keys move** on add/remove. Default for hash-partitioned KV / cache / unordered LB.
> - **Maglev / lookup-table hashing**: constant-time lookup, near-perfect balance. Default for L4/L7 LBs (Envoy, GLB).

For depth, this cheat sheet is the **whiteboard cadence**. Reference reading lives in `concepts/distributed-and-platform.html` (search "consistent.hash") — it has the QA/probing format and broader partitioning context.

---

## Why this pattern matters

Two questions every distributed system has to answer:

1. **Given a key, which node owns it?**
2. **What happens to ownership when a node joins or leaves?**

Naive `hash(key) % N` answers (1) cleanly but makes (2) catastrophic — change N, and ~all keys remap. For a 100-node cache, adding the 101st node invalidates ~99% of cached entries; the cache cold-starts at the worst possible moment (when you're trying to scale up under load).

Consistent hashing decouples the two: ownership shifts only at the *boundary* between the new node and its neighbors. Adding the 101st node moves ~1/101 of keys. Removing a node moves ~1/N to its successor. Cluster scaling becomes a routine operation.

---

## The mechanism (60-second whiteboard cadence)

```
        node B
          ●
    ┌──────────┐
   /            \
  /              \
node A          node C
  ●                ●
  \              /
   \            /
    └──────────┘
        node D
          ●
```

1. Hash both keys *and* nodes into the same fixed-size space (e.g., 2^160 with SHA-1, or 2^64 with murmur3). Visualize as a ring (positions modulo the space).
2. **Key ownership rule**: each key is owned by the *first node clockwise* from the key's position.
3. **On node add**: a new node B' lands somewhere on the ring. The keys it now owns are the ones that previously belonged to its clockwise neighbor — only that arc moves. Other nodes are untouched.
4. **On node remove**: B's keys all migrate to B's clockwise successor.

That's it. The "ring" is a metaphor — implementations are usually `SortedMap<HashValue, Node>` with `ceilingKey(hash(key))` for lookup. O(log N) per lookup; node changes update only the affected arc.

---

## Virtual nodes — the variance fix

Pure consistent hashing has a problem: with few nodes, the ring is unbalanced. 4 random points on a ring don't divide it into 4 equal arcs — one node ends up owning 40% of keys, another 10%.

**Fix**: each *physical* node owns many ring positions (e.g., 256). `node_A_v0`, `node_A_v1`, … `node_A_v255` all hash to different positions. Total ring has N×V positions. Law of large numbers smooths the distribution.

```python
# Ring construction
for node in nodes:
    for i in range(256):  # 256 vnodes per physical node
        ring[hash(node.id + ":" + str(i))] = node

# Lookup
def find_node(key):
    h = hash(key)
    return ring.first_clockwise_after(h)
```

**Why 256 (and not 16 or 4096)?** Empirical sweet spot. Below ~64, variance is still notable. Above ~512, ring memory and lookup CPU grow without further smoothing benefit. Cassandra's default is 256; DynamoDB doesn't expose it (managed). Treat 128–512 as the right band; tune only if metrics demand.

**Bonus benefit**: vnodes spread *the load of a removed node* across all surviving physical nodes (each surviving node absorbs ~1/N of the lost node's keys via its scattered vnodes). Without vnodes, the entire ex-neighbor's load lands on a single successor.

---

## Bounded-load consistent hashing — the hot-backend fix

Even with vnodes, **traffic** can be skewed: one key (or a few keys hashing to the same arc) gets disproportionate hits. Standard consistent hashing doesn't care about load — once a key's owner is fixed, that node bears all the key's traffic.

**Fix (Mirage / Vimeo, used by Envoy)**: cap each node at `(1+ε) × avg_load`. If a request arrives at node N and N is already at capacity, route to the *next* node clockwise instead. ε is the slack (typical: 0.25–1.0).

Trade-off: gives up strict key→node affinity for predictable max-load. Cache hit rate dips during overflow (the next-node fallback wasn't expecting these keys), but tail latency is bounded.

---

## Maglev hashing — Google's variant

Different shape, same goals: minimum disruption + balance + **O(1) lookup**.

Build a fixed-size lookup table (e.g., 65537 slots). Each backend produces a permutation of slot indices; populate the table by round-robin assignment per backend. Lookup = `table[hash(key) % len(table)]`. Constant-time; no tree traversal.

**On node add/remove**: rebuild the table. Disruption is proportionally minimal (most slots keep the same backend). Used by Envoy and Google's GLB. Pick this over ring-based when (a) lookup latency dominates and (b) lookups vastly outnumber topology changes (true for LBs).

| Variant | Lookup cost | Balance | Use case |
|---|---|---|---|
| **Plain ring** | O(log N) | Poor with few nodes | Pedagogical / small clusters |
| **Ring + vnodes** | O(log NV) | Good (V ≥ 128) | Cassandra, Dynamo, Redis Cluster |
| **Bounded-load** | O(log NV) + retry | Bounded max load | Envoy, hot-key-prone services |
| **Maglev** | O(1) | Near-perfect | L4/L7 LBs, GLB, Envoy |

---

## Where consistent hashing appears (pattern transfer)

| System | What gets hashed | What's a "node" |
|---|---|---|
| **Memcached / Redis Cluster** | cache keys | cache server |
| **Cassandra / Dynamo / Scylla** | partition keys | storage node (replicas via N successors) |
| **Sticky-session LB** | session/user/tenant ID | backend instance |
| **CDN edge selection** | content URL | edge POP |
| **gRPC client LB (ring_hash)** | request key (header) | upstream pod |
| **Distributed locks** _(careful)_ | lock name | lock service replica |

In a system-design interview, when someone says "we shard the cache" or "keep user sessions on the same backend without sticky cookies" or "replicate KV with N copies" — consistent hashing is part of the answer.

---

## Edge cases the interviewer probes

### 1. Replication on top of the ring
Most KV stores don't store one copy at the owning node — they store **N successors clockwise** (replication factor N). Read with quorum R, write with quorum W, where R+W > N for strong consistency. Cassandra and Dynamo both do this. Vnodes scatter the replicas across distinct physical nodes — no awareness needed in the lookup.

**Trap**: with vnodes, the next V vnodes might all be on the *same physical node* if you're unlucky. Implementations skip duplicates: walk the ring until you've collected N *distinct* physical nodes.

### 2. Rack / AZ awareness
"Replication factor 3" without rack-awareness might place all 3 copies in the same AZ — single AZ outage = data loss. Real implementations (Cassandra `NetworkTopologyStrategy`, DynamoDB) constrain successor selection to span availability zones. State this explicitly in interviews.

### 3. Hot key — vnodes don't fix it
A single super-hot key (the celebrity, the trending product) hashes to *one* vnode regardless of how many vnodes you have. You need application-level mitigation:
- **Replicate the key** across multiple owners; clients read any random replica.
- **Front the key with a local cache** at the request fan-in (LRU per app server).
- **Bounded-load LB** for stateless workloads with skewed key access.
Vnodes solve *node-level* variance. Hot key is a *key-level* problem.

### 4. Adding 1 node vs. doubling the cluster
Going from N=10 → 11: 1/11 of keys move (mostly off one neighbor). Going from N=10 → 20: ~1/2 of keys move (each new node steals keys from its arc-neighbor). The rule is "1/N moves on add" but you scale by adding *each* node — total movement on a doubling is summed across all the adds.

### 5. Drift on node restart
Stateless cache: a node restart (loses its in-memory state) is *equivalent* to remove + re-add. Clients keep hashing keys to the same node ID, but the node has no data — it cold-starts. Mitigation: short-window double-write to the next successor (Memcached client lib feature) or accept the brief miss-rate spike.

### 6. Why not sticky-modulo-some-large-prime?
"We have 1000 buckets, fixed; assign buckets to nodes." This is *bucket-based* consistent hashing — a degenerate case with V=1000/N, infinitely-fine but pre-allocated. It works (Riak, Vitess use variants). The trade-off: rebalancing is now bucket-granularity (good for tooling), but the pre-allocation forces a max-cluster-size decision. Interview answer: equivalent to vnoded ring with extra steps; ring+vnode is the canonical default unless you have an operational reason for bucket-explicit.

---

## Interview quick-fire

- **"What's the key property of consistent hashing?"** → Adding/removing a node remaps only ~1/N of keys, not all of them.
- **"How does the ring work?"** → Hash keys and nodes into the same space; each key goes to the next node clockwise.
- **"Why virtual nodes?"** → Smooths variance with few physical nodes (V≈256 per physical), and spreads removed-node load across all survivors instead of dumping it on one neighbor.
- **"What's bounded-load consistent hashing?"** → Caps each node at (1+ε)×avg; overflow goes to next node clockwise. Trades cache-hit rate for tail-latency bound. Used by Envoy.
- **"Maglev vs ring?"** → Maglev: constant-time lookup, near-perfect balance, expensive table rebuild on topology change. Ring + vnodes: O(log NV), incremental updates. Maglev for LBs (lookup-heavy); ring for storage (topology changes more frequent).
- **"How do KV stores replicate on the ring?"** → Walk clockwise, take next N *distinct physical* nodes (skip vnode duplicates). Add rack/AZ-spread constraint for fault isolation.
- **"What about a hot key?"** → Vnodes don't help. Replicate the key, front-cache it, or use bounded-load. Hot-*partition* fixes are different from hot-*node* fixes.
- **"Why not just `hash(key) % N`?"** → ~all keys remap when N changes. Cache cold-starts on every scale event. Disqualified.
- **"Why 256 vnodes?"** → Empirical sweet spot for variance reduction without ring memory blowup. 128–512 is the band; below 64 is noticeably uneven.
- **"How do you handle a permanently-failed node's data?"** → Successor takes over (read latency stays sub-ms). Background process re-replicates from surviving copies to restore replication factor; until then you're at N-1 copies. Hinted handoff (Cassandra/Dynamo) holds writes for a transient outage.

---

## Pattern transfer summary

Whenever the design has the shape **"map keys to a set of changing nodes, want minimal disruption on change"**, consistent hashing is the answer. The variants exist because different consumers care about different costs:

- **Storage** (Cassandra/Dynamo) — incremental topology, replication via successors, vnodes for variance. Ring + vnodes wins.
- **L4/L7 LB** (Envoy, GLB) — lookups dominate, topology changes are rare events. Maglev wins.
- **Hot-key-prone services** (auth tokens, user-routed gRPC) — bounded-load wins.

**Default if you forget**: ring + 256 vnodes. State it confidently; interviewer will prompt you toward variants if relevant.
