# Database Toolbox — Curated 6 + 1

**Philosophy:** know 6 databases cold, decision-tree to one. Depth on this set > surface across twenty. Each row is a *category default*; the alternates are siblings worth one-line mention, not deep-dive targets.

---

## The decision tree

```
                   What's the workload?
                          │
        ┌─────────────────┼──────────────────┬────────────┐
        │                 │                  │            │
   Transactional      Massive scale      Sub-ms reads     Other
   relational?        (>100k w/s         /counters/        │
        │             >10TB)?            sessions?         │
        │                 │                  │             │
   Postgres          DynamoDB             Redis           ↓
   (sharded if         (or                                 │
   needed)            Cassandra            ┌───────────────┼──────────────┐
                      OSS sibling)         │               │              │
                                       Search /         Event log /     Blobs /
                                       full-text?       streaming /      large files /
                                          │             pub-sub?         archival?
                                       Elasticsearch     │                │
                                                       Kafka            S3
                                                       (or              (or GCS / Azure Blob)
                                                       Kinesis on AWS)

   ↑
   STRETCH: Need global ACID + horizontal scale?
            → Spanner / Cockroach / TiDB. Mention as "if budget allows; otherwise sharded Postgres + careful schema."
```

---

## The 6 + 1

### 1. PostgreSQL — relational OLTP default
- **Internal model:** B-tree indexes, MVCC, WAL, page-based heap.
- **Picks when:** structured data with transactions, joins, complex queries, foreign keys; up to ~10TB / ~10K writes/sec on a vertically scaled primary.
- **Mastery target:**
  - Indexing: B-tree vs hash vs GIN vs BRIN; partial / expression / covering indexes; index-only scans.
  - MVCC + isolation levels (Read Committed default; Repeatable Read = snapshot; Serializable = SSI).
  - Replication: streaming (physical) for HA; logical (per-table) for selective replication and CDC.
  - Partitioning (declarative, by range / list / hash) — read scaling and large-table maintenance.
  - When to shard: Citus / pg_shard / app-level — tradeoffs vs jumping to DynamoDB.
  - Connection pooling (PgBouncer) — Postgres scales OUT poorly, IN well. Pool aggressively.
- **Alternates worth mentioning:** MySQL (similar shape, weaker JSON / partial-index story; stronger replication ecosystem). SQLite for embedded.

### 2. DynamoDB — scale-out KV / wide-column default
- **Internal model:** consistent-hash partitioned, per-partition LSM-style storage, single-digit ms reads.
- **Picks when:** known access patterns, must scale horizontally, can model around partition key, eventual consistency acceptable (or strongly-consistent reads OK at 2× cost).
- **Mastery target:**
  - Partition key + sort key — designing the access pattern *first*, then the schema (the inverse of relational).
  - Hot partition problem and write sharding (suffix partition key).
  - GSI vs LSI — eventual consistency on GSI; max 5 LSI per table.
  - On-demand vs provisioned capacity; auto-scaling.
  - Streams + Lambda for CDC / fanout.
  - Single-table design pattern (Rick Houlihan style) — composite keys encoding multiple entity types.
  - Limits: 400KB item, 25-item batch write, 1MB query result page.
- **Alternates worth mentioning:** Cassandra (same Dynamo paper lineage, OSS, more knobs). ScyllaDB (faster Cassandra rewrite). Bigtable (Google's equivalent).

### 3. Redis — in-memory cache + ephemeral state default
- **Internal model:** in-memory data structures (strings, hashes, sorted sets, streams, HyperLogLog), single-threaded command loop.
- **Picks when:** sub-ms reads, counters, leaderboards, sessions, rate-limit buckets, ephemeral pub/sub, distributed locks (with caveats), real-time queues.
- **Mastery target:**
  - Eviction policies (allkeys-lru, allkeys-lfu, volatile-ttl, noeviction) — picking based on workload.
  - Persistence: RDB (snapshot) vs AOF (append-only log) — durability vs perf trade-off.
  - Replication: master-replica (async); Sentinel for HA; Cluster mode for sharding (16384 hash slots).
  - Lua scripting for atomicity (replaces multi-key transactions in Cluster mode).
  - Streams (XADD/XREAD/XGROUP) — Kafka-lite for small fanout.
  - Distributed lock: SET NX EX is good enough for most; Redlock if you need fencing tokens (and even then: Kleppmann's critique).
  - Sorted sets (ZADD/ZRANGE) — leaderboards, time-bucketed sliding windows.
- **Alternates worth mentioning:** Memcached (simpler, no persistence, no rich types — pure cache). Hazelcast / Apache Ignite (JVM in-memory grid).

### 4. Elasticsearch — search + log analytics default
- **Internal model:** Lucene inverted index, sharded + replicated, BM25 scoring.
- **Picks when:** full-text search, fuzzy match, autocomplete, faceted search, log aggregation (ELK), analytics dashboards over text-heavy data.
- **Mastery target:**
  - Inverted index basics — token streams, analyzers (standard / language-specific / ngram for autocomplete).
  - Sharding (primary + replica shards); cannot resharding without reindexing — capacity planning matters.
  - Refresh interval (default 1s) — search visibility lag; tunable.
  - Mapping types vs dynamic mapping — schema-on-write vs schema-on-read tension.
  - Aggregations (terms, date_histogram, percentiles) — analytics shape.
  - Index lifecycle management (hot/warm/cold tiers) for log retention.
  - **Not** a primary store — derived from a system of record (Postgres / Dynamo) via CDC / dual-write / outbox.
- **Alternates worth mentioning:** OpenSearch (AWS fork, license drama). Solr (older, similar Lucene base). Algolia / Typesense (managed search-as-API).

### 5. Kafka — durable event log + streaming default
- **Internal model:** partitioned commit log; ordered within partition, not across; consumer offsets.
- **Picks when:** event sourcing, CDC pipelines, audit logs, pub/sub at scale, decoupling services, replay-able data flows.
- **Mastery target:**
  - Partition + offset model; ordering guarantee is **per-partition only**.
  - Producer: acks=0/1/all, idempotent producer (dedup within session), transactional producer (exactly-once across topics).
  - Consumer groups + offset commit; rebalance strategy (sticky / cooperative-sticky to reduce rebalance churn).
  - ISR (in-sync replicas), unclean leader election trade-off (availability vs durability).
  - Retention: time-based vs size-based; compacted topics (latest-value-per-key) for materialized state.
  - Schema registry (Avro / Protobuf) — backward / forward compatibility.
  - Kafka Streams + ksqlDB at the conceptual level (don't go deep unless asked).
- **Alternates worth mentioning:** AWS Kinesis (Kafka-like, AWS-managed, simpler API but fewer features). Pulsar (multi-tenant, tiered storage, segment-based). RabbitMQ (smart broker, classic queue — different shape from log).

### 6. S3 (object storage) — blob / archival / data lake default
- **Internal model:** flat key-value (key = path), eventually consistent for ~years, **strongly consistent since 2020** (read-after-write for new objects).
- **Picks when:** images / videos / documents, static assets, backups, data lake (with Parquet/Delta), archival.
- **Mastery target:**
  - Bucket / key model; "directories" are just key prefixes.
  - Storage classes (Standard / Standard-IA / Glacier / Glacier Deep Archive) — cost vs retrieval latency.
  - Lifecycle rules (auto-tier after N days; expire after M days).
  - Versioning + object lock (immutability for compliance).
  - Pre-signed URLs — time-limited direct access without proxying through your service.
  - Multipart upload — for files >5MB, mandatory >5GB.
  - Eventually consistent for **listing**; strongly consistent for read-after-write on a known key.
  - With CloudFront / CDN in front for global edge caching.
- **Alternates worth mentioning:** GCS / Azure Blob (same shape, different ecosystem). MinIO (self-hosted S3-compatible).

### +1. Spanner / CockroachDB / TiDB — global ACID stretch
- **Internal model:** Paxos/Raft replicated ranges, TrueTime (Spanner) or HLC (Cockroach) for global ordering.
- **Picks when:** need ACID + horizontal scale + multi-region; willing to pay 10-100ms commit latency for cross-region consistency.
- **Mastery target:**
  - One-line answer: "Spanner-like = global serializable, but each commit pays a coordination round-trip; only worth it when sharded Postgres can't and CockroachDB/Spanner budget exists."
  - Don't drill internals unless interviewer leans into it.
- **Alternates:** YugabyteDB, FoundationDB.

---

## Quick decision rules

- **Default everything to Postgres** unless the workload requires otherwise. "Why not Postgres?" is the question to answer. Going to a NoSQL store should have a *reason*: scale, latency, schema flexibility, geo-distribution.
- **Cache layer is Redis** unless you specifically need Memcached's simplicity (rare).
- **System of record + search?** → Postgres + Elasticsearch with CDC. Don't make Elasticsearch your primary.
- **Event-driven flow?** → Kafka. Even if a queue would do, Kafka's replay + retention earns its keep.
- **Blobs?** → S3. Always. Don't store binaries in your database.
- **Multi-region active-active?** → start with "what's our consistency requirement?" If eventual: DynamoDB Global Tables, or async Postgres replication with conflict resolution discussion. If strong: Spanner-class system, accept the latency.

## Interview anti-patterns
- **Naming a DB you don't actually know.** Interviewers WILL drill. "We'd use Cassandra" → "tell me about gossip / hinted handoffs / read repair." If you haven't drilled it, don't name it.
- **Switching DBs mid-design** because you remembered an issue. Pick the one you can defend, defend it, note the trade-off.
- **Going to NoSQL out of habit** when Postgres would do. Staff interviewers test judgment, not pattern-matching.
- **Forgetting that DynamoDB is the cousin of Cassandra** — same Dynamo paper. Knowing one transfers most of the other.
