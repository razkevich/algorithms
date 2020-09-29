package interviewcamp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Dependencies {
	enum Status {
		VISITED, VISITING
	}

	Map<Integer, Status> statuses = new HashMap<>();

	public static void main(String[] args) {
		Map<Integer, List<Integer>> deps = new HashMap<>();
		deps.put(1, List.of(11, 12, 13));
		deps.put(2, List.of(14, 15, 16));
		deps.put(3, List.of(17, 18, 19));
		deps.put(19, List.of(10, 21, 22));
		deps.put(22, List.of(19));
		visit(deps, new HashMap<>());
	}

	private static void visit(Map<Integer, List<Integer>> deps, Map<Integer, Status> statuses) {
		for (int d : deps.keySet()) {
			if (statuses.get(d) == null) {
				visit(deps, statuses, d);
			}
		}
	}

	private static void visit(Map<Integer, List<Integer>> deps, Map<Integer, Status> statuses, int parent) {
		statuses.put(parent, Status.VISITING);
		if (deps.containsKey(parent)) {
			for (int nei : deps.get(parent)) {
				if (statuses.get(nei) == Status.VISITING) {
					System.out.println("cycle detected");
					throw new RuntimeException("cycle detected");
				}
				if (statuses.get(nei) == null) {
					visit(deps, statuses, nei);
				}
			}
		}

		System.out.println(parent);
		statuses.put(parent, Status.VISITED);
	}


}
