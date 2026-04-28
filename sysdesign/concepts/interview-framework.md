# System Design Interview Framework — The 5-Stage Flow

**Source:** condensed from the Russian sysdesign course transcript ("Структурированный подход"). This is the canonical framework — both interviewer and candidate follow it during every mock in this workspace.

**The single rule that determines pass/fail:** *finish a working end-to-end system within the time box.* Most candidates fail not because of weak technical knowledge, but because they get stuck on details and never reach Deep Dive. Time discipline > depth.

---

## The 5 stages (45-60 min total)

| Stage | Time | What you produce |
|-------|------|------------------|
| 1. Requirements (functional + non-functional) | 5 min | 3 functional + 3-5 quantified non-functional |
| 2. Core entities | 2 min | Bulleted list, "v1 — will iterate" |
| 3. API design | 5 min | Endpoints with paths, methods, body shape |
| 4. High-level design | 10-15 min | Boxes-and-arrows architecture + schema annotations |
| 5. Deep dive | 10 min | Address each non-functional req, edge cases, bottlenecks |

**Total: 32-37 min for the core flow.** The rest is follow-up Q&A, pivots, and recovery. Hard cap each stage — the interviewer (me) will call time and move you forward whether you're ready or not.

---

## Stage 1 — Requirements (5 min)

### Functional ("Users should be able to…")
- **Pick exactly top 3.** Not 5, not 6. Every extra functional requirement is a commitment to design for it. Strategic prioritization > breadth.
- Drive it like a PM conversation: "Does the system need X?" "What if Y?"
- Twitter example: (1) post tweets, (2) follow other users, (3) see feed of followed. *Not* likes, retweets, DMs — those are extensions if time allows.

### Non-functional ("System should be / have…")
- **Pick top 3-5, quantified.** "Be available" is useless; "feed loads in <200ms" is a target.
- Use the **FCC + SLEDS** checklist to scan:
  - **F** — Fault Tolerance: redundancy, failure modes
  - **C** — CAP: consistency vs availability for this system?
  - **C** — Compliance: GDPR / HIPAA / SOC2 if domain demands
  - **S** — Scalability: peak QPS, data volume, growth rate
  - **L** — Latency: p99 targets per operation
  - **E** — Environment: mobile / battery / low-bandwidth / regions?
  - **D** — Durability: how much data loss is acceptable?
  - **S** — Security: data protection, access control

### Estimation (back-of-envelope)
- **Default: skip.** Tell the interviewer "I'll do the math later if it affects the design."
- Only do it now if you'll branch on the result. Example: "trending topics — thousands fits one in-memory min-heap; millions needs sharding. So I need to estimate."
- Don't burn 5 min on QPS math if the design doesn't change.

---

## Stage 2 — Core Entities (2 min)

- Just bulleted nouns, say "first version, will iterate."
- Twitter: User, Tweet, Follow.
- Why this stage matters:
  1. Vocabulary alignment with interviewer
  2. Becomes API resources + DB tables
  3. Foundation for the next stages
- Identification questions: *Who are the actors? What nouns are needed for the functional requirements? Are there overlaps?*
- Names matter — interviewers may probe naming as a signal.

---

## Stage 3 — API Design (5 min)

### Pick the protocol
- **REST = default.** Only deviate with a reason.
- **GraphQL** — heterogeneous clients needing different data shapes
- **gRPC / RPC** — internal service-to-service, perf-critical
- **WebSockets / SSE** — real-time bidirectional/push (still design REST first if applicable)

### Design endpoints
- Resources = entities, **plural names** (`/tweets` not `/tweet`).
- Path params for required identifiers.
- Twitter:
  ```
  POST  /v1/tweets              { text }
  GET   /v1/tweets/{tweetId}
  POST  /v1/users/{userId}/follows
  GET   /v1/feed                → [Tweet]
  ```

### Security rule (non-negotiable)
- **Never trust user IDs from the request body.** Extract from auth token (header / JWT). The `POST /v1/tweets` endpoint doesn't take an authorId — it comes from auth.
- Always authenticate; user identity comes from token, not from user input.

### Optional: data flow sequence
- For backend / data-pipeline systems with a long action chain (e.g. web crawler: fetch → parse → extract links → store → repeat), bullet the sequence. It informs Stage 4. Skip if the system isn't sequential.

---

## Stage 4 — High-Level Design (10-15 min) — *the core*

This is where you build the architecture. Four steps:

1. **Draw boxes and arrows.** Boxes = components (servers, DBs, caches, queues). Arrows = interactions.
2. **Walk each API endpoint.** For each, narrate how data flows through the system.
3. **Explain thinking out loud.** What state changes where? What's the data path?
4. **Annotate schema next to the DB visually.** Add fields *as endpoints touch the persistence layer*.

### Schema rules — what to include / exclude
| Include | Exclude |
|---------|---------|
| Fields *relevant to the design* | Obvious fields (`name`, `email`) |
| Entity relationships (FKs) | Data types (interviewer infers them) |
| Key indexes (esp. composite, partial) | Cosmetic columns |

### Three discipline rules for HLD
1. **Start with the simplest design that satisfies functional requirements.** No premature complexity.
2. **Don't sidetrack on caches / queues.** Note "would add a cache here" and *return* to it in Deep Dive. Caches mid-HLD = dead-end rabbit hole.
3. **Narrate the thinking.** Don't draw silently. The interviewer is grading the *reasoning*, not the diagram.

### Twitter HLD walk-through (illustration)
- `POST /tweets` → LB → web server → DB (insert into `tweets`).
- `GET /tweets/{id}` → LB → web server → DB read.
- `POST /follows` → LB → web server → DB (insert into `follows`).
- `GET /feed` → LB → web server → DB query: get following list, then get tweets from those users. *"This will be inefficient at scale — flag for Deep Dive."*

---

## Stage 5 — Deep Dive (10 min)

Last 10 min is where you optimize and harden. **Drive it by walking your non-functional requirements.**

### What to do
1. **Address every non-functional requirement.** You wrote them in Stage 1; now deliver. "We said <200ms feed load — that's why we're adding a precomputed feed cache."
2. **Handle edge cases.** What if user deletes a tweet? What if two users follow each other concurrently?
3. **Identify bottlenecks and single points of failure.** Where's the hot path? What fails first under load?
4. **Improve based on interviewer probes.** Listen for hints — they're often steering you.

### Twitter Deep Dive (illustration)
- Scalability (>100M DAU): horizontal scaling, sharding strategy, cache layer.
- Low feed latency (<200ms): **fanout-on-write vs fanout-on-read** discussion, precomputed feed cache, hybrid for celebrity accounts.

### Seniority calibration (proactivity ladder)
| Level | Behavior |
|-------|----------|
| Junior | Waits for interviewer to point at improvement areas |
| Mid-Senior | Self-identifies *some* optimization spots |
| Senior | Self-identifies *all* and leads discussion |
| Staff+ | Same as senior + makes trade-off decisions and defends them |

**You're targeting Senior / Staff+ → drive it proactively.**

### The "don't talk over the interviewer" rule
- Senior+ candidates often over-monologue. Mistake.
- Pause periodically. Let the interviewer probe. They have expectations you'll miss if you're filling all the air.
- Soft-skill grading happens here too. Talking through follow-ups *with* the interviewer > lecturing *at* them.

### Useful Deep Dive topics to surface (if not covered)
- **Metrics & monitoring** — how would you know it's working? Where are perf bottlenecks observable?
- **Fault tolerance** — replication, failover, recovery
- **Security** — data protection, access control, rate limits, auth

---

## Latency reference numbers (memorize)

| Operation | Typical latency |
|-----------|-----------------|
| Cache (in-memory / Redis) | 1-5 ms |
| Relational DB simple query | 30-50 ms |
| Web server simple request | 10-20 ms |
| **Target end-to-end p99** (no heavy compute) | < 100 ms |

If your design's hot path adds up to >100ms with no heavy compute justification, you have a problem.

---

## Anti-patterns (what tanks interviews)

1. **Long requirements list.** Looks broad; actually a commitment trap. Every requirement extends the design surface.
2. **Premature estimation.** Burning 5 min on QPS math that doesn't change the design.
3. **Sidetracking into caches / queues during HLD.** Mark them, return in Deep Dive.
4. **Silent diagramming.** No narration = no signal for the interviewer.
5. **Schema bloat.** Listing every column with types wastes time without adding signal.
6. **Talking over the interviewer.** Soft-skill grade tanks; you also miss the hints they're dropping.
7. **Trusting user input for sensitive identity.** User ID from request body = security red flag.
8. **Designing for non-functional requirements you didn't actually state.** "We made it highly available" without the requirement = noise.

---

## Two-line summary
> **Follow the structure to guarantee a working system at the buzzer. Top 3 requirements, simple first, complexity in Deep Dive, balance proactivity with collaboration.**

That's the whole thing. Everything else is execution detail.
