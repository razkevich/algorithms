# URL Shortener — Day 1 Mock (Calibration)

**Date:** 2026-04-28 → 2026-04-29 · **Day:** 1 / 10 · **Mock #:** 1 · **Status:** ✅ complete (clock-off mode)

---

## 🔒 Round agreement (was locked pre-mock)

> Goal: complete all 5 stages, end-to-end. Stage 2 (Entities) explicitly executed. FCC + SLEDS scan visible in Stage 1. Pick *one* DB and stick with it (no engine-shopping mid-design).

**Day 1 calibration prioritized process over depth — better to land a complete working system than to perfect any single stage.** Goal achieved.

---

## ✅ Stage 1 — Requirements (closed)

### Functional (top 3)
1. Create short link from long URL
2. Resolve short link → 302 redirect to long URL
3. Collect & expose click statistics (public per-link, no auth gate)

### Non-functional (quantified, FCC+SLEDS scanned)
| Letter | Requirement |
|--------|-------------|
| **L** | Read p99 < 200ms (NA region), write p99 < 1s |
| **F** | 99.95% availability (~4h/year downtime) |
| **S** | 100M creates/month → ~40 writes/sec avg, ~4K reads/sec avg; **peak = 10× avg** → 400 writes/sec, 40K reads/sec; hot key 10× per-key avg |
| **C(AP)** | Eventual consistency OK on resolve-after-create |
| **D** | No data loss on the short→long mapping |
| **E** | Global service, single-region deployment for v1; SLA only enforced for NA |
| **C(omp)** | Out of scope |
| **S(ec)** | Out of scope |

### Out of scope
Custom aliases · frontend · auth/signup (anonymous) · security/DDoS · multi-region · compliance.

### Process notes captured during Stage 1
- Initial close was sloppy — had to be force-closed with explicit artifact list. **Drill: end Stage 1 with verbal sign-off "locking these three: [funcs] [NFRs] [OOS]."**
- FCC+SLEDS partial scan — `E` and `D` only addressed when prompted. Both proved load-bearing (E: 200ms cross-continent RT impossible from one region; D: lost mapping = broken link forever).
- Read QPS math 10× off (38K → 3.8K). Self-corrected after prompt.
- Availability self-quoted at 99.9% but PM brief specified 99.95%. Locked at 99.95.

---

## ✅ Stage 2 — Core Entities (closed)

| Entity | What it represents |
|--------|---------------------|
| **Mapping** | The short→long URL pair (the core data the system owns) |
| **UserAction** | Action log for creates / accesses (source for click statistics) |

### Process notes
- Initial entity list conflated attributes (short URL / long URL) with entities. Required correction: those are *fields* of one entity (Mapping); the mapping IS the entity.
- UserAction is over-generalized for v1 (creates are implicit in the Mapping row's existence). Will narrow to ClickEvent in iteration.

---

## ✅ Stage 3 — API Design (closed)

```
POST  /v1/mappings                                                 { longUrl }
                                                                   → 201 { shortcode, shortUrl }

GET   /{shortcode}                              [short domain]     → 302 redirect to longUrl

GET   /v1/analytics/{shortcode}/clicks?from=&to=                   → 200 [{ date, count }, ...]

GET   /v1/analytics/{shortcode}/total_clicks                       → 200 { total }
```

### Process notes
- 302 chosen over 301 deliberately so every click hits server (preserves analytics). Correct call.
- Initial path used `{short_url}` — corrected to `{shortcode}` (full URLs don't belong in path segments).
- Top-level `/v1/analytics/...` resource path is non-canonical (canonical: `/v1/mappings/{shortcode}/stats` as sub-resource). Accepted as deliberate choice.
- Two-endpoint split (clicks + total_clicks) not actively defended. Defensible answer if probed: different cache profiles.
- **Senior-judgment moment**: candidate initially de-scoped analytics on dependency chain (analytics → identity → auth → OOS). PM-pushback surfaced public-stats alternative; candidate updated correctly.

---

## ✅ Stage 4 — High-Level Design (closed)

### First pass — required restart
Vendor-specific naming (EKS / API Gateway / Ingress / Deployment) flagged as a senior-vs-mid signal. Candidate reset cleanly and redelivered logical-first.

### Final shape

```
client ──► API Gateway (logical) ──► Stateless app tier ──┐
                                          │              │
                                          ▼              │
                                      Redis (read-       │
                                      through cache)     │
                                          │              │
                                          ▼              │
                                      Postgres ◄────── Ticket server
                                                       (issues ID ranges of 1000
                                                        in atomic transaction;
                                                        last-used persisted in DB)

  mappings:                              analytics (counter table):
  ┌──────────────────────────┐           ┌──────────────────────────┐
  │ id          BIGINT  PK   │           │ shortcode   FK           │
  │ shortcode   VARCHAR  IDX │           │ date        DATE         │
  │ long_url    TEXT         │           │ count       BIGINT       │
  │ created_at  TIMESTAMP    │           │ PK (shortcode, date)     │
  └──────────────────────────┘           └──────────────────────────┘
```

### ID generation
Ticket server returns ranges of 1000 IDs in atomic DB transaction. Multiple ticket-server instances scale stateless. ID space: base62, 6 chars → 57B (well above 6B/5yr need).

### Schema notes
- `id` BIGINT PK chosen over `shortcode` PK for: simpler ticket-server ID gen, future-proofing for bulk ops/pagination, derived `shortcode = base62(id)`.
- Index on `shortcode` for redirect lookups.
- `created_at` had to be added when prompted.

### Caching
Redis read-through cache absorbs power-law read traffic. Most popular shortcodes pinned; tail handled by Postgres + read replicas.

### CDN
Initially proposed CDN in front of API Gateway. Conflict raised: CDN-cached redirects bypass server → break click analytics. **Dropped CDN.**

### Process notes
- Asked to skip HLD entirely → pushback → accepted "accelerated HLD" (option B).
- Vendor-naming corrected on second pass.
- Schema initially missing `created_at`.
- base62 math: candidate said 45 chars (incorrect — actually 62, alphanumeric). Conclusion right (6 chars enough), math wrong.

---

## ✅ Stage 5 — Deep Dive (closed)

### Walked NFRs

**L (latency < 200ms read)**
- Postgres baseline + Redis cache absorbs hot tail
- Read replicas (2-10 based on burst — count unquantified, would need refinement)
- Tens-of-ms typical via Redis hit

**F (99.95% availability)**
- Stateless app tier scales via K8s
- Postgres single-master accepted as trade-off (downtime acceptable during failover)
- **Failover automation not named** — would mention Patroni / RDS Multi-AZ / Stolon for senior depth
- Ticket server: stateless multi-instance, last-used-ID persisted
- **Ticket-server race condition not articulated** — atomic UPDATE...RETURNING needed for concurrent range fetches

**S (40K peak reads, 10× hot key)**
- Read side: Redis + replicas
- Click-write side: **async event stream → consumer → batched flush** (correct shape; SQS or Kafka)
- Hot-key on Redis required probing — senior+ should self-volunteer this

**C (eventual consistency)**
- Matches design (read replicas + cache TTL acceptable)

**D (no data loss)**
- Background job: weekly archive job (initially with deletion bug — fixed via two-table tiered approach), daily backups to S3 cold tier
- Self-flagged "primitive" counter-table issue → resolved correctly via async queue

### Probe 1 — archival logic
Initial proposal had archival + deletion bug (would break "links forever" guarantee). Candidate corrected to two-table tiered approach (current + archive, query current first then archive). Functional but reinvents Postgres native declarative partitioning (`PARTITION BY RANGE (created_at)` + `pg_partman`).

### Probe 2 — hot key on Redis
- Single-key concentration → addressed via L1 in-process cache + random-suffix key replication ✓
- Cache stampede → candidate observed (correctly) that random-suffix replication also mitigates stampede via TTL staggering. **Sharp connection.**

---

## 📊 Severity-rated critique

### 🔴 Blockers — none
Round agreement met. All 5 stages closed end-to-end. Day 1 calibration goal achieved.

### 🟠 Majors (drill before Day 2)
1. **Asked to skip HLD entirely** — read as memorization in a real loop. Use "accelerated HLD" instead.
2. **Vendor-specific naming** on first HLD pass (EKS / API Gateway / Deployment). Logical-first vocabulary needs muscle memory.
3. **Stage 1 sloppy close** — required force-closure with explicit artifact list.
4. **FCC + SLEDS partial scan** — `E`, `D`, second `C` skipped silently. E was load-bearing (cross-continent RT vs 200ms p99). Memorize before Day 2.
5. **Math errors not pre-caught** — 38K vs 3.8K reads; base62 = 45 vs 62. Drill: 100M/mo → 40 w/s, 4K r/s @ 100:1; base62 = 62; 62⁶ ≈ 57B.
6. **Hot-key on Redis required probing** — should self-volunteer anytime "burst" / "viral" / "popular" appears in NFRs.

### 🟡 Minors (polish)
- UserAction entity over-generalized (narrows to ClickEvent)
- Two-endpoint analytics split not actively defended
- CDN-vs-302 conflict surfaced only when probed
- Two-table archival vs Postgres declarative partitioning
- Read-replica count unquantified
- Failover automation unnamed (Patroni / RDS Multi-AZ / Stolon)
- Ticket server race not articulated (atomic UPDATE...RETURNING)
- Observability not volunteered (senior+ should self-surface)

### Communication
- ✓ Coherent once redirected; took corrections without sulking (senior signal)
- ⚠️ Stream-of-consciousness in long answers — pause more, let interviewer probe

---

## 🎯 Day 2 drill list (priority)

1. **FCC + SLEDS memorization** — full recital target before Day 2 mock
2. **Stage 1 closing ritual** — verbal sign-off with the 3 artifacts (top-3 funcs / 3-5 NFRs / OOS)
3. **Vendor-neutral architecture vocabulary** — drill "logical-first" phrasing
4. **Math canon (URL Shortener)** — 100M/mo → 40 w/s, 4K r/s @ 100:1; base62 = 62; 62⁶ ≈ 57B; 62⁷ ≈ 3.5T
5. **Self-volunteer hot-key handling** — checklist item for any S-tier NFR mentioning load skew

---

## 🟢 Strong signals to keep
- **Reset under correction** without sulking (M1/M2 both required reset; both were taken cleanly)
- **Click-counter async-queue solution** (correct shape, named Kafka alternative)
- **ID generation reasoning** (ticket server, range pre-fetch, crash trade-off articulation)
- **Probe 2 stampede insight** (random-suffix → TTL stagger → stampede mitigation)
- **Senior judgment on de-scoping** (dependency-chain reasoning, even if alternative was missed)
- **Self-flagging "primitive" deferrals** with explicit "fix later" pointers
