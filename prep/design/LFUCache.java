package prep.design;

import java.util.*;

public class LFUCache {

    Map<Integer, Node> data = new HashMap<>();
    Map<Integer, LinkedHashSet<Integer>> freqs = new HashMap<>();
    int minFreq =1;
    int cap;


    public LFUCache(int capacity) {
        this.cap=capacity;
    }

    public int get(int key) {
        Node node = getNode(key);
        return node==null?-1:node.getValue();
    }

    private Node getNode(int key) {
        if (!data.containsKey(key)){
            return null;
        }
        Node node = data.get(key);
        int newFreq = node.getFreq() + 1;
        node.setFreq(newFreq);
        freqs.putIfAbsent(newFreq, new LinkedHashSet<>());
        freqs.get(newFreq).add(node.key);
        freqs.computeIfPresent(newFreq-1,(k,v)->{v.remove(node.key);return v;});
        if (newFreq - 1 == minFreq &&freqs.get(newFreq-1).isEmpty()){
//            freqs.remove(newFreq-1);
//            minFreq = freqs.keySet().stream().mapToInt(a->a).min().orElse(1);
            minFreq++;
        }
        return node;
    }
    public void put(int key, int value) {
        var node = getNode(key);
        if (node==null){
            if (cap == 0) return;
            prune();
            minFreq=1;
            data.put(key, new Node(key, value, 1));
            freqs.putIfAbsent(1, new LinkedHashSet<>());
            freqs.get(1).add(key);
        } else {
            node.setValue(value);
        }
    }

    private void prune() {
        if (data.size()>=cap){
            LinkedHashSet<Integer> value = freqs.get(minFreq);

            Integer toRemove = value.iterator().next();
            value.remove(toRemove);
            data.remove(toRemove);
        }
    }


    static class Node{
        int key;
        int value;
        int freq;

        public Node(int key, int value, int freq) {
            this.key = key;
            this.value = value;
            this.freq = freq;
        }

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public int getFreq() {
            return freq;
        }

        public void setFreq(int freq) {
            this.freq = freq;
        }
    }
}
