package prep.graphs;

import java.util.*;
import java.util.stream.Collectors;

// LC 269 — Alien Dictionary. Kahn's topological sort over edges derived from
// adjacent-word comparison. Two non-obvious correctness points:
//   1. All chars must be vertices, even ones that never appear in a derived
//      edge (otherwise isolated letters silently vanish from the output).
//   2. Cycle = no zero-indegree node remains while indegrees still non-empty.
//      Don't need a separate DFS pre-check; Kahn detects it naturally.
// Edge case: word1 is a strict prefix of word2 going backward (e.g. "abc"
// before "ab") is invalid input → "".
class AlienDictionary {

    public String alienOrder(String[] words) {
        List<String> result = new ArrayList<>();
        try {
            List<List<Character>> rels = new ArrayList<>();
            for (int i = 0; i < words.length - 1; i++) {
                rels.add(deriveRel(words[i], words[i + 1]));
            }
            Map<String, List<String>> adjacencyList = getAdjList(rels, words);
            Map<String, Integer> indegrees = getIndegree(adjacencyList);
            Map<String, Queue<String>> invertedRels = getInverted(rels, words);
            while (!indegrees.isEmpty()) {
                var zeroIndegreesIndexes = indegrees.entrySet().stream().filter(a -> a.getValue() == 0).map(Map.Entry::getKey).collect(Collectors.toList());

                if (zeroIndegreesIndexes.isEmpty()) {
                    return ""; // cycle: nothing left to peel
                }

                for (var i : zeroIndegreesIndexes) {
                    result.add(i);
                    indegrees.remove(i);

                    for (var j : invertedRels.get(i)) {
                        indegrees.put(j, indegrees.get(j) - 1);
                    }
                }
            }
        }catch (IllegalArgumentException e){
            return "";
        }
        return String.join("",result);
    }

    private Map<String, Integer> getIndegree(Map<String, List<String>> adjacencyList) {
        Map<String, Integer> indegrees = new HashMap<>();
        for (String node : adjacencyList.keySet()) {
            indegrees.putIfAbsent(node, 0);
        }
        for (var entry : adjacencyList.entrySet()) {
            for (String neighbor : entry.getValue()) {
                indegrees.merge(neighbor, 1, Integer::sum);
            }
        }
        return indegrees;
    }

    private Map<String, Queue<String>> getInverted(List<List<Character>> rels, String[] words) {
        Map<String, Queue<String>> result = new HashMap<>();
        for (String w : words) {
            for (char c : w.toCharArray()) {
                result.putIfAbsent(String.valueOf(c), new ArrayDeque<>());
            }
        }
        Set<String> seen = new HashSet<>();
        for (List<Character> rel : rels) {
            if (rel.size() == 2) {
                String from = String.valueOf(rel.get(0));
                String to = String.valueOf(rel.get(1));
                if (seen.add(from + "->" + to)) {
                    result.get(from).offer(to);
                }
            }
        }
        return result;
    }

    private Map<String, List<String>> getAdjList(List<List<Character>> rels, String[] words) {
        Map<String, List<String>> result = new HashMap<>();
        for (String w : words) {
            for (char c : w.toCharArray()) {
                result.putIfAbsent(String.valueOf(c), new ArrayList<>());
            }
        }
        Set<String> seen = new HashSet<>();
        for (List<Character> rel : rels) {
            if (rel.size() == 2) {
                String from = String.valueOf(rel.get(0));
                String to = String.valueOf(rel.get(1));
                if (seen.add(from + "->" + to)) {
                    result.get(from).add(to);
                }
            }
        }
        return result;
    }


    List<Character> deriveRel(String word1, String word2){
        for (int i=0;i<Math.max(word1.length(), word2.length());i++){
            Character c1 = getAChar(word1, i);
            Character c2 = getAChar(word2, i);
            if (c1!=null&&c2==null){
                throw new IllegalArgumentException("illegal");
            }
            if (!Objects.equals(c1, c2) && c1!=null && c2!=null){
               return List.of(c1,c2);
            }
        }
        return List.of();
    }

    private static Character getAChar(String w, int i) {
        if (w.length()<=i){
            return null;
        }
        return w.charAt(i);
    }
}
