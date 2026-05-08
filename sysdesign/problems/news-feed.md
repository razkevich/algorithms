# News Feed / Twitter — Day 2 Mock

**Date:** 2026-05-08 · **Day:** 2 / 10 · **Mock #:** 2 · **Status:** ✅ complete (clock-off mode, ended early via escape valve at Stage 5 mid-deep-dive)

---

## 🔒 Round agreement (locked pre-mock)

> Goal: **nail the fanout decision with explicit trade-offs and a clean hot-key story; close every stage formally.** Lead with the hybrid (push for normal, pull for celeb) — don't be pushed there. Stage 1 close discipline must lock by today (Day 1 weakness).

Pre-mock review: stages + caps recited (caps missed). SCALDS + COO recited (which-stage-each-belongs-to missed). DB toolbox drift: said `Postgres / Cassandra / Mongo / Elasticsearch / Redis / DynamoDB / Clickhouse` — Mongo and Clickhouse aren't core; **Kafka and S3 missing** (Kafka relevant today). Stakes (item 4) skipped.

---

## ✅ Stage 1 — Requirements (closed, with pushback)

### Functional (committed top set)
1. Post short-form text content
2. Follow / unfollow other users
3. View home feed (recent posts from followees)
4. Notifications (kept in scope as "architecturally significant")

Engagement (likes/replies/reshares), discovery/search, DMs, ads, video pipeline → **out of scope.**

### Non-functional (SCALDS scan)

| Letter | Requirement |
|--------|-------------|
| **S(cale)** | 10M DAU, ~1K read QPS, ~100 write QPS user-facing posts. Tens of millions of followers possible (celebs). |
| **C(onsistency)** | Eventual on feed visibility. Few seconds for normal users; ~30s tolerable for celeb fanout edge. |
| **A(vailability)** | 99.9% (~8.7 hr/yr). **Read path prioritized** — write path can degrade briefly. |
| **L(atency)** | Feed read p99 < 200 ms. Post submit p99 < 500 ms. |
| **D(urability)** | _Not stated._ |
| **S(ecurity)** | _Not stated. Auth delegated to Auth0; identity from JWT, never body/path._ |

### Process notes
- **Stage 1 close regression — same Day 1 miss.** Said "commit to that and close stage one" without restating NFRs as a quantified list. Required identical pushback. Durability and Security skipped silently.
- Math derivation correct: 10M DAU × 10 reads/day = 100M/day ≈ 1K QPS; × 1 post/day = 100 QPS. Day 1 math errors fixed.
- Followed product brief reframing (news-aggregator → social feed) cleanly.
- Cleanly committed to read-path-priority availability stance.

---

## ✅ Stage 2 — Core Entities (closed)

| Entity | What it represents |
|--------|---------------------|
| **User** | Account record |
| **Post** | User-generated text content |
| **Subscription** | Follow edge (follower → target) |
| **Notification** | Outbound notification record |

### Process notes
- Bulleted, fast — Stage 2 done as intended.
- **Missing**: `Feed` / `TimelineEntry` as first-class entity (would surface naturally with fanout-on-write). Acceptable to defer to HLD.

---

## ✅ Stage 3 — API Design (closed, after corrections)

```
POST   /v1/posts                                       { text }
                                                       → 201 { postId, ... }
                                                       (author = auth.userId from JWT)

POST   /v1/users/{userId}/follow                       (no body)
                                                       (follower = JWT, target = path)
DELETE /v1/users/{userId}/follow                       (unfollow — added on prompt)

GET    /v1/feed?cursor=<opaque>&limit=20               → 200 { posts, next_cursor }
```

Notifications: out-of-API surface (server-emitted email).
Auth: bearer JWT validated by Auth0; identity from token, **never body/path** (Day 1 round-goal fix).

### Process notes
- `/v1/post` (singular) corrected → plural. Convention drift; not yet locked.
- **Required pushback on auth-from-token binding** — Day 1 specific weakness; spelled out only after probe.
- **Required pushback on cursor pagination** — feed without pagination is a non-starter; not volunteered.
- Cursor reasoning accepted readily once raised: offset breaks under inserts during scroll.
- Unfollow symmetry not initially included.

---

## ✅ Stage 4 — High-Level Design (closed; major omissions captured)

### Final shape (after pushback on hot-key path)

```
                                     ┌────────────────────────────┐
                                     │  Redis: celeb cache        │
                                     │  per-celeb sorted set,     │
                                     │  last ~100 posts each      │
                                     └──────▲────────────┬────────┘
                                            │ write-     │ read
                                            │ through    │ (parallel
                                            │            │  ZRANGE)
                                            │            │
client ─► API GW ─► Post Service ──┬────────┘            │
                       │            │                    │
                       │ Postgres   │ Kafka              │
                       ▼            │ (partitioned       │
                   posts (SoT)      │  by user_id)       │
                                    │                    │
                                    ▼                    │
                              Ingest Service             │
                              (Kafka Streams,            │
                               normal-author fanout)     │
                                    │                    │
                                    ▼                    │
                          ┌─────────────────────┐        │
                          │ Materialized feeds  │ ◄─ read by Feed Service
                          │ (storage NOT spec'd)│        │
                          └─────────────────────┘        │
                                                         │
client ─► API GW ─► Feed Service ────────────────────────┘
                       │
                       │  In-process: ML rank + merge
                       │  (precomputed timeline) + (celeb pull)
                       ▼
                    response
```

### Architectural decisions made
- **Read/write service split** (post service + feed service) — independent scaling under read-heavy load. Correct intuition, articulated.
- **Async fanout via Kafka** — fire-and-forget on user response, partitioned by user_id.
- **Fanout-on-write for normal authors** — push to followers' materialized timelines.
- **Hybrid for celebs (pull at read time)** — required heavy probing to converge.
- **Subscription data**: lives in post service (durable) + Redis cache (read-fast).
- **ML ranking** initially placed at ingest service; relocated to feed service in-process after pushback (kills a hop).
- **Celeb cache**: Redis sorted set per celeb, populated by post service write-through.
- **Celeb classification**: materialized count → refined to *flag-on-follow-edge* / *per-user `celebs_followed` set* after probe.

### Hot-key resolution journey (the round-goal probe)
| Iter | Proposal | Issue |
|------|----------|-------|
| 1 | Pure fanout-on-write | Celeb post = 10M feed inserts in one event |
| 2 | Hybrid with ingest as celeb-fetch API | Ingest is a stream processor — role conflation |
| 3 | Feed → Ingest → Post Svc → Postgres → ML | 3 sequential hops + ML on read path = 200ms budget dead |
| 4 | "Trade-off for simplicity" defense | Hand-wave — NFR was self-set; can't relax it without redesign |
| 5 | ML moved to feed service (one fewer hop) | Better, but Postgres still on read path under load |
| 6 | Redis read-through cache for celeb posts | ✅ Lands; ~5-10ms parallel ZRANGE |

### Major omissions (captured for critique)
1. **Materialized feed storage shape never specified.** Where does the per-user precomputed timeline (10M users × ~500 entries) live? Cassandra/DynamoDB wide-column canonical (partition by user_id, clustering by ts DESC). Postgres won't scale at this volume. Redis sorted sets work but cost-prohibitive. **Biggest HLD gap.**
2. **Notification path scoped in (Stage 1) but evaporated.** Should be: another Kafka consumer → email/push provider with retry + dedup + per-user rate limit.
3. **18 TB Postgres** for posts source-of-truth — partitioning strategy not articulated.
4. **"Kafka Streams or Flink"** — pick one, own it. Hand-wave smell.

### Process notes
- Self-caught storage estimation gap mid-Stage 4. Math correct (10M × 1/day × 5yr × ~1KB ≈ 18TB).
- Reset cleanly under each pushback — important interview behavior.
- Subscription data ownership ambiguous (post service vs. feed service for follower counts) — required forced-pick.

---

## ⚠️ Stage 5 — Deep Dive (one item covered, ended early)

### Walked NFRs / COO

**O (Observability)** — self-volunteered ✓
- Four golden signals (latency, traffic, errors, saturation)
- Datadog as platform
- SLO + error-budget framing (SRE)
- Business metrics via separate Kafka consumer (pattern recognition)
- **Gap**: only metrics — logs and traces missing. Distributed tracing critical for 5-hop service path.
- **Gap**: didn't call out *the* killer signal — Kafka consumer lag on ingest service (lag spike = visible eventual-consistency breakage).

### Not covered (ended early via `end early` escape valve)
- Scalability (write amplification under hot-key, ingest scaling)
- Availability (read-path resilience, Redis failure mode)
- Latency (caching layers in front of feed service, hot-key reflex)
- Consistency (eventual — what user actually sees, replica reads)
- **Cost** (must volunteer — not covered)
- **Operability** (must volunteer — not covered)

---

## 📊 Severity-rated critique

### 🔴 Blockers — none
Architecture eventually correct. No fundamental misunderstanding.

### 🟠 Majors (drill before Day 3)

1. **Stage 1 close regression — same Day 1 miss.** Required identical pushback. Durability and Security never stated. **Round goal said "close every stage formally." Not met.** Lock the ritual on Day 3 or it's a pattern.

2. **Hot-key handling required heavy probing.** Round goal was "lead with hybrid." Reality: ingest service on synchronous read path (role conflation), 3-hop chain, "trade-off for simplicity" hand-wave before redesigning. Celeb fanout is the canonical News Feed probe. Slow convergence here is a level signal in a real Twitter/Meta loop.

3. **Materialized feed storage shape never specified.** Source-of-truth in Postgres, fine. But precomputed timelines (10M users × ~500 entries) — *where do they live?* This is the biggest HLD omission. Cassandra/DynamoDB is the canonical answer; the gap means the entire write-path scaling story is unaudited.

4. **Notification path scoped in but never architected.** Either descope explicitly or design (Kafka consumer → provider, retry, dedup). Don't drop scoped requirements silently.

5. **Celeb classification took two iterations.** First "check at read time" (300x amplification), then "materialize counts" (still 300 lookups). Only landed at "classify on follow edge / per-user set" after specific probe.

### 🟡 Minors (polish)

- `/v1/post` → `/v1/posts` corrected (plural-resource habit not yet locked).
- Storage estimation came mid-Stage 4 — should be pre-Stage 1 NFR quantification.
- 18 TB single-Postgres aggressive without explicit partitioning story.
- Read-through vs write-through under-specified for celeb cache (write-through cleaner).
- Tracing missing from observability triangle (logs + traces + metrics, not just metrics).
- "Kafka Streams or Flink" floated for ingest — pick one and own it.
- Cursor pagination not volunteered (corrected on probe).
- Auth-token binding not volunteered (corrected on probe — Day 1 round-goal item).
- Unfollow symmetry not initially included.

### 🟢 Strong signals to keep

- **Read/write service split** with stated reasoning (independent scaling under read skew).
- **Eventual consistency call** with right justification.
- **Async via Kafka, fire-and-forget** — clean architectural choice; user not blocked on fanout.
- **Pattern recognition under correction**: relocated ML to feed service in-process to kill a hop.
- **Observability section** strongest segment — self-volunteered, four golden signals + SLO/error-budget + business metrics via Kafka consumer.
- **Cursor pagination** accepted with correct reasoning (offset drifts under inserts).
- **Storage math correct** — Day 1 weakness fixed.
- **Reset under pushback** repeatedly — didn't dig in on the 3-hop chain; redesigned when pressed.

### Communication
- Long stream-of-consciousness HLD answer — would benefit from breaking into "let me cover write path / read path / data ownership" rather than one wall.
- Took every correction cleanly (no sulking, no defensiveness).

---

## Rating

**Lean hire / borderline weak hire (real loop equivalent).** Stronger than Day 1: handled the canonical probe, observability strong. Drag from Stage 1 close regression and slow convergence on hybrid. Real Twitter/Meta loop = "yes with reservations."

---

## 🎯 Day 3 drill list (priority)

1. **Stage 1 close ritual — make it muscle memory.** Verbal: "Locking these — Functional: 1/2/3. NFRs: S=X, C=Y, A=Z, L=p99, D=stated, S=stated. OOS: list. Stage 1 closed." If this doesn't hold on Day 3, framework-discipline blocker — divert all energy to it.
2. **Lead with hybrid on any feed/fanout problem.** Don't be pushed there. Volunteer the celeb constraint within Stage 1 NFRs.
3. **DB toolbox recall — fix the drift.** Memorize: Postgres / DynamoDB(Cassandra) / Redis / Elasticsearch / **Kafka** / **S3** + Spanner stretch. Not Mongo/Clickhouse. One-line picking criterion each.
4. **`patterns/feed-fanout.md`** — write today. Must include: hybrid threshold logic, celeb-cache shape, materialized-feed storage choice, classification-on-edge pattern, latency budget walkthrough.
5. **Materialized timeline storage**: Cassandra wide-column drill. Partition key = user_id, clustering = (timestamp DESC, post_id). Read pattern matches sort order.
6. **Auth-from-token + plural-resource** habits — should be reflexive in Stage 3 by Day 3.
