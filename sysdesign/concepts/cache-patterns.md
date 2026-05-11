# Cache Patterns — Sync Strategies

> **Pattern home**: any system where a cache sits in front of a database. The question "how does the cache stay in sync with the DB?" has four canonical answers. Pick the wrong one and you get stale data, write amplification, or loss risk.

> **Direction cheat-sheet**:
> - **Cache-aside** (lazy): cache miss → read DB → populate cache. Default for read-heavy. Cache and DB are independently managed.
> - **Write-through**: every write → cache AND DB synchronously. Always warm, doubled write latency.
> - **Write-back** (write-behind): write to cache only, async flush to DB. Fast writes, loss risk on crash.
> - **Refresh-ahead**: proactively re-fetch before TTL expires. Eliminates cold misses, wastes resources if prediction is wrong.

---

## The four patterns

### 1. Cache-aside (lazy population)

The most common pattern. The application owns the cache interaction — not the cache layer.

```
READ path:
  client → cache.get(key)
    HIT  → return cached value
    MISS → db.read(key) → cache.set(key, value, ttl) → return value

WRITE path:
  client → db.write(key, value) → cache.delete(key)  ← invalidate, not update
```

**Why invalidate on write instead of update?** Writing the new value to cache on every update causes race conditions: two concurrent writers can interleave, leaving the cache with the older value. Invalidation is safe — next read repopulates from DB with the definitive value.

**When it wins:**
- Read-heavy workloads (reads >> writes).
- Data access is sparse — no point preloading everything.
- Cache and DB can have different data shapes (you can store a transformed / aggregated view in cache).

**Failure modes:**
- **Cache stampede**: many clients get a miss simultaneously after expiry → all hit DB at once. Mitigate with probabilistic expiration, request coalescing, or refresh-ahead.
- **Stale reads**: between a write (which invalidates) and the next DB read, the cache is empty. A fast second read hits the DB directly. Acceptable for eventual consistency; not for read-your-writes.

**Where it appears:** most web application caches (user profiles, product catalog, session data), Redis in front of Postgres.

---

### 2. Write-through

Every write goes to the cache AND the database synchronously before returning to the caller. The cache layer (or a sidecar) intercepts writes.

```
WRITE path:
  client → cache.set(key, value) → db.write(key, value) → return success

READ path:
  client → cache.get(key)
    HIT  → return (cache always warm for written keys)
    MISS → rare; happens only for keys never written since cache start
```

**When it wins:**
- Data is written frequently AND read frequently (no point caching stale data immediately after write).
- Read-your-writes consistency required: the cache is always at least as fresh as the DB.
- You can tolerate higher write latency (cache + DB in sequence).

**Failure modes:**
- **Write latency doubles** (or more with replication). Serializing cache + DB on every write is expensive under high write QPS.
- **Cache churn**: you cache data that may never be read (write-heavy with random key distribution).

**Where it appears:** financial ledger caches, user preference stores, any system that must read what it just wrote.

---

### 3. Write-back (write-behind)

Write to cache only; a background process flushes dirty entries to the DB asynchronously.

```
WRITE path:
  client → cache.set(key, value, dirty=true) → return success (immediately)
                  ↓ (background)
           flush worker → db.write(key, value) → cache.mark(key, clean)

READ path:
  client → cache.get(key) → return value (always fresh from cache perspective)
```

**When it wins:**
- Write-heavy workloads where write latency is the bottleneck.
- Writes can be batched (e.g., 1000 individual counter increments → one DB write of the final value).
- Data loss on crash is acceptable (or you have a WAL-like durability layer separately).

**Failure modes:**
- **Data loss on cache failure**: dirty entries not yet flushed to DB are lost. If the cache node crashes, you lose recent writes. Mitigate with write-ahead log, write replicas, or bounded flush delay.
- **Consistency gap**: DB lags behind cache. Other services reading the DB directly see stale data.
- **Operational complexity**: the flush worker must handle retries, ordering, and partial failures.

**Where it appears:** write-heavy analytics counters, gaming leaderboards, clickstream aggregation — anywhere you can afford to lose the last N seconds of writes.

---

### 4. Refresh-ahead (proactive prefetch)

The cache predicts which entries will expire soon and re-fetches them before the TTL fires, so reads never see a miss.

```
Background process:
  for each entry approaching TTL:
    new_value = db.read(key)
    cache.set(key, new_value, ttl=fresh)

READ path:
  client → cache.get(key) → always a HIT (if prediction is right)
```

**When it wins:**
- Access patterns are predictable (same keys accessed repeatedly in a cycle).
- Cold misses are expensive (DB is slow or rate-limited).
- Combined with background refresh for hot keys that must never expire (celebrity profile, viral product).

**Failure modes:**
- **Wasted resources**: you prefetch keys that won't be accessed again, burning DB reads and cache memory.
- **Stale data on wrong prediction**: if the refresh fires before a write, you cache a value that the DB is about to change.

**Where it appears:** CDN edge prefetch, celeb cache warm-up, session token refresh.

---

## Hot key + stampede: which pattern helps

| Pattern | Prevents stampede? | Notes |
|---|---|---|
| Cache-aside | ❌ by default | Needs coalescing or jitter added |
| Write-through | ✓ partial | Cache is always populated after every write — no miss after write |
| Write-back | ✓ partial | Same as write-through for the read path |
| Refresh-ahead | ✓ strong | Eliminates miss-triggered stampede for predicted keys |

**Best stampede mitigations regardless of pattern:**
1. **Probabilistic early expiration** — start randomly returning misses a few minutes before hard TTL; individual clients repopulate independently, spreading load.
2. **Request coalescing** — on miss, only one goroutine fetches from DB; all others queue and share the result. No duplicate DB round-trips.
3. **Stale-while-revalidate** — serve the stale value immediately; trigger async refresh; next read gets the fresh value.
4. **Event-driven invalidation (CDC + Kafka)** — skip TTL for hot keys; invalidate explicitly when DB emits a change event. Eliminates the expiry-triggered stampede at the root.

---

## Decision matrix

| Scenario | Pick |
|---|---|
| Read-heavy, sparse access, eventual consistency OK | **Cache-aside** |
| Read-your-writes required, write latency OK | **Write-through** |
| Write-heavy, loss of last N seconds acceptable | **Write-back** |
| Predictable hot keys, cold miss is expensive | **Refresh-ahead** |
| Hot key with TTL-based stampede concern | **Refresh-ahead** + **coalescing** + **probabilistic expiration** |
| Cache invalidation must be precise (financial, inventory) | **Write-through** + **CDC-driven invalidation** |

---

## Interview quick-fire

- **"How does your cache stay in sync with the DB?"** → cache-aside with invalidate-on-write is the default; explain the race-condition reason invalidation is safer than update.
- **"Why not update the cache on every write?"** → concurrent writers can interleave, leaving stale data. Invalidation is atomic; next read repopulates from the authoritative DB.
- **"What's the difference between write-through and write-back?"** → write-through is synchronous (both DB and cache before ack); write-back is async (cache ack immediately, flush to DB later). Trade-off: write latency vs durability.
- **"How do you prevent a hot key from causing a DB spike on expiry?"** → probabilistic early expiration + request coalescing + refresh-ahead. Or skip TTL entirely with event-driven invalidation.
- **"What's stale-while-revalidate?"** → serve the stale cached value immediately, fire an async refresh; caller gets response with zero extra latency; next reader gets fresh value.
