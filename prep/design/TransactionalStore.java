package prep.design;

/*
 * Transactional in-memory key-value store.
 *
 *   set(key, value)   — store the value.
 *   get(key)          — return the value, or null if not set / deleted.
 *   delete(key)       — remove the key.
 *   begin()           — open a new transaction. Subsequent writes are scoped to
 *                       this transaction until commit/rollback.
 *   commit()          — merge the current transaction's writes into the parent
 *                       transaction (or the base store if no parent).
 *   rollback()        — discard the current transaction's writes.
 *
 * Transactions nest: begin() inside an open transaction opens a child.
 * commit/rollback always apply to the innermost open transaction.
 *
 * Reads see the union of: base store + all writes from open transactions
 * (innermost transaction wins on conflict, including for deletions).
 *
 * Examples:
 *   set("a", "1"); get("a") == "1"
 *   begin(); set("a", "2"); get("a") == "2"; rollback(); get("a") == "1"
 *   begin(); delete("a"); get("a") == null; commit(); get("a") == null
 *   begin(); set("a", "2"); begin(); set("a", "3"); rollback(); get("a") == "2"
 */
class TransactionalStore {

    public TransactionalStore() {
    }

    public void set(String key, String value) {
    }

    public String get(String key) {
        return null;
    }

    public void delete(String key) {
    }

    public void begin() {
    }

    public void commit() {
    }

    public void rollback() {
    }
}
