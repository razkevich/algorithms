# Rate Limiting Algorithms — Cheat Sheet

Decision matrix for the 5 algorithms you'll be asked to name + pick between in any rate-limiter round.

---

## The 5 algorithms in one paragraph

**Fixed window** counts hits in calendar buckets (00:00–00:01). Simple, cheap, but suffers boundary-burst (2x at second-rollover). **Sliding window log** stores a timestamped list per key — perfectly accurate but O(N) memory per active key. **Sliding window counter** weights two adjacent fixed windows by elapsed fraction — near-perfect accuracy at O(1) memory; the academic favorite. **Token bucket** refills tokens at a steady rate up to a cap; allows bursts up to the cap. **Leaky bucket** processes requests from a FIFO at a constant drain rate; smooths bursts to a steady output.

---

## Decision matrix

| Algorithm | Burst behavior | Memory per key | Boundary accuracy | Best for |
|---|---|---|---|---|
| **Fixed window** | Allows 2x at boundary | 1 counter | ❌ poor | Quick & dirty, monthly quotas where 2x burst tolerable |
| **Sliding log** | Strict | O(req-in-window) | ✅ perfect | Small windows, low traffic, audit-grade |
| **Sliding counter** | Strict, smooth | 2 counters | ✅ near-perfect | Production default when no bursts allowed |
| **Token bucket** | Allows bursts up to cap | 2 numbers (tokens, ts) | ✅ no boundary | When bursts are a *feature* (most real APIs) |
| **Leaky bucket** | Smooths to constant rate | Queue depth | ✅ no boundary | Traffic shaping, when you must protect a downstream that can't burst |

---

## Picking criterion (one-line each)

- **PM says "bursts OK / 2x quiet-period spike is fine"** → **token bucket**
- **PM says "strict — never exceed by more than 1%"** → **sliding window counter**
- **You're shaping flow to a downstream that processes at fixed rate** (e.g., outbound webhook delivery to slow customer) → **leaky bucket**
- **Audit / billing precision matters more than memory** → **sliding log**
- **Prototype / cron-style quotas** ("100 calls per minute, refresh on the minute") → **fixed window** (and accept the boundary bug)

---

## The fixed-window boundary bug — why it disqualifies fixed window in real systems

```
Window starts: 12:00:00, limit = 100/min
12:00:59 — 100 requests arrive → all allowed (window has room)
12:01:00 — counter resets
12:01:01 — 100 more requests arrive → all allowed
```

Result: **200 requests in 2 seconds**, against a "100/min" limit. Sliding-window-counter fixes this by weighting the prior window's contribution proportionally to the elapsed fraction.

---

## Token bucket — the workhorse

**State per key:** `tokens` (float), `last_refill_ts`.

**On request with cost `c`:**
```
now = current_time()
tokens = min(capacity, tokens + (now - last_refill_ts) * refill_rate)
last_refill_ts = now
if tokens >= c:
  tokens -= c
  return ALLOW
else:
  return DENY, retry_after = (c - tokens) / refill_rate
```

**Properties:**
- 2 numbers per key. Memory-bounded.
- No boundary bug — refill is continuous.
- Bursts allowed: an idle tenant accumulates tokens up to `capacity`, then can spend them all at once.
- One atomic op (Redis Lua script) → no read-modify-write races.

**Distributed variant: local sub-bucket** — each worker holds a slice of the global capacity, decrements locally, replenishes from the central store when empty. Eliminates hot-key load on the central store at the cost of slight overshoot (workers hold unspent slices). Stripe / Envoy use this.

---

## Sliding window counter — when bursts are forbidden

**State per key:** `current_window_count`, `prev_window_count`, `current_window_start`.

**On request:**
```
elapsed_in_current = (now - current_window_start) / window_size
weighted_total = prev_window_count * (1 - elapsed_in_current) + current_window_count
if weighted_total < limit:
  current_window_count += 1
  return ALLOW
else:
  return DENY
```

**Properties:**
- 3 numbers per key.
- Approximates a true sliding window using two adjacent fixed windows weighted by elapsed fraction.
- Accuracy: < 1% error for uniform traffic, slightly higher for very bursty.
- No bursts — by design.

---

## Sliding window log — when you need exactness

**State per key:** sorted set of timestamps (Redis ZSET).

**On request:**
```
ZREMRANGEBYSCORE key 0 (now - window)   # drop expired
count = ZCARD key
if count < limit:
  ZADD key now now
  return ALLOW
else:
  return DENY
```

**Properties:**
- Perfectly accurate.
- O(req-in-window) memory per key — explodes for high-QPS tenants.
- Two Redis ops (could be one Lua).
- Use only when accuracy matters more than memory: billing-tied quotas, audit-grade rate-limits.

---

## Leaky bucket — shaping, not just limiting

**Conceptually:** requests enter a FIFO queue, drain at constant rate. Queue full → reject.

**Implementation:** essentially a token bucket where you *also* delay (not just reject) excess. Often built on a real queue (in-memory or Redis list).

**When to use:** you have a downstream that genuinely cannot burst (legacy customer webhook, partner SLA caps inbound). Token bucket would *allow* the burst then let it hit the downstream; leaky bucket *smooths* it.

**When NOT to use:** standard API rate limiting. Adds latency (queue wait) for marginal benefit over token bucket.

---

## Anti-patterns

- **Database-backed counters at high QPS** — `UPDATE counters SET n = n + 1 WHERE key = ?` on every request. Lock contention murders you at 1K QPS, let alone 100K. Use Redis (or in-memory with periodic flush).
- **Read-then-write without atomicity** — race condition where two workers both read `tokens=1`, both decrement to `0`, both allow. Always use Lua / `WATCH` / atomic op.
- **Per-second windows only** — real APIs need per-second (burst) AND per-day (quota) AND per-month (billing). Multi-window is standard.
- **Trusting client-supplied tenant ID** — always pull from signed upstream header.
- **Strict cross-region consistency** — global quotas reconciled async (every few seconds) is almost always acceptable; cross-region strong consistency on every request is a 10x latency cost for sub-1% accuracy gain.

---

## Pattern transfer

- **Token bucket** ↔ Snowflake-ID worker slice (worker holds a range, decrements locally) ↔ Kafka idempotent producer's PID epoch range.
- **Sliding-window-counter weighting** ↔ exponential moving averages (a "leaky" approximation of an expensive exact stat).
- **Fail-open on store outage** ↔ default-allow pattern in any synchronous enforcement service (auth cache, feature flag service, schema validator).
