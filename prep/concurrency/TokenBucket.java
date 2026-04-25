        package prep.concurrency;

        import java.util.concurrent.locks.Condition;
        import java.util.concurrent.locks.ReentrantLock;

        class TokenBucket {

            long capacity;
            long refillTokensPerSec;
            float currentValue;
            long lastNano;
            ReentrantLock lock = new ReentrantLock();
            Condition condition;

            public TokenBucket(long capacity, long refillTokensPerSec) {
                this.capacity = capacity;
                this.currentValue = capacity;
                this.refillTokensPerSec = refillTokensPerSec;
                this.lastNano = System.nanoTime();
                this.condition = lock.newCondition();
            }

            public boolean tryAcquire(long n) {
                lock.lock();
                try {
                    float secs = getSecsBetween(System.nanoTime(), this.lastNano);
                    this.currentValue = Math.min(this.capacity, this.currentValue + this.refillTokensPerSec * secs);
                    this.lastNano = System.nanoTime();
                    if (this.currentValue >= n) {
                        this.currentValue -= n;
                        this.lastNano = System.nanoTime();
                        return true;
                    }
                    return false;
                } finally {
                    lock.unlock();
                }
            }

            public void acquire(long n) throws InterruptedException {
                lock.lock();
                try {
                    while (true) {
                        float secs = getSecsBetween(System.nanoTime(), this.lastNano);
                        this.currentValue = Math.min(this.capacity, this.currentValue + this.refillTokensPerSec * secs);
                        this.lastNano = System.nanoTime();
                        if (this.currentValue<n){
                            condition.awaitNanos(toNanos(n-this.currentValue)/this.refillTokensPerSec);
                        } else {
                            this.currentValue -= n;
                            this.lastNano = System.nanoTime();
                            return;
                        }
                    }

                } finally {
                    lock.unlock();
                }
            }

            private long toNanos(float l) {
                return (long)(l * 1_000_000_000L);
            }

            private float getSecsBetween(long l, long lastNano) {
                return (float)((l - lastNano)) / 1_000_000_000f;
            }
        }
