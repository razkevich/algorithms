# System Design Prep — Progress Tracker

Mock-driven prep for staff/tech-lead loops at first/second-tier companies (distributed systems, SaaS, backend).

**Baseline courses (already studied — revisit, don't drill):**
- 🇬🇧 [English course](https://sysdesign-course-t83nq.ondigitalocean.app/course)
- 🇷🇺 [Russian course](https://course.sysdesign.online/course) — also has built-in case studies: Airbnb, Messenger, Notification Service, URL Shortener

**This tracker covers the *interview performance* layer**: problem walkthroughs that drill the framework + flow, surface concept gaps, and let cheat sheets accumulate as side-effects.

---

## 📅 10-day schedule (1.5 week sprint)

Hard pace: 1 mock/day Mon–Sat, calibration mocks on Days 9–10. Tier 1 problems first; Tier 2 fills in. Daily shape: 60 min cheat-sheet write OR course revisit → 5 min pre-mock review → 45 min mock → 15-20 min critique → commit. (No algo warm-up — this workspace is purely system design.)

| Day | Mock | New cheat sheet / pattern | Notes |
|-----|------|---------------------------|-------|
| 1 | URL Shortener (calibration) ✅ | ✅ `back-of-envelope.html` | Champion #1 — done 2026-04-28 |
| 2 | News Feed / Twitter ✅ | ✅ `feed-fanout.md` (push/pull/hybrid + celeb cache + classification) | Champion #2 — done 2026-05-08 |
| — | Real-time Chat / Messenger ✅ | _(RU course case study)_ | Champion #3 — covered out of band |
| — | Notification / Webhook Delivery ✅ | _(RU course case study)_ | Champion #4 — covered out of band |
| 3 | **Distributed Cache (Redis-like)** | `consistent-hashing.md` + visualization | Champion #5 — TODAY 2026-05-09 |
| 4 | Ride-Sharing Match (Uber) | `geohashing.md` + visualization | Champion #6 |
| 5 | Payment / Wallet System | `idempotency.md` (idempotency keys, sagas, double-spend, audit log) | Champion #7 |
| 6 | Distributed Rate Limiter | `rate-limiting-algorithms.md` (token / sliding / leaky bucket) | Champion #8 |
| — | Job Scheduler / Cron ✅ | _(covered out of band)_ | Stretch #9 — covered |
| 7 | **Video Streaming Platform** _(if media target — else swap with Day 8)_ | `cdn-adaptive-bitrate.md` (HLS/DASH, ingestion pipeline, transcoding) | Stretch #10 |
| 8 | **Search Typeahead** _(if search target — else replay weakest Champion as second cold re-mock)_ | `inverted-index.md` (trie, prefix sharding, popularity ranking) | Stretch #11 |
| 9 | **Cold re-mock** of worst Day 1–8 problem | _(no notes, full timer — calibration)_ | — |
| 10 | **Final mock under realistic pressure** | Behavioral/leveling prep | — |

**Schedule rationale — Champion 8 + Stretch 3 (in order of importance):** problems chosen on dual criteria of (a) interview frequency at staff/tech-lead loops and (b) concept-leverage to other problems. See [Problem queue](#-problem-queue--priority-ordered-for-stafftech-lead-loops) for ranking justification.

**Covered out of band (5 of 11):** Champion #1-2 via mocks (Days 1-2); Champion #3-4 (Messenger / Notification) via RU course case studies; Stretch #9 (Job Scheduler) via prior knowledge. Re-mocking the out-of-band ones risks pattern-matching memory rather than driving the framework cold; better signal to drill the Champion #5-8 + Stretch #10-11 (the 6 not-yet-mocked) under live conditions.

**Day 7-8 stretch ordering:** Video Streaming first (Day 7), Search Typeahead second (Day 8). Both are company-specific signals; swap if Search-target loops (Google) are imminent. If neither target applies, replay weakest Champion as a second cold re-mock — calibration is the highest-leverage use of those days.

**Foundations (already written, reference daily):**
- ✅ `concepts/interview-framework.md` — the 5-stage flow (binding for every mock)
- ✅ `concepts/db-toolbox.md` — 6+1 DB decision tree
- ✅ `concepts/back-of-envelope.html` — QPS / storage / bandwidth / latency cheat sheet (Day 1 foundation)
- ✅ `concepts/mock-prep.html` — daily pre-mock study guide (11 tabs: framework, SCALDS+COO, math canon, vocab, Stage 1 ritual, Stage 3 API, Stage 5 deep dive, hot-key reflex, DB drill facts, communication, Day 1 recap)

**Course material this week:** RU case studies (Airbnb, Messenger, Notification, URL Shortener) used as *post-mock calibration*, not pre-read. Read them *after* you've designed the system yourself.

**Adjustments allowed mid-sprint:** if a problem exposes deeper gaps than the day budgets, push the next day's mock back and patch the gap. Better to do 7 great mocks than 10 sloppy ones.

**Assumptions baked in (correct if wrong):**
- Behavioral/leveling drilled separately → if not, swap Day 7's buffer for behavioral
- Generic distributed-systems / SaaS / backend target → if specific (frontier-lab / fintech / etc.), swap 1–2 problems

---

## 🔥 Problem queue — priority-ordered for staff/tech-lead loops

**Champion 8** — must-master before any real loop. Each one anchors a concept area no other problem covers as cleanly. **Stretch 3** — drill if time / target company permits.

### Champion 8 (in order of importance)
1. [x] **URL Shortener** — base template (KV scaling, ID gen, sharding, cache). Calibration round; every loop opens with a variant. _(✅ Day 1 mock — see `problems/url-shortener.md`.)_
2. [x] **News Feed / Twitter** — read-heavy + fanout-on-write/read + hot-key (celeb). Most-asked FAANG-tier round; concepts transfer to Chat / Notification / Video. _(✅ Day 2 mock — see `problems/news-feed.md`; cheat sheet `patterns/feed-fanout.md`.)_
3. [x] **Real-time Chat / Messenger** — stateful long-lived connections (WebSocket/SSE), presence, connection affinity, per-channel ordering. Asked at every consumer-tech loop. _(✅ Covered via RU course case study.)_
4. [x] **Notification / Webhook Delivery** — async 1:N reliable fanout, retry/backoff/dead-letter, idempotent consumers, dedup, per-consumer throttling. Every SaaS has one. _(✅ Covered via RU course case study.)_
5. [ ] **Distributed Cache (Redis-like)** — consistent hashing, eviction, replication, hot-key. The consistent-hashing concept transfers everywhere — must be reflexive. _(Concept gap: `patterns/consistent-hashing.md` + visualization.)_
6. [ ] **Ride-Sharing Match (Uber)** — geospatial indexing (geohash/quadtree), realtime dispatch matching, surge pricing. Tests coordination under realtime constraints — distinct concept area. _(Concept gap: `patterns/geohashing.md` + visualization.)_
7. [ ] **Payment / Wallet System** — strict consistency, idempotency keys, sagas, double-spend prevention, audit log. The single problem where eventual consistency is the wrong default. _(Concept gap: `patterns/idempotency.md`.)_
8. [ ] **Distributed Rate Limiter** — token bucket / sliding window in shared store, edge vs central enforcement. Quick problem; common screening signal at staff loops. _(Concept gap: `concepts/rate-limiting-algorithms.md`.)_

### Stretch 3 (drill based on target company / time budget)
9. [x] **Job Scheduler / Cron** — leader election, lease-based execution, missed-fire/overlap handling, time-zone correctness, idempotent jobs, fan-out to workers. Closes the leader-election gap left by the Champion 8. Universally applicable. _(✅ Covered out of band.)_
10. [ ] **Video Streaming Platform** — CDN-dominated, adaptive bitrate (HLS/DASH), ingestion pipeline, transcoding. Drill if interviewing at Netflix / YouTube / Twitch / Disney+. _(Concept gap: `concepts/cdn-adaptive-bitrate.md`.)_
11. [ ] **Search Typeahead** — inverted index, trie, prefix sharding, popularity ranking, freshness. Drill if interviewing at Google or search-product targets. _(Concept gap: `concepts/inverted-index.md`.)_

### Defensibly dropped (not on the champion list — see brainstorm 2026-05-09)
- ~~Web Crawler~~ — classic, but rare in modern loops; politeness/freshness budgets a niche signal.
- ~~Dropbox / Google Drive~~ — only if file-storage role; chunking/CRDT/conflict-res transfers little to other rounds.
- ~~Distributed File System (GFS-like)~~ — infra-deep specialist; rare in product loops.
- ~~Kafka-clone / Distributed Log~~ — interviewers want you to *use* a broker, not design one.
- ~~Recommender System~~ — ML-flavored; off the distributed-systems main path.
- ~~Distributed ID Generator (Snowflake)~~ — sub-pattern of URL Shortener; doesn't deserve a standalone mock.
- ~~Airbnb / Booking~~ — covered by RU course case study; concurrency / double-booking concepts subsumed by Payment.
- ~~Realtime Analytics (Lambda / Kappa)~~ — specialist; off the main path for product loops.

---

## 🧰 Database toolbox (deep > wide)

Curated set — master these, decision-tree to one. See [`concepts/db-toolbox.md`](concepts/db-toolbox.md) for the decision tree + per-DB mastery target.

- [ ] **PostgreSQL** — relational OLTP default. Drill: indexes (B-tree/GIN/BRIN), MVCC, isolation levels, replication, partitioning, sharding strategies.
- [ ] **DynamoDB** _(or Cassandra OSS sibling)_ — scale-out KV/wide-column. Drill: partition key design, GSI/LSI, hot partitions, single-table pattern.
- [ ] **Redis** — cache + ephemeral state. Drill: eviction, persistence, Cluster mode, sorted sets, streams, distributed lock caveats.
- [ ] **Elasticsearch** — search + log analytics. Drill: inverted index, sharding, refresh interval, mapping, aggregations, ILM tiers. NOT system of record.
- [ ] **Kafka** — durable event log. Drill: partition+offset, acks/idempotent/transactional producer, consumer groups, ISR, retention/compaction.
- [ ] **S3 / object storage** — blob/archival/data lake. Drill: storage classes, lifecycle, pre-signed URLs, multipart, consistency model.
- [ ] **Spanner / CockroachDB** _(stretch)_ — global ACID. One-line answer + trade-off; don't deep-dive unless asked.

---

## 🎯 Concept coverage map

Tick when a problem walkthrough has *actively exercised* it (not just mentioned). Course-covered items link out for revisit; gap items become cheat-sheet candidates as problems expose them.

### Course-covered (revisit, don't drill)
- [ ] **Sharding & Replication strategies** — [EN/RU Mod 4]
- [ ] **CAP Theorem & PACELC trade-offs** — [EN/RU Mod 4]
- [ ] **ACID vs BASE, Isolation Levels** — [EN/RU Mod 5]
- [ ] **Distributed Consensus (Raft, Paxos overview)** — [EN/RU Mod 4]
- [ ] **Distributed Locking & Coordination** — [EN/RU Mod 4]
- [ ] **Message Queues + Exactly-Once Semantics** — [EN/RU Mod 5]
- [ ] **Outbox, Sagas, CQRS, Event-Driven** — [EN/RU Mod 2]
- [ ] **Circuit Breakers, Bulkheads, Rate Limiting (concepts)** — [EN/RU Mod 6]
- [ ] **Caching strategies (write-through/back, TTL, invalidation)** — [EN/RU Mod 6]
- [ ] **Auth / AuthZ, OAuth2, JWT** — [EN/RU Mod 7]
- [ ] **Observability / SRE (logs, metrics, traces, SLOs)** — [EN/RU Mod 6]
- [ ] **API Architecture Patterns (REST/gRPC/GraphQL)** — [EN/RU Mod 3]
- [ ] **Multi-tenancy strategies** — [EN/RU Mod 2]

### Gap-fill (cheat-sheet candidates — fill as problems expose them)

**Foundations (already written):**
- [x] **Database toolbox decision tree** — `concepts/db-toolbox.md` _(underpins every problem)_
- [x] **Interview framework (5-stage hardcoded flow)** — `concepts/interview-framework.md` _(canonical mock protocol)_
- [x] **Back-of-envelope estimation cheat sheet** — `concepts/back-of-envelope.html` _(QPS / storage / bandwidth + latency ladder)_
- [x] **Fanout-on-write vs fanout-on-read for feeds** — `patterns/feed-fanout.md` _(✅ Day 2 — hybrid push/pull, celeb cache)_

**Active sprint (each tied to a scheduled mock):**
- [x] **Consistent hashing** + visualization — `patterns/consistent-hashing.md` + `visualizations/consistent-hashing.html` _(✅ Day 3 — ring / vnodes / bounded-load / Maglev variants; node-add 1/N-property visualized)_
- [ ] **Geohashing / spatial indexing** + visualization — `patterns/geohashing.md` _(Day 4 — Ride-Sharing)_
- [ ] **Idempotency keys, sagas, double-spend, audit log** — `patterns/idempotency.md` _(Day 5 — Payment)_
- [ ] **Rate-limiting algorithms** (token / sliding / leaky bucket) — `concepts/rate-limiting-algorithms.md` _(Day 6 — Rate Limiter)_
- [ ] **CDN + adaptive bitrate** (HLS/DASH, transcoding pipeline) — `concepts/cdn-adaptive-bitrate.md` _(Day 7 — Video Streaming, if media target)_
- [ ] **Inverted index + trie ranking** (prefix sharding, popularity scoring) — `concepts/inverted-index.md` _(Day 8 — Search Typeahead, if search target)_

**Low-priority (gap-fill if a mock exposes weakness):**
- [ ] **Messaging decision matrix** (Kafka / Kinesis / SQS / RabbitMQ / SNS) — `concepts/messaging-toolbox.md`
- [ ] **Cache decision matrix** (Redis / Memcached / in-process / CDN edge) — `concepts/cache-toolbox.md`
- [ ] **Consistency cheat sheet** (linearizable / sequential / causal / eventual / read-your-writes / monotonic) — `concepts/consistency-cheatsheet.md` _(touched by Day 5 Payment)_
- [ ] **Multi-region / DR** (RPO/RTO, active-active vs active-passive) — `concepts/multi-region.md`
- [ ] **Bloom filters + count-min sketch in storage paths** — `patterns/probabilistic-structures.md`

**Deprioritized (sched changed — concept areas covered out of band or dropped):**
- ~~`concepts/realtime-fanout.md`~~ — was for Live Streaming Chat; Champion #3 (Messenger) covered via RU course
- ~~`patterns/cdc.md`~~ — was for Dropbox; Dropbox dropped from queue
- ~~`patterns/snowflake-id.md`~~ — sub-pattern of URL Shortener; covered inline rather than standalone
- ~~`patterns/leader-election.md`~~ — was for Job Scheduler; Stretch #9 covered out of band

---

## 🧪 Mock interview log

| # | Date | Problem | Coverage | Communication | Notes / gaps |
|---|------|---------|----------|---------------|--------------|
| 1 | 2026-04-28 | URL Shortener (calibration) | ✅ all 5 stages | ✓ coherent; pause more | ✅ Day 1 calibration goal met. Majors: skipped-HLD ask, vendor-naming, sloppy Stage 1 close, FCC+SLEDS partial scan _(framework since replaced with SCALDS+COO)_, math errors (38K→3.8K reads, base62=45→62), hot-key required probing. Strong: reset under correction, click-counter shape, ID gen, stampede insight. Full critique → [`problems/url-shortener.md`](problems/url-shortener.md). |
| 2 | 2026-05-08 | News Feed / Twitter | ✅ stages 1-4; Stage 5 ended early via `end early` (Observability covered) | ⚠️ stream-of-consciousness in HLD; reset cleanly under correction | **Lean hire / borderline**. Round-goal hot-key story landed but with heavy pushback (6 iterations to hybrid). **Majors**: Stage 1 close regression (same Day 1 miss — durability/security never stated), slow path to hybrid, materialized feed storage shape never specified, notification path scoped-in-then-evaporated, celeb classification took 2 iterations. **Strong**: read/write svc split, async Kafka fire-and-forget, ML-relocation under correction, observability section (4 golden signals + SLO/error-budget + business metrics consumer), storage math correct (Day 1 weakness fixed), cursor pagination once raised. Full critique → [`problems/news-feed.md`](problems/news-feed.md). Cheat sheet → [`patterns/feed-fanout.md`](patterns/feed-fanout.md). |

Coverage = did all 6 framework stages get hit (clarify → estimate → API → schema → high-level → deep-dive → bottlenecks)? Communication = clarity, structure, signal density.

---

## 📚 External resources (for revisit on specific topics)
- **Designing Data-Intensive Applications** (Kleppmann) — chs. 5-7 (replication, partitioning, transactions), 8-9 (consistency, consensus). The deep reference.
- **System Design Interview Vol. 1 & 2** (Alex Xu) — problem walkthroughs in interview format.
- **High Scalability blog** — real-world architecture writeups, search by company.
- **AWS Architecture Center** — reference architectures; useful for "what's the cloud-native shape?".
- **Martin Kleppmann's blog & papers** — for consensus / consistency depth.
- **Jepsen** (https://jepsen.io) — for "is this database actually consistent?" reality checks.

---

## 🚫 Confidence-trimmed (covered, low interview ROI)
- **DDD deep-dive** — interview signal is "knows aggregates exist", not modeling skill. Course covers it; revisit only if asked.
- **K8s deep-dive** — devops-flavored; rare in product-design rounds.
- **AWS resource hierarchy specifics** — interviewers go cloud-agnostic.
- **Compliance frameworks (GDPR/SOC2/HIPAA)** — only relevant for fintech/health-specific roles.
- **Reactive architecture** — light interview signal.
- **OSI model details** — anything beyond L4/L7 distinction rarely asked.
