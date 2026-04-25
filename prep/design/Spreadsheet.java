package prep.design;

import java.util.*;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

class Spreadsheet {


    Map<String, Cell> cells= new HashMap<>();
    Map<String, Set<String>> deps = new HashMap<>();

    public Spreadsheet() {
    }

    public void set(String cell, long value) {
        if (cells.containsKey(cell)){
            deps.put(cell, new HashSet<>());
            cells.get(cell).formula = null;
            cells.get(cell).value = value;
            cells.get(cell).type = "value";
        } else {
            deps.put(cell, new HashSet<>());
            cells.put(cell, new Cell(cell, value, "value"));
        }
    }

    public void setFormula(String cell, Set<String> deps, ToLongFunction<Map<String, Long>> formula) {
        Set<String> oldDeps = this.deps.get(cell);
        this.deps.put(cell, deps);
        if (hasCycle(cell, this.deps, new HashSet<>())){
            this.deps.put(cell,oldDeps);
            throw new IllegalArgumentException("cycle");
        }
        if (cells.containsKey(cell)){
            cells.get(cell).formula = formula;
            cells.get(cell).value = null;
            cells.get(cell).type="formula";
        } else {
            cells.put(cell, new Cell(cell, null,formula,"formula"));
        }
    }

    private boolean hasCycle(String cur, Map<String, Set<String>> deps, Set<String> visiting) {
        if (visiting.contains(cur)){
            return true;
        }
        Set<String> newVisiting = new HashSet<>(visiting);
        Set<String> children = deps.getOrDefault(cur, Set.of());
        newVisiting.add(cur);
        for (var dep:children){
            if (hasCycle(dep, deps, newVisiting)) return true;
        }
        return false;
    }

    public long get(String cell) {
        if (!cells.containsKey(cell)||cell==null) {
            throw new IllegalArgumentException("not found");
        }
        Map<String, Integer> indegree = new HashMap<>();
        Map<String, Set<String>> invertedDeps = new HashMap<>();


        for (var entry: deps.entrySet()){
                indegree.put(entry.getKey(), entry.getValue()==null?0:entry.getValue().size());
        }

        for (var entry: deps.entrySet()){
            if (!invertedDeps.containsKey(entry.getKey())){
                invertedDeps.put(entry.getKey(), new HashSet<>());
            }
            for (var val:entry.getValue()){
                if (invertedDeps.containsKey(val)){
                    invertedDeps.get(val).add(entry.getKey());
                } else {
                    invertedDeps.put(val, new HashSet<>());
                    invertedDeps.get(val).add(entry.getKey());
                }
            }
        }
        while(indegree.entrySet().stream().anyMatch(a->a.getValue()==0)){

            List<String> zeroKeys = indegree.entrySet().stream().filter(a -> a.getValue() == 0).map(Map.Entry::getKey).collect(Collectors.toList());
            for (var e: zeroKeys){

                if (cells.get(e).type.equals("formula")){
                    cells.get(e).calculate();
                }
                if (cell.equals(e)){
                    return cells.get(e).value;
                }
                zeroKeys.forEach(indegree::remove);

                invertedDeps.get(e).forEach(a->indegree.compute(a,(k,v)->v-1));

            }

        }


        throw new IllegalArgumentException("not found");
    }


    class Cell{
        String id;
        Long value;
        ToLongFunction<Map<String, Long>> formula;
        String type;

        public Cell(String id, Long value, ToLongFunction<Map<String, Long>> formula, String type) {
            this.id=id;
            this.value = value;
            this.formula = formula;
            this.type=type;
        }

        public Cell(String id, Long value, String type) {
            this.id=id;
            this.value = value;
            this.type=type;
        }

        public void calculate() {
            Map<String, Long> depVals = new HashMap<>();
            for (String dep : deps.get(this.id)) {
                depVals.put(dep, cells.get(dep).value);  // already computed because topo order
            }
            this.value = formula.applyAsLong(depVals);
        }
    }
}
