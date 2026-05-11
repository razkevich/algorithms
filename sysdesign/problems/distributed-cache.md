# Distributed Cache (Redis-like) — Day 3 Mock

**Date:** 2026-05-11 · **Day:** 3 / 10 · **Mock #:** 3 · **Status:** ✅ complete (clock-off mode)

---

## 🔒 Round agreement (locked pre-mock)

> Goal: **(a) ring + vnodes flow naturally in HLD without prompting — the cheat-sheet was just written, bar is reflexive; (b) volunteer eviction, cache-aside vs write-through, and stampede mitigation in Stage 5 without probing — Day 1 hot-key redo.**

Pre-mock review: stages recited (time caps missed). SCALDS + COO correct but stage-binding omitted. DB toolbox: missed Kafka, S3, Spanner; got MongoDB (not canonical — Cassandra is the wide-column pick). Stakes (item 4): "sharding and replication, theoretical foundation" — too thin; didn't predict eviction / cache patterns / stampede as the likely probe areas.

---

## ✅ Stage 1 — Requirements (closed, with pushback)

### Functional (committed top set)
1. Get or update a key
2. Manual delete / evict a value
3. LRU eviction (auto-evict when memory full)

Out of scope: persistence guarantees, complex query patterns, pub/sub.

### Non-functional (SCALDS scan)

| Letter | Requirement |
|--------|-------------|
| **S(cale)** | 100K reads/s, 10K writes/s at peak. Dataset 1–5 TB. Cluster expandable. |
| **C(onsistency)** | Eventual — few seconds of stale reads acceptable. No linearizability. |
| **A(vailability)** | 99.9% floor. |
| **L(atency)** | Sub-10ms p99 reads. Sub-1ms p50 target. |
| **D(urability)** | Not guaranteed — cache is not source of truth. Loss on restart acceptable. |
| **S(ecurity)** | Internal service-to-service; no auth for v1. |

### Process notes
- **Stage 1 close miss — 3rd consecutive.** Initially stated only C / L / D. Skipped S (scale numbers), A (availability %) and Security. Required correction. Fixed cleanly but now a documented pattern — must be automatic by Day 4.
- Correctly scoped: string K/V, get/set/delete, LRU. Tight and appropriate.

---

## ✅ Stage 2 — Core Entities (closed)

| Entity | Fields |
|--------|--------|
| **CacheEntry** | key, value, lru_metadata (last_accessed_at or DLL position), ttl |

One entity is correct. Cache has no relational structure; LRU metadata is the only non-obvious field. Clean and minimal.

---

## ✅ Stage 3 — API Design (closed, after correction)

```
GET    /entries/{key}              → 200 { value, ttl } | 404 (miss or expired)
PUT    /entries/{key}  { value, ttl? }  → 200          (upsert)
DELETE /entries/{key}              → 204
```

**Correction applied:** user initially proposed separate POST (create) + PUT (update). For a cache, both are upserts — PUT handles both. Separate POST creates ambiguity on conflict and doubles surface area unnecessarily.

**Minor:** key named `id` in GET/DELETE, `key` in PUT/POST — harmonized to `key` across all methods.

TTL: optional on PUT, returned on GET. Correct.

---

## ✅ Stage 4 — High-level Design (closed, with probe)

### Motivation for distributed
- 5 TB won't fit single-node RAM → sharding required.
- 100K reads/s + 10K writes/s → horizontal scale needed.
- Spike headroom (5–10× burst) → cluster must be elastically expandable.

### Topology
```
Client services
      │
      ▼
 Facade / Router service
 (hides sharding, handles routing, caches hot keys)
      │
      ├── Shard 1 (primary + replicas)
      ├── Shard 2 (primary + replicas)
      └── Shard N (primary + replicas)
```

**Sharding algorithm:**
- Initially stated hash mod N → correctly identified the remap problem (topology change = near-total key migration).
- **Required probe to name consistent hashing.** After prompt: consistent hashing + virtual nodes (256 vnodes per physical node). Adding a node moves only ~1/N keys; vnodes smooth arc variance. Ring state held by facade / coordination service.

**Eviction:** passive TTL (check on read, return 404 if expired) + active eviction under memory pressure respecting LRU order.

**Replication:** async fanout to read replicas → eventual consistency. Writes return immediately after primary ack; replica sync is background.

### What was strong
- Passive TTL + active-under-pressure eviction model is correct.
- Async replication / write-fast + sync-background is the right trade-off.
- Fan-out reads to replicas for read scalability.

### Gaps in HLD
- Consistent hashing named only after explicit probe — should be the first pivot on "what algorithm replaces hash mod N."
- Facade described as single master without noting it's a SPOF; should be: stateless + replicated, ring metadata in etcd/ZooKeeper.

---

## ✅ Stage 5 — Deep Dive (walked by NFR, with probing on hot-key / stampede)

### Scalability
~30–50 servers, few hundred GB RAM each. 100K reads/s and 10K writes/s are trivially handled by in-memory store — Redis operates at microsecond level. No concern.

### Consistency
Single-threaded event loop (Redis model) → linearizable within one shard. Cross-shard during remapping → small inconsistency window. Accepted.

### Availability
Read replicas per shard. Raft for leader election on primary failure. AZ-spread for replica placement.

### Latency
RAM operations → sub-1ms. Sub-10ms p99 is conservative — no concern.

### Durability
Accept data loss. Future: RDB snapshots / AOF for restart recovery (Redis-style).

### Security
Internal only, no auth v1.

### Observability *(strongest section of the mock)*
- Golden signals: error rate, latency, RPS, availability.
- Cluster health: data distribution evenness, per-node memory utilization, capacity alerts.
- Logs + monitors + alerts + traces. Observability pipeline decoupled from main service (Kafka events or pull-based agent). Mentioned importance of independence.

### Cost
Realistic: dozens of servers = thousands–tens of thousands $/month. Levers: managed solutions (DynamoDB/Elasticache) to start, spot instances, right-sizing, reserved capacity, same-VPC to avoid egress.

### Hot key (required explicit probe)
User covered:
- Key prefix scattering: `key_0` … `key_N-1`, random pick → distributes traffic across shards.
- Facade local cache for hot keys.
- Client-side in-process HashMap.
- Temporary out-of-ring replication with TTL and internal metadata.

**Missed (not raised proactively):**
- Bounded-load routing (overflow to next node when shard is overloaded).
- Per-shard read replicas scaled independently for hot shards.

### Stampede (required second probe)
User covered:
- Probabilistic early expiration (jitter before hard TTL).
- Background refresh / refresh-ahead (proactive refill).

**Missed (not raised proactively):**
- **Request coalescing with mutex** — only one request fetches from DB on miss; others queue for the same result. The canonical companion to probabilistic expiration.
- Stale-while-revalidate — serve stale immediately, async refresh triggered by miss.
- Event-driven invalidation (CDC + Kafka) — eliminates TTL-based stampede entirely; cache invalidates when DB emits change event.

### Cache patterns — not mentioned at all (Major gap)
Cache-aside vs write-through vs write-back was never raised. Every cache design should address "how does this cache stay in sync with the DB?":
- **Cache-aside:** client reads cache, miss → DB → populate cache. Most common for read-heavy.
- **Write-through:** write to cache + DB synchronously. Always warm, higher write latency.
- **Write-back:** write to cache, async flush to DB. Fast writes, risk of loss on cache failure.

See `concepts/cache-patterns.md`.

---

## 📊 Verdict: Lean hire / borderline

**Strong:** Observability + Cost were the best across 3 mocks. Consistency nuance (single-threaded loop = linearizable per shard). Availability via replicas + Raft. Eviction model (passive TTL + active under pressure).

**Blockers for clear hire:**
1. SCALDS Stage 1 close — automatic by Day 4 (3 consecutive misses, now a pattern).
2. Cache patterns — omitted entirely; "how does cache stay in sync with DB?" is a staple question.
3. Consistent hashing + hot-key + stampede — correct content, but all required probing. At staff level the bar is voluntary.

---

## 🔗 Cheat sheets surfaced
- `patterns/consistent-hashing.md` — ring + vnodes (already written, Day 3 foundation)
- `concepts/cache-patterns.md` — cache-aside / write-through / write-back (gap-fill, written post-mock)
