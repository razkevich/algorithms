package prep.concurrency;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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

            Set<String> visited = new ConcurrentSkipListSet<>();
            while (!dependencyCount.isEmpty()) {
                List<String> ready = dependencyCount.entrySet().stream()
                        .filter(a -> a.getValue() == 0)
                        .map(Map.Entry::getKey).collect(Collectors.toList());
                if (ready.isEmpty()) {
                    throw new IllegalStateException("Cycle detected among tasks: " + dependencyCount.keySet());
                }
                CountDownLatch cdl = new CountDownLatch(ready.size());
                Set<String > toRemove = new ConcurrentSkipListSet<>();
                for (var indegree : ready) {
                    if (visited.contains(indegree)) continue;
                    pool.execute(() -> {
                        try {
                            tasksWork.get(indegree).run();
                        }finally{
                        toRemove.add(indegree);
                        visited.add(indegree);
                        cdl.countDown();}
                    });
                    Set<String> dependants = inverted.get(indegree);
                    dependants.forEach(d -> dependencyCount.put(d, dependencyCount.get(d) - 1));
                }
                cdl.await();
                toRemove.forEach(dependencyCount::remove);
            }
        } finally {
            pool.shutdown();
        }
    }

}
