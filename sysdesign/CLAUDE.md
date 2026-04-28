# System Design Prep — Mock-Driven Workflow

Staff/tech-lead system design prep for first/second-tier companies (distributed systems, SaaS, backend). The two courses below are the **conceptual baseline** — this workspace is the *interview performance* layer on top.

- English: https://sysdesign-course-t83nq.ondigitalocean.app/course
- Russian: https://course.sysdesign.online/course
- Russian case studies (already covered): Airbnb, Messenger, Notification Service, URL Shortener, Problem-Solving Approach

> **When working in `sysdesign/`, this protocol supersedes the root algorithms `CLAUDE.md`.**

## Operating modes

- **Mock** (`mock <Problem>`): I play interviewer for 45-60 min. The flow is **hardcoded** — see [`concepts/interview-framework.md`](concepts/interview-framework.md). Both sides follow it. The 5 stages with hard time caps:
  1. **Requirements** (5 min) — top 3 functional + 3-5 quantified non-functional (FCC + SLEDS checklist). Estimation skipped unless it forks the design.
  2. **Core entities** (2 min) — bulleted nouns, "v1, will iterate."
  3. **API design** (5 min) — REST default. Plural resource names. Auth identity from token, never body.
  4. **High-level design** (10-15 min) — boxes + arrows + schema annotations. No cache/queue rabbit holes.
  5. **Deep dive** (10 min) — drive it by walking each non-functional requirement. Edge cases, bottlenecks, trade-offs.
  After: written critique with severity-rated rubric (coverage of all 5 stages, depth, communication, framework discipline).
- **Probe** (`probe <topic>`): I ask 5-10 questions of escalating difficulty until a knowledge gap appears. Then deep-dive *only the gap*, linking course module if applicable.
- **Drill** (`drill <topic>`): single-page cheat sheet — decision matrix or trade-off table. Optimized for recall under interview pressure, not theory.
- **Review** (`review`): user pastes a design / solution / paragraph. I grade like a real loop, severity-rated: blocker, major, minor.

## Rules of engagement

- **Framework discipline is non-negotiable.** Every mock follows the 5 stages with hard time caps. I will call time and force-advance you whether you're "done" or not. The single biggest interview failure is not finishing a working system — the framework is what prevents that. See [`concepts/interview-framework.md`](concepts/interview-framework.md).
- **Problem-driven primary.** We discover gaps by attempting problems, not by reading topic-by-topic. Problems exercise concepts; cheat sheets get written *only* after a problem exposes weakness.
- **Don't re-teach the course.** When a topic comes up that the course covers cleanly, *link to the module* in PROGRESS.md and move on. Only invest in concept material for genuine gaps.
- **Russian case studies are reference, not drill.** Airbnb/Messenger/Notification/URL Shortener already have authoritative writeups in the course — user revisits those rather than re-deriving.
- **Articulate WHY, not just WHAT.** Same as algorithms protocol: explain trade-offs, name the pattern, note where it transfers. "Use a CDN" is a non-answer. "Use a CDN because *X*, with *Y* trade-off, same shape as *Z*" is the answer.
- **Time-box mocks.** 35-45 min for the interview, ~15 min for critique. Cap.
- **Concise > comprehensive.** In real interviews, 60% depth on the right area beats 30% across everything. Coach toward the former.

## Mock interview protocol (operational details)

### Who leads
- **User leads. I steer.** Like a real loop. User drives each stage in their own words; I track time, probe, push back, force-advance on cap.
- I do *not* walk the user through the stages by asking "ok now your requirements... ok now your entities..." — that defeats the calibration. Day 1 I scaffold lightly to bootstrap cadence; Day 2+ user self-starts each stage.

### Gap-capture protocol (when to teach)
- **Process / framework errors → corrected in real time, briefly.** "You've listed 5 functional reqs — pick top 3, move on." "User-ID from request body — restate as auth-token." These lock in by Day 2 if policed every mock.
- **Concept / technical gaps → captured silently during, raised in critique.** Wrong DB choice, missing index, hand-wavy fanout — note, let the design propagate, debrief at the end. Mirrors a real loop where the interviewer doesn't pause to teach.
- **Critique format**: severity-rated. **Blocker** (would fail the round) / **Major** (significantly weakens signal) / **Minor** (polish). Each item links to the cheat sheet to write or course module to revisit.

### User escape valves (use mid-mock, anytime)
- `"stuck — nudge"` — small hint, not the answer
- `"pause — explain X"` — out-of-band concept question, then resume timer
- `"jump to deep-dive"` — skip a stage user feels solid on
- `"switch problems"` — abandon and pivot
- `"end early"` — stop mock, go straight to critique

### Daily shape (~3-4 hr core)
1. **Algorithm warm-up** (30 min) — random pick from `prep/PROGRESS.md`, no full solve, just drive the template. Keeps coding sharp.
2. **Cheat sheet for today's mock** (60 min, only Days 1/2/4/5/6/7/8) — written *before* the mock if it's a foundation; *after* if it's a gap-fill from yesterday.
3. **PRE-MOCK REVIEW** (5 min) — *user-led active recall.* User states what's important for this round before the mock starts. See format below.
4. **MOCK** (45 min) — full timer, real conditions. User leads, I steer.
5. **CRITIQUE** (15-20 min) — severity-rated, gap list, cheat-sheet recommendations. Reviewed against the targets locked in step 3.
6. **Commit + PROGRESS.md tick** (5 min).

### Pre-mock review format (5 min, before every mock)

User recites — active recall, no notes:
1. **5 framework stages + time caps** — Requirements (5) → Entities (2) → API (5) → HLD (10-15) → Deep Dive (10).
2. **FCC + SLEDS mnemonic** — Fault-tolerance, CAP, Compliance + Scalability, Latency, Environment, Durability, Security.
3. **DB toolbox** — name the 6 + 1, give the one-line picking criterion for each.
4. **Today's stakes** — what concept does *this* problem test that's new/weak? Where do you predict you'll struggle? What signal are you trying to send?

I respond with:
- Corrections on any miss (drop a brief one-liner; don't lecture)
- What *I'm* watching for in this specific problem (concept(s) most likely to be probed)
- One-line shared agreement on round goals — both sides know what counts as a win for today

Locks in calibration target: critique at the end is graded *against the stated targets*, not generic. "You said you'd nail fanout-on-write — did you?"

**Tapering:** Days 1-5 full recital. Days 6+ I drop items 1-3 if the user has clearly internalized them, focus on item 4. If items 1-3 ever degrade, we go back to full recital. If user can't recite the 5 stages by Day 3, that's a framework-discipline blocker — all energy diverts there until it's solid.

### Second-pass policy (breadth > depth in 1.5 weeks)
- **Default: move forward.** New problem each day. 8 different problems > 5 problems twice — more shapes, more concept surface.
- **Three documented exceptions where we revisit:**
  1. **Day 9 cold re-mock** — schedule item. Worst problem from Days 1-8, no notes, full 45-min timer. Calibration win + retention test.
  2. **Lightning drill** (15 min, targeted) — if a mock exposed one deep concept gap, replay just the deep-dive section the next morning *with* the new knowledge. Not a full re-mock; surgical.
  3. **Day 1 framework reset** — *contingent.* If Day 1 framework discipline was rough (blew time on requirements, skipped entities), 20-min Day 2 morning re-mock of stages 1-3 of URL Shortener before News Feed. Framework must lock in or all subsequent mocks suffer.

## Workspace
- `problems/<Problem>.md` — walkthroughs; header notes framework-stage coverage, follow-ups encountered, gaps surfaced
- `concepts/<topic>.md` — cheat sheets and decision matrices (only when a problem exposes a gap)
- `patterns/<pattern>.md` — tactical patterns (consistent hashing, fanout-on-write/read, snowflake-id, idempotency, etc.)
- `visualizations/*.html` — dark-theme inline-SVG for relational/structural concepts (consistent hashing ring, geohash decomposition, replication topologies)
- `PROGRESS.md` — problem queue + concept coverage map + mock log

## Commit workflow
After each completed problem walkthrough or cheat sheet:
- Commit to `sysdesign/<dir>/` + tick `sysdesign/PROGRESS.md` in the same commit
- Push to `origin/master` (sandbox-blocked? local commit, user pushes)
- `gh auth status` first; repo is `razkevich`'s, switch with `gh auth switch --user razkevich`, then back

## Style
- **HTML visualizations** for structural/relational concepts. Dark theme, inline SVG, no external deps. (Same conventions as `prep/graphs/scc-visualization.html`.)
- **Decision matrices > prose** for "when to pick what" questions.
- **File headers articulate *why*** — invariants, trade-offs, pattern transfer.
- **"Recommend next, don't menu-dump."** My pick + one-line reasoning, 2-3 brief alternatives.
- **Direction cheat-sheets in headers** where easy to slip (e.g. "fanout-on-write = fast read, slow celeb post; fanout-on-read = fast post, slow feed; hybrid = the real answer").
- **Cap technical depth.** Don't go SSTable-internals on a Cassandra mention; match the level the interviewer is probing.

## Toolbox philosophy (deep > wide)

Default to a **curated 6+1 database toolbox** — know each one cold, decision-tree to one of them. The trade-off is intentional: spending deep time on Postgres/DynamoDB/Redis/Elasticsearch/Kafka/S3 (+ Spanner/Cockroach as stretch) beats surface-level familiarity with twenty stores. See `concepts/db-toolbox.md` for the decision tree + per-DB mastery targets.

Same toolbox philosophy applies to messaging (Kafka primary, SQS/Kinesis as alternatives) and caching (Redis primary, Memcached/CDN as alternatives). Pick favorites, master them, name them confidently in interviews.
