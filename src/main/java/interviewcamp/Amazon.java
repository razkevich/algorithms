package interviewcamp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.ListUtils;

public class Amazon {

	public static void main(String[] args) {
		System.out.println(getMinScore(6, List.of(1, 2, 2, 3, 4, 5), List.of(2, 4, 5, 5, 5, 6)));
	}

	public static int getMinScore(int productNodes, List<Integer> productsFrom, List<Integer> productsTo) {
		Map<Integer, List<Integer>> neighboursMap = buildNeighboursMap(productsFrom, productsTo);

		return getTriangles(List.of(neighboursMap.keySet().iterator().next()), new HashMap<>(), neighboursMap).stream()
				.peek(triangle -> triangle.sort(Comparator.comparingInt(b -> b)))
				.mapToInt(triangle -> countTriangleScore(triangle, neighboursMap))
				.min()
				.orElse(-1);
	}

	private static Map<Integer, List<Integer>> buildNeighboursMap(List<Integer> productsFrom, List<Integer> productsTo) {
		Map<Integer, List<Integer>> neighboursMap = new HashMap<>();
		for (int i = 0; i < productsFrom.size(); i++) {
			neighboursMap.merge(productsFrom.get(i), List.of(productsTo.get(i)), ListUtils::union);
			neighboursMap.merge(productsTo.get(i), List.of(productsFrom.get(i)), ListUtils::union);
		}
		return neighboursMap;
	}

	public static List<List<Integer>> getTriangles(List<Integer> path, Map<Integer, State> states, Map<Integer, List<Integer>> neighboursMap) {
		Integer value = path.get(path.size() - 1);
		if (states.get(value) == State.VISITING) {
			return path.size() - path.indexOf(value) == 4 ? List.of(path.subList(path.indexOf(value) + 1, path.size())) : List.of();
		}
		List<List<Integer>> triplets = new ArrayList<>();
		states.put(value, State.VISITING);
		for (Integer neighbor : neighboursMap.get(value)) {
			triplets.addAll(getTriangles(ListUtils.union(path, List.of(neighbor)), states, neighboursMap));
		}
		states.put(value, State.VISITED);
		return triplets;
	}

	private static int countTriangleScore(List<Integer> triangle, Map<Integer, List<Integer>> neighboursMap) {
		int score = 0;
		for (int point : triangle) {
			for (int nei : neighboursMap.get(point)) {
				if (!triangle.contains(nei)) {
					score++;
				}
			}
		}
		return score;
	}

	public enum State {VISITING, VISITED;}
}
