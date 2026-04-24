package prep.concurrency;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/*
 * Parallel Task Runner with dependencies — Databricks/ETL classic.
 *
 * Input: a DAG of tasks (id, deps, work). Goal: execute in parallel on a
 * bounded pool, respecting deps, maximizing parallelism.
 *
 * Two canonical approaches — pick one, know both:
 *
 *   A) Kahn + counter + ExecutorService (explicit scheduling):
 *      - Per-task remaining-dep counter (AtomicInteger), plus a reverse
 *        adjacency list (children per task).
 *      - On addTask, if deps == 0, submit immediately.
 *      - Each submitted task runs its Runnable, then for every child
 *        decrements its counter; if it hits 0, submit that child.
 *      - Track outstanding tasks with a CountDownLatch initialized to
 *        the total task count (or a Phaser). runToCompletion awaits it.
 *      - Cycle detection: after registration, if any task has deps > 0
 *        but is unreachable from the zero-dep frontier, it's a cycle
 *        (equivalently: if total submitted < total registered when the
 *        frontier drains).
 *      - Good when: you want explicit control, easy fail-fast, or need
 *        to log scheduling decisions.
 *
 *   B) CompletableFuture composition (declarative):
 *      - Build tasks in topo order. For each task t:
 *          depFutures = [futures[d] for d in t.deps]
 *          futures[t.id] = CompletableFuture.allOf(depFutures)
 *                                           .thenRunAsync(t.work, pool);
 *      - runToCompletion = CompletableFuture.allOf(all).join();
 *      - Fail-fast is automatic: if any dep completes exceptionally,
 *        downstream CFs complete exceptionally without running.
 *      - Cycle detection: topo sort must be done up front — if it can't
 *        order all tasks, cycle. Without it you'd deadlock.
 *      - Good when: you want minimal code, automatic fan-in/fan-out,
 *        and exceptional-completion propagation for free.
 *
 * Invariants (both approaches):
 *   - Deps must be registered (or known) before the dependent runs — either
 *     resolve forward-references after addTask calls close, or require deps
 *     to be added first.
 *   - Pool sizing: `poolSize` threads cap parallelism; the algorithm itself
 *     doesn't assume anything about pool size.
 *   - Shutdown: the pool is owned by this runner; shut it down in a finally
 *     so runToCompletion never strands threads.
 *
 * Transfers to: Spreadsheet recompute (this exact shape + cycle reject +
 *   value propagation), Make/Bazel build graphs, airflow-style DAG runners.
 *
 * ---
 * Wave-barrier vs. event-driven (refinement of approach A):
 *
 *   The implementation below is wave-barrier: drain all zero-count tasks,
 *   block on a CountDownLatch for the whole wave, advance. Simple, but loses
 *   parallelism when a wave has uneven task durations — a short chain behind
 *   a fast task must idle until the wave's slowest task finishes.
 *
 *   Canonical event-driven form (no per-wave barrier):
 *
 *     - Global `CountDownLatch done = new CountDownLatch(totalTasks);`
 *       (or a Phaser if task count is unknown up front).
 *     - Per-task `AtomicInteger remaining = new AtomicInteger(deps.size());`
 *     - submit(t): pool.execute(() -> {
 *           try { t.work.run(); }
 *           finally {
 *               for (child of t.children) {
 *                   if (remaining[child].decrementAndGet() == 0) submit(child);
 *               }
 *               done.countDown();
 *           }
 *       });
 *     - runToCompletion: submit every task with remaining == 0, then done.await().
 *     - Failure: on exception, could either still propagate (children run) or
 *       cancel downstream (skip-children); policy choice.
 *     - Cycle: if after runToCompletion fewer than totalTasks were ever
 *       submitted, the un-submitted ones form a cycle — detect via a counter.
 */
class TaskRunner {

    Map<String, Set<String>> tasks = new HashMap<>();
    Map<String, Runnable> tasksWork = new HashMap<>();

    private final ExecutorService pool;

    public TaskRunner(int poolSize) {
        this.pool = Executors.newFixedThreadPool(poolSize);
    }

    public void addTask(String id, Set<String> deps, Runnable work) {
        tasks.put(id,deps);
        tasksWork.put(id, work);
    }

    public void runToCompletion() throws InterruptedException {

        try {
            Map<String, Integer> dependencyCount = new HashMap<>();
            Map<String, Set<String>> inverted = new HashMap<>();
            for (var kv : tasks.entrySet()) {
                dependencyCount.put(kv.getKey(), kv.getValue().size());
            }

            for (var kv : tasks.entrySet()) {
                inverted.putIfAbsent(kv.getKey(), new HashSet<>());
                for (var v : kv.getValue()) {
                    inverted.putIfAbsent(v, new HashSet<>());
                    inverted.get(v).add(kv.getKey());
                }
            }

            while (!dependencyCount.isEmpty()) {
                int waveSize = (int) dependencyCount.entrySet().stream().filter(a -> a.getValue() == 0).count();
                // Cycle: non-empty map but nothing can start → un-submittable tasks form a cycle.
                if (waveSize == 0) {
                    throw new IllegalStateException("Cycle detected among tasks: " + dependencyCount.keySet());
                }
                CountDownLatch cdl = new CountDownLatch(waveSize);
                Set<String> toRemove = ConcurrentHashMap.newKeySet();
                for (var id : dependencyCount.keySet()) {
                    if (dependencyCount.get(id) == 0) {
                        pool.execute(() -> {
                            try {
                                tasksWork.get(id).run();
                            } finally {
                                // In finally so a throwing task doesn't strand the latch.
                                toRemove.add(id);
                                cdl.countDown();
                            }
                        });
                        inverted.get(id).forEach(d -> dependencyCount.put(d, dependencyCount.get(d) - 1));
                    }
                }
                cdl.await();
                toRemove.forEach(dependencyCount::remove);
            }
        } finally {
            pool.shutdown();
        }
    }

}
