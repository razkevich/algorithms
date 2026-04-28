# URL Shortener — Day 1 Mock (Calibration)

**Date:** 2026-04-28 · **Day:** 1 / 10 · **Mock #:** 1 · **Status:** pre-mock setup complete; mock pending

---

## 🔒 Round agreement (locked, pre-mock)

> **Goal: complete all 5 stages within 45 minutes, end-to-end, hitting time caps.** Stage 2 (Entities) explicitly executed. FCC + SLEDS scan visible in Stage 1. Pick *one* DB and stick with it (no engine-shopping mid-design). Interviewer (me) will force-advance on caps; don't fight it.
>
> **Day 1 calibration prioritizes process over depth.** Better to land a complete working system at minute 45 than to perfect any single stage and never reach Deep Dive.

### Stage time caps

| Stage | Cap |
|-------|-----|
| 1. Requirements (functional + non-functional) | 5 min |
| 2. Core entities | 2 min |
| 3. API design | 5 min |
| 4. High-level design | 10–15 min |
| 5. Deep dive | 10 min |
| **Total core flow** | **~32–37 min** |

### What I'll be watching for (interviewer probe targets)

1. **Read-heavy 100:1 workload** → cache strategy is the deep-dive.
2. **Hot-key handling** → viral URL = single shortcode at 100,000× normal QPS.
3. **ID-generation choice** (base62 counter / hash / UUID / Snowflake) and *defended* trade-off.
4. **Custom alias collision** (concurrency on unique constraint).
5. **Click analytics** as follow-up (write-heavy stream layered on read-heavy resolves; don't write to Postgres on the read path).
6. **Auth identity from token, never request body** (security tell).
7. **Framework discipline** — do all 5 stages get hit on time? **This is the Day 1 calibration grade.**

### Locked DB choice (no swapping mid-design)

Per `concepts/db-toolbox.md` (user's stack): **Postgres** as the primary store. Scale via sharding (Citus / app-level / vertical primary + read replicas) + **Redis** read-through cache. No Mongo / Dynamo swap mid-mock — URL Shortener is pure KV access pattern; Postgres + Redis handles internet-scale URL Shortener fine.

---

## 📋 Pre-mock recall — gaps captured

User completed Day 1 full recital. Strengths and gaps logged here for end-of-mock critique calibration.

### ✅ Strong
- Stage 1 split into functional + non-functional + scope.
- Stage 4 discipline (simplest-first, build on it).
- Stage 5 driven by non-functional requirements.
- Self-driven proactivity to surface gaps (observability, security).
- Solid intuition on Postgres / Mongo / Kafka / Redis / S3 / Elasticsearch architecture.
- Caught own gap: capacity-estimation math.
- Identified URL-shortener stakes correctly (scaling, ID-mapping, ticket server / GUID / range allocation).

### ❌ Process gaps to drill
- **Stage 2 (Core Entities) skipped in recall** — must execute explicitly in mock; not implicit between Reqs and API.
- **Time caps not stated** — re-recite tomorrow until automatic.
- **FCC + SLEDS not memorized** — user opted out; this is the scan checklist for non-functional reqs in Stage 1, can't skip. Drill before Day 2.

### ❌ Factual / conceptual gaps to drill
- **DynamoDB transaction semantics**: TransactWrite/Read (up to 100 items, single account/region, ACID); conditional writes for CAS; strongly-consistent reads at 2× RCU; ~3K RCU / 1K WCU per partition limit.
- **MongoDB terminology**: shard key (not partition key); replica set (the replication unit); mongos (the router); ACID transactions since 4.0 / 4.2 (don't claim Mongo lacks them).
- **Kafka model**: partitions (within topics) are the parallelism unit, mapped to brokers. Exactly-once is opt-in (idempotent producer + transactions + read_committed). Default is at-least-once. Consumer offsets stored in `__consumer_offsets` topic by default.
- **Redis "tunable consistency"** — mischaracterization. Redis is single-primary per shard, async replication. `WAIT` blocks for replication acks but doesn't make it "tunable" in the Cassandra sense. Persistence (AOF / RDB / hybrid) is what's tunable. Redlock is contested for distributed locking — default to Postgres advisory lock or ZK/etcd for correctness-critical locks.
- **Elasticsearch**: near-real-time, not real-time (1s default refresh interval). Indexed docs aren't searchable for ~1s.
- **S3**: strong read-after-write consistency since Dec 2020. The "eventually consistent" answer is now stale and a tell.
- **Global ACID gap**: no answer for multi-region strong-consistency prompts. Memorize one-sentence Spanner/Cockroach pitch before facing fintech / payments rounds.

---

## Stages — to be filled during mock

### Stage 1 — Requirements *(5 min cap)*
_(empty — user fills during mock)_

#### Functional (top 3)
- [ ]
- [ ]
- [ ]

#### Non-functional (3–5, quantified, FCC+SLEDS scanned)
- [ ]

#### Out of scope
- [ ]

### Stage 2 — Core Entities *(2 min cap)*
_(empty — user fills during mock; "v1, will iterate")_

- [ ]

### Stage 3 — API Design *(5 min cap)*
_(empty — REST default; resources plural; auth from token)_

```
```

### Stage 4 — High-Level Design *(10–15 min cap)*
_(empty — boxes + arrows + schema annotations; simplest first; mark cache/queue points for deep-dive return)_

### Stage 5 — Deep Dive *(10 min cap)*
_(empty — walk each non-functional req; address bottlenecks, hot keys, edge cases)_

---

## Critique — filled post-mock

_(empty — severity-rated rubric: blocker / major / minor; coverage of all 5 stages; framework discipline; communication; depth where it counted)_
