package prep.concurrency;

import java.util.ArrayDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/*
 * LC 1188 — Bounded Blocking Queue.
 *
 * Pattern: ReentrantLock + two Conditions (one per wait-reason).
 *   - Producers wait on `notFull`, signal `notEmpty` after enqueue.
 *   - Consumers wait on `notEmpty`, signal `notFull` after dequeue.
 *
 * Invariants:
 *   - Wait in a WHILE loop, not an if. Spurious wakeups are legal, and even
 *     without them, another thread may have consumed the slot you were woken for.
 *   - Always re-check the predicate (`q.size() == capacity` / `q.isEmpty()`).
 *   - Release the lock in `finally` — an interrupt or RuntimeException inside
 *     the critical section must not strand the lock.
 *
 * Why two conditions, not one:
 *   A single condition forces `signalAll()` to wake everyone (producers +
 *   consumers) on every state change — O(n) wakeups, most of which go back
 *   to sleep. Two conditions let you `signal()` exactly one thread of the
 *   right kind. (This is the textbook motivation for `Condition` over
 *   `Object.wait/notifyAll`.)
 *
 * Transfers to: token bucket (semaphore/condition on "tokens > 0"),
 *   bounded thread pool's work queue, back-pressured streams.
 *
 * Canonical form (what `ArrayBlockingQueue` does):
 *   - Condition names: `notFull` (producers wait here), `notEmpty` (consumers).
 *   - FIFO pairing: `offerLast` + `pollFirst`, or just `add` / `poll` via the
 *     Queue interface. `offerFirst` + `pollLast` is also FIFO but inverted.
 *   - Fair lock (`new ReentrantLock(true)`) buys strict FIFO thread ordering
 *     at ~10× throughput cost in contention. Default is non-fair.
 */
class BoundedBlockingQueue {

    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final Condition waitingBecauseEmpty = reentrantLock.newCondition();
    private final Condition waitingBecauseOverloaded = reentrantLock.newCondition();

    private final ArrayDeque<Integer> data;
    private final int capacity;


    public BoundedBlockingQueue(int capacity) {
        data = new ArrayDeque<>();
        this.capacity = capacity;

    }

    public void enqueue(int element) throws InterruptedException {
        reentrantLock.lock();
        try {
            while (data.size() >= capacity) {
                waitingBecauseOverloaded.await();
            }
            data.offerFirst(element);
            waitingBecauseEmpty.signal();
        }finally {
            reentrantLock.unlock();
        }

    }

    public int dequeue() throws InterruptedException {
        reentrantLock.lock();
        try {
            while (data.isEmpty()) waitingBecauseEmpty.await();
            Integer i = data.pollLast();
            waitingBecauseOverloaded.signal();
            return i;
        }finally {
            reentrantLock.unlock();
        }
    }

    public int size() {
        reentrantLock.lock();
        try {
            return data.size();
        }finally{
            reentrantLock.unlock();
        }
    }
}
