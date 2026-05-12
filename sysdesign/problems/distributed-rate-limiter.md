# Distributed Rate Limiter — Day 6 Reference

**Date:** 2026-05-12 · **Day:** 6 / 10 · **Mock #:** — · **Status:** ✅ reference (mock skipped — user opted for direct writeup)

> **Why skipped:** user reported solid grasp of the problem shape and chose a concise reference walkthrough over a live 45-min mock. Captured here as a study artifact; mock-style critique not applicable.

---

## Stage 1 — Requirements

### Functional (top 3)
1. **Check-and-consume** on every API request: given `(tenant_id, endpoint, cost)`, return allow/deny + remaining budget.
2. **Multi-dimensional, multi-window limits** — per-tenant global, per-endpoint, per-(tenant, endpoint); per-second + per-day windows. *All applicable rules must pass.*
3. **429 + standard headers** (`Retry-After`, `X-RateLimit-{Limit,Remaining,Reset}`) and rule changes propagated globally within <1 min.

### Non-functional (SCALDS + COO)
| | |
|---|---|
| **S**cale | 1M QPS global; largest tenant 50–100K QPS sustained; 100K active tenants |
| **C**onsistency | Best-effort; ±5–10% overshoot acceptable. Convergence in seconds. |
| **A**vailability | 99.99% — **fail open** under degradation (WAF handles attackers; limiter enforces contractual fairness, not abuse defense) |
| **L**atency | p99 ≤5ms added (synchronous hot path) |
| **D**urability | Counters are ephemeral; only rule config is durable |
| **S**ecurity | Internal; signed tenant ID from upstream gateway, never trust body |
| **C**ost | In-memory store; tenant cardinality drives RAM, not request volume |
| **O**bservability | Per-tenant allow/deny rate, p99 store latency, degraded-mode counter |
| **O**perability | Rule change → globally effective in <1 min (Enterprise sales onboarding gate) |

---

## Stage 2 — Core entities

- **Tenant** — quota holder (with tier: Free / Pro / Enterprise)
- **Endpoint** — route identifier (`POST /search`, `GET /users`)
- **LimitRule** — `(scope, algorithm, capacity, refill_rate, window)`; scope = tenant, endpoint, or (tenant, endpoint)
- **Counter** — ephemeral `(key → tokens, last_refill_ts)`
- **TierDefault** — fallback rules when no tenant-specific rule exists

---

## Stage 3 — API

**Hot path (internal, gRPC, called by gateway):**
```
CheckAndConsume(tenant_id, endpoint, cost=1)
  → { allow: bool, remaining: int, reset_at: ts, retry_after_ms?: int }
```
*Tenant ID is from the signed upstream header — never from the request body.*

**Control plane (admin REST):**
```
POST   /v1/limit-rules
PUT    /v1/limit-rules/{id}
DELETE /v1/limit-rules/{id}
GET    /v1/limit-rules?tenant_id=...
```

---

## Stage 4 — High-level design

```
[client] → [API gateway] → CheckAndConsume → [RL worker (stateless)]
                                                    │
                                                    ▼
                                        [Redis Cluster — sharded by tenant_id]
                                                    │
                              [Control plane: Postgres + Kafka rule-change topic]
                                                    │
                                       (workers subscribe; cache rules in-memory)
```

**Algorithm: token bucket.** Why:
- Burst tolerance is a product requirement (PM said "quiet tenants shouldn't be blocked for a 2x spike").
- Constant memory per key: `(tokens, last_refill_ts)`.
- Single atomic op: refill-then-decrement, expressible in a Redis Lua script.
- No calendar-boundary double-spend (unlike fixed-window).

**Storage: Redis Cluster.** Why:
- In-memory → ≤1ms p99.
- Lua scripts give atomicity (no read-modify-write races on counters).
- Hash-tag sharding `{tenant_id}:endpoint` co-locates a tenant's multi-dimensional keys on one shard → single pipelined round-trip checks all applicable rules.
- TTL on idle keys auto-reclaims memory.

**Atomic refill+decrement (Lua, conceptual):**
```lua
local tokens, last = redis.call("HMGET", key, "tokens", "last")
tokens = math.min(cap, tokens + (now - last) * refill_rate)
if tokens >= cost then
  redis.call("HMSET", key, "tokens", tokens - cost, "last", now)
  return {1, tokens - cost}     -- allow
else
  return {0, tokens}             -- deny
end
```

**Control plane:** rules in Postgres (system of record) → change-event published to Kafka → all RL workers consume + update in-memory rule cache. Worker boot loads full snapshot. New tenants fall back to tier defaults.

---

## Stage 5 — Deep dive (walking NFRs)

### Latency (p99 ≤5ms)
- RL workers co-located with gateway in same AZ; sidecar or library, not separate hop.
- One Redis round-trip per request — multi-rule pipelined under shared hash tag.
- Lua script cached on Redis side (`EVALSHA`).
- Optional: local LRU "definitely-not-limited" cache for low-traffic tenants (skip Redis when observed QPS << limit). Trade-off: looser bounds, but still within tolerance.

### Hot keys — the central distributed-systems problem
A 50K-QPS tenant's counter is one Redis key → one shard → CPU saturation. Two layers:

1. **Local sub-bucket / token allocation** (a.k.a. distributed token bucket — Stripe/Envoy pattern):
   - Each worker holds a local *slice* of the tenant's global quota (e.g., 100 tokens of 10K).
   - Decrement from local bucket on the hot path → no Redis hop most of the time.
   - When local slice depletes, pull another slice via `CheckAndConsume(slice_size)`.
   - **Trade-off**: workers can each hold unspent slices when traffic shifts → slight overshoot. Sized so worst case stays inside the ±5–10% product tolerance.

2. **Pre-shard the key** for the very-large tenants (>50K QPS):
   - Split into N virtual sub-keys: `tenant:foo:shard:0..15`, round-robin assigned to workers.
   - Periodic reconciliation merges sub-counters for visibility/billing; enforcement is per-slice.
   - This is the same idea as Cassandra's "anti-hot-partition" bucket pattern.

### Availability + failure modes
- **Fail open** by PM mandate. If Redis unavailable or exceeds 3ms budget → allow + emit `degraded_mode_allows{tenant}` counter.
- Two-tier degradation:
  1. Primary Redis replica failover (Cluster handles this automatically).
  2. Full Redis outage → workers run *local-only* from their cached slice + rule cache. Bound by cache TTL; eventually all tenants converge to "tier default" behavior.
- Multi-region: Redis cluster per region. Global tenant quotas reconciled by background aggregator (sum regional counters every few seconds, redistribute slice sizes). Best-effort by design — the ±5–10% tolerance buys this.

### Consistency / boundary error
- Token bucket has no fixed-window boundary bug (no 2x burst at second-rollover).
- Refill math uses **Redis server time** as single source of truth — worker clocks irrelevant. Eliminates clock-skew races.

### Observability + operability
- Per-tenant: req/s, allow/deny ratio, p99 Redis latency, degraded-mode invocations.
- Rule rollout supports versioning + percentage-based shadow mode ("enforce on 10% of traffic, log on 90%") for safe new-limit deployment.
- Support team gets per-tenant rate-limit hits in admin dashboard.

### Edge cases
- **Cost-weighted requests** (search = 5 tokens): `cost` is an arg to the script.
- **Multiple rules per request**: pipeline N script calls under same hash tag → AND the allow bits → emit the *strictest* `retry_after`.
- **New tenant** (no rule yet): fall back to tier default rule, lazily materialize on first request.
- **Rule change mid-request**: monotonic rule version on the worker; if a `CheckAndConsume` raced a rule update, accept the slight inconsistency (within tolerance).

---

## Trade-offs explicitly accepted

| Choice | What it buys | What it costs |
|---|---|---|
| Token bucket (vs sliding-window-counter) | Natural bursts, simpler math, one key | Slightly looser boundary accuracy |
| Local sub-buckets | Hot-key elimination, sub-ms p99 | Up to ~10% overshoot when traffic shifts |
| Fail open | Availability, latency floor | Won't stop abuse during outage (WAF does) |
| Redis (vs Postgres for counters) | 100x throughput, microsecond ops | No durability — fine for ephemeral counters |
| Eventually consistent cross-region | Sub-5ms p99 globally | Global quotas drift few seconds before reconcile |

---

## Pattern transfer

- **Local sub-bucket / slice-and-replenish** → same shape as Snowflake ID generators (workers grab a range, mint locally); same shape as Kafka idempotent producer's PID/epoch range.
- **Hash-tag co-location for atomic multi-key ops** → reusable everywhere you need cross-key consistency in Redis Cluster (multi-window counters, related session keys, leaderboards).
- **Pre-shard hot key into N virtual partitions** → same anti-hot-partition pattern used in Cassandra wide rows, DynamoDB write-sharding, Kafka high-fanout partition keys.
- **Fail-open + degraded-mode metric** → reusable pattern for any synchronous-path enforcement service (auth cache, feature flags, schema validators).
