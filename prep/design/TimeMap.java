package prep.design;

import java.util.*;

/*
 * LC 981 — Time-based Key-Value Store.
 *
 * What it is: a key-value store that *remembers every write*, indexed by
 * timestamp. A regular Map<K,V> overwrites — past values are lost. A
 * time-based KV store keeps the full write history per key, so reads can
 * time-travel: "what was this key's value at time T?"
 *
 * Mental model: instead of `Map<K, V>`, you have `Map<K, SortedMap<Time, V>>`.
 * Each set() appends a new (timestamp, value) entry to the per-key history;
 * each get() asks "give me the value that was current at time T" — which
 * is the most recent write at or before T. Past writes are never overwritten,
 * just shadowed by later ones.
 *
 * Why it exists in real systems: this is the underlying shape of MVCC
 * (multi-version concurrency control) in databases, snapshot isolation,
 * version control (git's "what did this file look like at commit X?"),
 * time-series databases, audit logs, event-sourced systems, and "show me
 * the config that was active during the incident" tooling. Once you see
 * the pattern (per-key sorted history + binary-search-on-time), you
 * recognize it across all of them.
 *
 * API:
 *
 *   set(key, value, timestamp)
 *     Store `value` at `key` at time `timestamp`. Timestamps for the same
 *     key are strictly increasing across calls (guaranteed by the problem),
 *     so the per-key history is naturally append-only.
 *
 *   get(key, timestamp) → String
 *     Return the value associated with `key` at the *latest* timestamp_prev
 *     such that timestamp_prev <= timestamp. If no such write exists,
 *     return "" (empty string). This is a "floor lookup" on the timestamp
 *     axis — find the largest key ≤ T in the per-key sorted map.
 *
 * Examples:
 *   set("foo", "bar", 1);
 *   get("foo", 1)  == "bar"        // exact-match timestamp
 *   get("foo", 3)  == "bar"        // looks back to t=1
 *   set("foo", "bar2", 4);
 *   get("foo", 4)  == "bar2"       // exact-match wins
 *   get("foo", 5)  == "bar2"       // still bar2 — most recent <= 5
 *   get("foo", 0)  == ""           // no write at or before t=0
 */
class TimeMap {

    Map<String, TreeMap<Integer, String>> data = new HashMap<>();

    public void set(String key, String value, int timestamp) {
        data.computeIfAbsent(key, k -> new TreeMap<>()).put(timestamp, value);
    }

    public String get(String key, int timestamp) {
        TreeMap<Integer, String> history = data.get(key);
        if (history == null) return "";
        Map.Entry<Integer, String> e = history.floorEntry(timestamp);
        return e == null ? "" : e.getValue();
    }
}
