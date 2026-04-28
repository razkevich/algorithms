# System Design Prep — Progress Tracker

Mock-driven prep for staff/tech-lead loops at first/second-tier companies (distributed systems, SaaS, backend).

**Baseline courses (already studied — revisit, don't drill):**
- 🇬🇧 [English course](https://sysdesign-course-t83nq.ondigitalocean.app/course)
- 🇷🇺 [Russian course](https://course.sysdesign.online/course) — also has built-in case studies: Airbnb, Messenger, Notification Service, URL Shortener

**This tracker covers the *interview performance* layer**: problem walkthroughs that drill the framework + flow, surface concept gaps, and let cheat sheets accumulate as side-effects.

---

## 🔥 Problem queue — priority-ordered for staff/tech-lead loops

Tier 1 covers ~70% of distributed/SaaS/backend rounds. Tier 2 adds ~20%. Tier 3 is breadth.

### Tier 1 — must-do (drill before any real loop)
- [ ] **URL Shortener** — base "DB + cache + ID gen" template. _(Russian course has case study — review after attempt for canonical comparison.)_
- [ ] **News Feed / Twitter** — fanout-on-write vs read, hot-key handling, hybrid. _(Concept gap: feed-fanout cheat sheet.)_
- [ ] **WhatsApp / Messenger** — real-time, message ordering, presence, delivery guarantees. _(Russian course has case study — compare after.)_
- [ ] **Uber / ride-sharing** — geohashing, dispatch matching, surge pricing. _(Concept gap: spatial indexing cheat sheet.)_
- [ ] **Distributed Cache (Redis-like)** — consistent hashing, eviction, replication. _(Concept gap: consistent-hashing visualization.)_
- [ ] **Distributed Rate Limiter** — token bucket / sliding window in shared store, edge vs central enforcement.
- [ ] **Notification System** — fanout reliability, channel abstraction (email/SMS/push), retry/dedup. _(Russian course has case study — compare after.)_

### Tier 2 — high-value
- [ ] **Airbnb / booking** — concurrency, double-booking prevention, search + filtering. _(Russian course has case study — compare after.)_
- [ ] **Web Crawler** — distributed scheduling, dedup, politeness, freshness budget.
- [ ] **Dropbox / Google Drive** — chunking, dedup, conflict resolution, sync protocol.
- [ ] **Video Streaming (Netflix-style)** — CDN, adaptive bitrate (DASH/HLS), ingestion pipeline.
- [ ] **Search Autocomplete** — trie, popularity ranking, freshness, prefix sharding.
- [ ] **Distributed ID Generator (Snowflake-style)** — clock skew, monotonicity, collision avoidance.

### Tier 3 — breadth (round out coverage)
- [ ] **Payment System** — idempotency keys, sagas, double-spend prevention, audit log.
- [ ] **Distributed Log / Kafka clone** — partitioning, retention, ISR, exactly-once.
- [ ] **Recommender System** — feature store, online vs batch scoring, A/B test infra.
- [ ] **Real-Time Analytics** — Lambda/Kappa architecture, watermarks, exactly-once.
- [ ] **Distributed File System (GFS-like)** — chunk servers, master, fault tolerance.

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
- [x] **Database toolbox decision tree** — `concepts/db-toolbox.md` _(starter — written first because it underpins every problem)_
- [x] **Interview framework (5-stage hardcoded flow)** — `concepts/interview-framework.md` _(the canonical protocol both sides follow during every mock)_
- [ ] **Back-of-envelope estimation cheat sheet** — `concepts/back-of-envelope.md` _(QPS / storage / bandwidth math)_
- [ ] **Messaging decision matrix** (Kafka / Kinesis / SQS / RabbitMQ / SNS) — `concepts/messaging-toolbox.md`
- [ ] **Cache decision matrix** (Redis / Memcached / in-process / CDN edge) — `concepts/cache-toolbox.md`
- [ ] **Consistency cheat sheet** (linearizable / sequential / causal / eventual / read-your-writes / monotonic) — `concepts/consistency-cheatsheet.md`
- [ ] **Consistent hashing** + visualization — `patterns/consistent-hashing.md`
- [ ] **Geohashing / spatial indexing** — `patterns/geohashing.md`
- [ ] **Snowflake-style ID generation** — `patterns/snowflake-id.md`
- [ ] **Fanout-on-write vs fanout-on-read for feeds** — `patterns/feed-fanout.md`
- [ ] **Idempotency keys, request hedging, retries with backoff/jitter** — `patterns/idempotency.md`
- [ ] **CDC (Change Data Capture) patterns** — `patterns/cdc.md`
- [ ] **Multi-region / DR (RPO/RTO, active-active vs active-passive)** — `concepts/multi-region.md`
- [ ] **Bloom filters + count-min sketch in storage paths** — `patterns/probabilistic-structures.md`

---

## 🧪 Mock interview log

| # | Date | Problem | Coverage | Communication | Notes / gaps |
|---|------|---------|----------|---------------|--------------|
| _empty — fill after each mock_ | | | | | |

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
