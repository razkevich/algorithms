# Feed Fanout — Hybrid Push/Pull

> **Pattern home**: any system where producers post events that get assembled into a per-consumer view (social feeds, notification timelines, activity streams). Twitter, Instagram, Facebook home feed.

> **Direction cheat-sheet**:
> - **Fanout-on-write (push)**: fast read, slow celebrity post.
> - **Fanout-on-read (pull)**: fast post, slow read for high-fanout consumers.
> - **Hybrid**: push for normal, pull for celebrities. **The real answer.**

---

## Why this pattern matters

Read/write asymmetry: feed reads dominate writes by 100-1000×. So spend write-time work to make read-time cheap. But pure push breaks under power-law fanout (one celeb → 10M+ inserts per post). Pure pull breaks read latency (must aggregate from N follows on every read). Hybrid is the canonical reconciliation.

---

## The three shapes

### Fanout-on-write (push)

When @alice posts → ingest service fans out post_id into the precomputed timeline of every follower.

```
@alice posts
    │
    ▼
write to Posts table (source of truth)
    │
    ▼
emit to Kafka
    │
    ▼
ingest consumer reads followers of @alice
    │
    ▼
INSERT (follower_id, post_id, ts) into N timeline rows
```

**Read path:** trivial. `SELECT FROM timeline WHERE user_id = $1 ORDER BY ts DESC LIMIT 20`. Single keyed read; sub-10 ms.

**Write amplification:** N (where N = followers of author).

**When it wins:** N is bounded and small (< ~1K followers). Reads are vastly more frequent than writes.

**When it breaks:** celebrity. @taylorswift posts once → 10M inserts. One event blows up:
- Ingest consumer lag explodes for everyone
- Write QPS to timeline store spikes 10M/post-duration
- Celebrity fanout backs up; @ordinary's post arrives 10 minutes late

### Fanout-on-read (pull)

Posts only land in the author's `posts` table. At read time, feed service aggregates posts from each followee.

```
read /v1/feed for @bob
    │
    ▼
look up @bob's followees: [@alice, @celeb1, @celeb2, ...]
    │
    ▼
SELECT * FROM posts WHERE author_id IN (...) ORDER BY ts DESC LIMIT 20
    │
    ▼
return
```

**Write path:** trivial. One row per post.

**Read path:** N keyed reads + merge. If @bob follows 500 people, that's 500 lookups per feed read. At 1K read QPS, 500K backend reads/sec.

**When it wins:** very low read traffic OR users have very few follows.

**When it breaks:** typical social graph. Reads are too expensive at the access pattern's natural scale.

### Hybrid (the real answer)

Classify users at follow-time: `is_celeb` = follower_count > threshold (say 100K).

- **Normal authors → fanout-on-write** to followers' timelines.
- **Celebrity authors → no fanout** at write. Post lands in author's row only + a celeb cache.
- **Read time**: feed service merges (a) precomputed timeline (push from normals) + (b) recent posts pulled from each celeb the user follows.

```
write path                          read path
──────────                          ─────────
post                                GET /v1/feed
  │                                   │
  ├─► Posts (SoT)                    ├─► precomputed timeline
  │                                   │      (only normal-author posts)
  │   if author.is_celeb:            │
  ├─► celeb cache                    ├─► for each celeb in user.celebs_followed:
  │   (Redis sorted set,             │      ZRANGE celeb:{id}:recent_posts
  │    score=ts, last ~100)          │      (parallel)
  │                                   │
  │   else:                          ├─► merge by timestamp
  └─► Kafka → ingest →               │
      INSERT into N timelines        └─► return top 20 with cursor
```

---

## Storage choices

### Precomputed timeline (push side)

**Canonical: Cassandra / DynamoDB (wide-column).**
- Partition key: `user_id` (the *reader*, not the author).
- Clustering key: `(timestamp DESC, post_id)`.
- Read pattern matches sort order → no sort cost.
- Wide-column handles per-user unbounded growth.
- TTL to bound storage (e.g., 30 days of timeline; older drops off).

**Why not Postgres**: at 10M users × 500 timeline entries × pointer overhead = billions of rows. Single-master Postgres struggles; sharding by user_id works but you're rebuilding wide-column.

**Why not Redis sorted sets**: works (`ZADD timeline:{user} ts post_id` + `ZREVRANGE`). Fast but expensive at this volume. Defensible for hot users only with tiered approach (hot in Redis, warm in Cassandra). Two-tier complexity often not worth it.

### Celeb post cache (pull side)

**Canonical: Redis sorted set per celeb.**
- Key: `celeb:{user_id}:recent_posts`
- Score: timestamp
- Member: post_id (or a small post payload)
- Trim to last ~100 posts (`ZREMRANGEBYRANK ... 0 -101`)

**Population**: post service writes through at post-time (write-through, not read-through — guarantees freshness on first read).

**Read**: feed service does parallel `ZREVRANGE celeb:{id}:recent_posts 0 19` for each celeb in `user.celebs_followed`. ~1-2 ms per celeb, parallelized → ~5-10 ms for 10 celebs.

### Source of truth

Posts table (Postgres or wide-column). Holds full content. Celeb cache and timelines store *post_ids* (or small denormalized projections); full hydration from Posts table at render. Decouples fanout cost from post size.

---

## Celeb classification — when and where

### Wrong: classify at read time

"Look up follower_count for each followee" → 300 lookups per feed read × 1K read QPS = **300K classification lookups/sec**, just to answer yes/no. Wastes everything you saved by precomputing timelines.

### Right: classify on the follow edge

When Alice follows TaylorSwift, record on the follow row:
```
follows: { follower_id, target_id, target_is_celeb (denormalized), ts }
```
Or maintain a per-user Redis set: `user:{alice}:celebs_followed = {taylor, elon, ...}`.

Read time becomes **one lookup**: pull Alice's `celebs_followed` set, parallel-fetch celeb caches.

### Edge: classification flips

User crosses the threshold (99,999th → 100,001st follower). Now they're a celeb but their last 1M followers' timelines have already been receiving fanout.

**Two options:**
1. **Don't backfill.** Followers from before the flip still get fanout-on-write for new posts (waste, but bounded at threshold size). After the flip, no new fanout.
2. **Stop fanout at flip + backfill celeb cache.** Cleaner. Run as a batch job.

In interview: state the trade-off, pick #1 for simplicity unless asked.

### Edge: extreme followers (millions of follows)

A user follows 10K celebs. At read time, 10K parallel ZRANGE = real cost. Mitigation: cap "celebs returned per read" via ranking (e.g., top 100 by recency or ML score among the celebs). Most users follow <50 celebs in practice — this is usually a non-issue but worth one sentence.

---

## Latency budget walkthrough

**Target: feed read p99 < 200 ms**

| Hop | Time | Notes |
|---|---|---|
| API gateway → feed service | 2-5 ms | LB, TLS, in-region |
| Feed service → user metadata (cache) | 1-2 ms | who do you follow, who's a celeb |
| Feed service → precomputed timeline (Cassandra) | 5-15 ms | one keyed read |
| Feed service → celeb cache (Redis, parallel) | 5-10 ms | parallel ZRANGE × ~10 celebs |
| In-process: ML rank + merge | 1-5 ms | cached features; small model |
| Feed service → Posts table for hydration | 10-20 ms | batch SELECT WHERE id IN (...) |
| Return + serialization | 2-5 ms | |
| **Total** | **26-62 ms typical** | Well under 200 ms |

**Anti-patterns to refuse**:
- Synchronous chain through ingest service ("feed → ingest → post-svc") — role conflation, multiplies hops.
- ML on remote service over network on read path — bring inference in-process or cache scores.
- Pulling celebs from Postgres directly under load — celeb cache is non-negotiable.

---

## Pattern transfer

The same shape recurs in:

| System | Push side | Pull side |
|--------|-----------|-----------|
| **Twitter home feed** | normal-author timeline insert | celeb timeline merge |
| **Instagram feed** | same | same |
| **Notification systems** | normal sender → recipient inbox row | high-fanout sender → poll at read |
| **Activity streams** (GitHub, etc.) | repo events → watcher inbox | popular-repo events → fetch on view |
| **Sports/news live updates** | small-following games → push | popular games → broadcast channel + pull |

Whenever you have **producers with skewed fan-out distribution**, hybrid is the canonical answer.

---

## Interview quick-fire

- "How does Twitter handle celebrity posts?" → **Hybrid: push for normal, pull for celebs. Threshold-based classification on follow edge. Celeb posts in dedicated Redis cache (sorted set per celeb).**
- "Why not pure push?" → @taylorswift = 10M inserts per tweet, ingest lag for everyone, blocks normal fanout.
- "Why not pure pull?" → 500 followee lookups per feed read × 1K read QPS = 500K backend reads/sec. Doesn't fit the latency budget.
- "Where do precomputed timelines live?" → Wide-column (Cassandra/DynamoDB), partitioned by reader user_id, clustered by ts DESC.
- "Where do celeb posts live for read?" → Redis sorted set per celeb, populated write-through, read via parallel ZRANGE.
- "How do you classify celebs?" → On follow edge or per-user celebs-followed set; not at read time.
- "Latency budget?" → ~30-60 ms typical end-to-end; 200 ms p99 budget holds with comfortable margin.
