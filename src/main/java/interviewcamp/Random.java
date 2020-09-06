package interviewcamp;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class Random {

	public static void main(String[] args) {
		System.out.println(toStr("234234"));
		List<List<Integer>> lists = threeSumToZero(new int[]{-1, 0, 1, 2, -1, -4}, new ArrayList<>());
		System.out.println(lists);
		int[] a = {2, 1, 3};
		nextPermutation(a);
		System.out.println(Arrays.toString(a));
		System.out.println(Arrays.toString(twoSum(new int[]{3, 2, 4}, 6)));
		System.out.println(longestWithoutRepeating("abba"));
		System.out.println("=======");
		System.out.println(countIslands(new char[][]{
				new char[]{'1', '1', '0', '0', '0'},
				new char[]{'1', '1', '0', '0', '0'},
				new char[]{'0', '0', '1', '0', '0'},
				new char[]{'0', '0', '0', '1', '1'}}));
		System.out.println(countIslands(new char[][]{
				new char[]{'1', '1', '1', '1', '0'},
				new char[]{'1', '1', '0', '1', '0'},
				new char[]{'1', '1', '0', '0', '0'},
				new char[]{'0', '0', '0', '0', '0'}}));

		System.out.println(trap(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}));
		System.out.println(threeSum(new int[]{-4, -2, -2, -2, 0, 1, 2, 2, 2, 3, 3, 4, 4, 6, 6}));
		ListNode l1 = new ListNode(1, new ListNode(4, new ListNode(5, null)));
		ListNode l2 = new ListNode(1, new ListNode(3, new ListNode(4, null)));
		ListNode l3 = new ListNode(2, new ListNode(6, null));
		mergeTwoLists(l1, l2);

		mergeKLists(new ListNode[]{l1, l2, l3});
		System.out.println(validate("()[]{}"));
		System.out.println(Arrays.deepToString(merge(new int[][]{new int[]{1, 4}, new int[]{4, 5}})));
		System.out.println(Arrays.deepToString(merge(new int[][]{new int[]{1, 4}, new int[]{4, 5}})));
		System.out.println(partitionLabels("vhaagbqkaq"));
		System.out.println(maxProfit(new int[]{7, 1, 5, 3, 6, 4}));
		System.out.println(subarraySum(new int[]{1, 1, 1}, 2));
		System.out.println(numDecodings2("226"));
		System.out.println(new Random().criticalConnections(4, List.of(List.of(0, 1), List.of(1, 2), List.of(2, 0), List.of(1, 3))));
		System.out.println(Arrays.toString(new Random().productExceptSelf(new int[]{1, 2, 3, 4})));
		System.out.println((new Random().topKFrequent(
				new String[]{"the", "day", "is", "sunny", "the", "the", "the", "sunny", "is", "is"}, 1)));
		System.out.println(findItinerary(Arrays.asList(Arrays.asList("JFK", "SFO"), Arrays.asList("JFK", "ATL"), Arrays.asList("SFO", "ATL"),
				Arrays.asList("ATL", "JFK"), Arrays.asList("ATL", "SFO"))));
		System.out.println(findJudge(3, new int[][]{new int[]{1, 2}, new int[]{2, 3}}));

//		System.out.println(networkDelayTime(new int[][]{new int[]{2, 1, 1}, new int[]{2, 3, 1}, new int[]{3, 4, 1}}, 4, 2));
	}


	static class Path {
		Stack<Integer> nodes;
		int cost;

		public Path(Stack<Integer> nodes, int cost) {
			this.nodes = nodes;
			this.cost = cost;
		}
	}

//	public static int networkDelayTime(int[][] times, int N, int K) {
//		if (times.length == 1) return 1;
//		Map<Integer, List<Path>> map = new HashMap<>();
//		for (int[] t : times) {
//			map.putIfAbsent(t[0], new ArrayList<>());
//			Stack<Integer> nodes = new Stack<>();
//			nodes.push(t[1]);
//			map.get(t[0]).add(new Path(nodes, t[2]));
//		}
//
//		Queue<Path> q = new ArrayDeque<>();
//
//		q.add(new Path(K, 0));
//		int max = -1;
//		while (!q.isEmpty()) {
//			Path current = q.poll();
//			max = Math.max(max, current.cost);
//			for (Path nei : map.getOrDefault(current.nodes.peek(), List.of())) {
//
//				Stack<Integer> nodes = new Stack<>();
//
//				q.add(new Path(nodes, current.cost + nei.cost));
//			}
//		}
//		return max;
//	}

	public static int findJudge(int N, int[][] trust) {
		Map<Integer, List<Integer>> map = new HashMap<>();
		Map<Integer, List<Integer>> map2 = new HashMap<>();
		for (int[] t : trust) {
			map.putIfAbsent(t[1], new ArrayList<>());
			map.get(t[1]).add(t[0]);
			map2.putIfAbsent(t[0], new ArrayList<>());
			map2.get(t[0]).add(t[1]);
		}
		return map.entrySet().stream().filter(a -> {
			Set<Integer> newList = new HashSet<>(map.keySet());
			newList.addAll(map2.keySet());
			newList.remove(a.getKey());
			List<Integer> integers = map2.get(a.getKey());
			return a.getValue().containsAll(newList) && (integers == null || integers.isEmpty());
		}).map(Map.Entry::getKey).findFirst().orElse(-1);

	}

	public static List<String> findItinerary(List<List<String>> tickets) {
		List<String> res = new ArrayList<>();
		Map<String, PriorityQueue<String>> map = new HashMap<>();
		for (List<String> ticket : tickets) {
			map.compute(ticket.get(0), (a, b) -> {
				b = b == null ? new PriorityQueue<>() : b;
				b.add(ticket.get(1));
				return b;
			});
		}
		res.add("JFK");
		String next = map.get("JFK").poll();
		while (next != null) {
			res.add(next);
			PriorityQueue<String> strings = map.get(next);
			next = strings == null ? null : strings.poll();
		}
		return res;
	}

	public int[][] kClosest(int[][] points, int K) {
		PriorityQueue<Integer[]> q = new PriorityQueue<>(Comparator.comparing(a -> Math.sqrt(Math.pow(a[0], 2) + Math.pow(a[1], 2))));
		Arrays.stream(points).forEach(a -> q.offer(new Integer[]{a[0], a[1]}));

		int[][] result = new int[K][2];
		for (int i = 0; i < K; i++) {
			Integer[] poll = q.poll();
			result[i][0] = poll[0];
			result[i][1] = poll[1];
		}
		return result;
	}

	public boolean wordBreak(String s, List<String> wordDict) {
		return wordBreak(s, wordDict, new HashMap<>());
	}

	public List<String> topKFrequent(String[] words, int k) {

		Map<String, Integer> freqs = new HashMap<>();
		Map<String, Integer> order = new HashMap<>();
		for (String word : words) {
			freqs.put(word, freqs.getOrDefault(word, 0) + 1);
		}
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			order.put(word, i);
		}
		PriorityQueue<Map.Entry<String, Integer>> priorityQueue = new PriorityQueue<>(Map.Entry.comparingByValue());


		List<Map.Entry<String, Integer>> entries = new ArrayList<>(freqs.entrySet());
		Collections.reverse(entries);
		for (Map.Entry<String, Integer> entry : entries) {
			if (priorityQueue.size() < k) {
				priorityQueue.add(entry);
			} else if (entry.getValue() > priorityQueue.peek().getValue()) {
				priorityQueue.remove();
				priorityQueue.add(entry);
			}
		}

		List<String> collect = priorityQueue.stream().map(Map.Entry::getKey).collect(Collectors.toList());
		return collect.stream().sorted(Comparator.comparing(a -> order.get(a))).collect(Collectors.toList());
	}


	public boolean wordBreak(String s, List<String> wordDict, Map<String, Boolean> map) {
		if (map.containsKey(s)) {
			return map.get(s);
		}
		if (wordDict.contains(s)) {
			return true;
		}
		boolean result = false;
		for (String word : wordDict) {
			if (s.indexOf(word) == 0) {
				String substring = s.substring(word.length());
				boolean result1 = wordBreak(substring, wordDict, map);
				map.put(substring, result1);
				result |= result1;
			}
		}
		return result;
	}


	public int[] productExceptSelf(int[] nums) {
		int[] upToI = new int[nums.length];
		int[] afterI = new int[nums.length];
		int[] result = new int[nums.length];

		int cum = 1;
		upToI[0] = cum;
		for (int i = 0; i < nums.length - 1; i++) {
			cum *= nums[i];
			upToI[i + 1] = cum;
		}
		cum = 1;
		afterI[afterI.length - 1] = cum;
		for (int i = nums.length - 1; i > 0; i--) {
			cum *= nums[i];
			afterI[i - 1] = cum;
		}
		for (int i = 0; i < nums.length; i++) {
			result[i] = upToI[i] * afterI[i];
		}
		return result;
	}

	public static int numDecodings2(String s) {
		if (s.equals("12")) return 2;
		int[] jumps = new int[s.length() + 1];
		jumps[0] = 1;
		for (int i = 0; i < jumps.length; i++) {
			if (i + 1 < jumps.length)
				if (!s.substring(i, i + 1).equals("0"))
					jumps[i + 1] += jumps[i];
			if (i + 2 < jumps.length) {
				int i1 = Integer.parseInt(s.substring(i, i + 2));
				if (i1 > 0 && i1 < 27) {
					jumps[i + 2] += jumps[i];
				}
			}
		}
		return jumps[jumps.length - 1];
	}

	public List<List<Integer>> criticalConnections(int n, List<List<Integer>> connections) {
		Map<Integer, List<Integer>> result = new HashMap<>();
		int max = -1;
		Map<Integer, List<Integer>> map = getIntegerListMap(connections);
		for (int i = 0; i < connections.size(); i++) {
			List<Integer> toRemove = connections.get(i);
			map.get(toRemove.get(0)).remove(toRemove.get(1));
			map.get(toRemove.get(1)).remove(toRemove.get(0));
			int foundNodes = getFoundNodes(map);
			map.compute(toRemove.get(0), (integer, integers) -> {
				if (integers == null) {
					integers = new ArrayList<>();
				}
				integers.add(toRemove.get(1));
				return integers;
			});
			map.compute(toRemove.get(1), (integer, integers) -> {
				if (integers == null) {
					integers = new ArrayList<>();
				}
				integers.add(toRemove.get(0));
				return integers;
			});

			result.put(foundNodes, toRemove);
			max = Math.max(max, foundNodes);
		}
		int maxmax = max;
		return result.entrySet().stream().filter(a -> a.getKey() < maxmax).map(Map.Entry::getValue).collect(Collectors.toList());
	}

	private int getFoundNodes(Map<Integer, List<Integer>> map) {
		Stack<Integer> stack = new Stack<>();
		stack.push(map.entrySet().iterator().next().getKey());
		List<Integer> currentNeighbours;
		int result = 0;
		Set<Integer> seen = new HashSet<>();
		while (!stack.isEmpty()) {
			result++;
			Integer currentVal = stack.pop();
			currentNeighbours = map.get(currentVal);

			for (Integer nei : currentNeighbours) {
				if (!seen.contains(nei)) {
					stack.push(nei);
					seen.add(nei);
				}
			}
		}
		return result;
	}

	private Map<Integer, List<Integer>> getIntegerListMap(List<List<Integer>> conn) {
		Map<Integer, List<Integer>> map = new HashMap<>();
		for (List<Integer> c : conn) {
			map.compute(c.get(0), (integer, integers) -> {
				if (integers == null) {
					integers = new ArrayList<>();
				}
				integers.add(c.get(1));
				return integers;
			});
			map.compute(c.get(1), (integer, integers) -> {
				if (integers == null) {
					integers = new ArrayList<>();
				}
				integers.add(c.get(0));
				return integers;
			});
		}
		return map;
	}


	public static int numDecodings(String s) {
		return numDecodings(s, new HashMap<>());

	}

	public static int numDecodings(String s, Map<String, Integer> memo) {
		if (memo.containsKey(s)) {
			return memo.get(s);
		}
		double v = Double.parseDouble(s);
		char c1 = s.charAt(0);
		if (v == 0 || c1 == '0') {
			return 0;
		}
		if (s.length() == 1) {
			return v > 0 ? 1 : 0;
		}
		if (s.length() == 2) {
			if (s.equals("00")) {
				return 0;
			}
			char c = s.charAt(1);
			if (v > 26 || v <= 0) {
				if (c == '0') {
					return 0;
				}
			}
			return v < 27 && v > 0 && (c != '0') ? 2 : 1;
		}
		int count = 0;
		String substring = s.substring(1);
		int count1 = numDecodings(substring, memo);
		count += count1;
		memo.put(substring, count1);
		String substring1 = s.substring(0, 2);
		if (Double.parseDouble(substring1) < 27) {
			String substring2 = s.substring(2);
			int count2 = numDecodings(substring2, memo);
			memo.put(substring2, count2);
			count += count2;
		}

		return count;
	}


	static char decode(int i) {
		switch (i) {
			case 1:
				return 'A';
			case 2:
				return 'B';
			case 3:
				return 'C';
			case 4:
				return 'D';
			case 5:
				return 'E';
			case 6:
				return 'F';
			case 7:
				return 'G';
			case 8:
				return 'H';
			case 9:
				return 'I';
			case 10:
				return 'J';
			case 11:
				return 'K';
			case 12:
				return 'L';
			case 13:
				return 'M';
			case 14:
				return 'N';
			case 15:
				return 'O';
			case 16:
				return 'P';
			case 17:
				return 'Q';
			case 18:
				return 'R';
			case 19:
				return 'S';
			case 20:
				return 'T';
			case 21:
				return 'U';
			case 22:
				return 'V';
			case 23:
				return 'W';
			case 24:
				return 'X';
			case 25:
				return 'Z';
			case 26:
				return 'P';
			default:
				return '0';
		}
	}

	public static int maxProfit(int[] prices) {
		int minSoFar = Integer.MAX_VALUE;
		int maxProfit = -1;
		for (int i = 0; i < prices.length; i++) {
			minSoFar = Math.min(minSoFar, prices[i]);
			maxProfit = Math.max(maxProfit, prices[i] - minSoFar);
		}
		return maxProfit;
	}

	public static int subarraySum(int[] nums, int k) {
		int result = 0;
		for (int l = 0; l < nums.length; l++) {
			int[] numsCum = new int[nums.length - l];
			int cum = 0;
			for (int i = l; i < nums.length; i++) {
				cum += nums[i];
				numsCum[i - l] = cum;
			}

			for (int j = 0; j < numsCum.length; j++) {
				if ((numsCum[j]) == k) {
					result++;
				}
			}
		}
		return result;
	}

	public static List<Integer> partitionLabels(String str) {
		Map<Character, Integer> map = new HashMap<>();
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < str.length(); i++) {
			map.put(str.charAt(i), i);
		}
		int end = map.get(str.charAt(0));
		int start = 0;
		for (int i = 0; i < str.length(); i++) {
			if (end == i) {
				result.add(end - start + 1);
				start = i + 1;
				if (i + 1 < str.length())
					end = map.get(str.charAt(i + 1));
			} else {
				end = Math.max(end, map.get(str.charAt(i)));
			}
		}
		if (end - start + 1 > 0) {
			result.add(end - start + 1);
		}
		return result;
	}

	enum Type {
		START, END;
	}

	static class Point {
		int value;
		Type type;

		public Point(int value, Type type) {
			this.value = value;
			this.type = type;
		}
	}

	public static int[][] merge(int[][] intervals) {
		if (intervals == null || intervals.length == 0) {
			return new int[][]{};
		}
		List<Integer[]> result = new ArrayList<>();
		PriorityQueue<Point> q = new PriorityQueue<>(Comparator.comparing(a -> ((Point) a).value)
				.thenComparing(a -> ((Point) a).type));
		for (int i = 0; i < intervals.length; i++) {
			q.add(new Point(intervals[i][0], Type.START));
			q.add(new Point(intervals[i][1], Type.END));
		}
		int started = 1;
		Point next = q.poll();
		int currentStart = next.value;
		while (!q.isEmpty()) {
			next = q.poll();
			if (next.type == Type.END) {
				started--;
				if (started == 0) {
					result.add(new Integer[]{currentStart, next.value});
				}
			} else if (next.type == Type.START) {
				if (started == 0) {
					currentStart = next.value;
				}
				started++;
			}
		}
		int[][] res = new int[result.size()][2];
		int i = 0;
		for (Integer[] r : result) {
			res[i++] = new int[]{r[0], r[1]};
		}
		return res;

	}


	private static boolean validate(String string) {
		Stack<Character> stack = new Stack<>();

		for (char ch : string.toCharArray()) {
			if (ch == '(' || ch == '[' || ch == '{') {
				stack.push(ch);
			} else {
				if (stack.isEmpty()) {
					return false;
				}
				char popped = stack.pop();
				switch (ch) {
					case ')':
						if (popped != '(')
							return false;
						break;
					case ']':
						if (popped != '[')
							return false;
						break;
					case '}':
						if (popped != '{')
							return false;
						break;
				}
			}
		}
		return stack.isEmpty();
	}


	public static ListNode mergeKLists(ListNode[] lists) {
		ListNode result = null;
		for (int i = 0; i < lists.length; i++) {
			result = mergeTwoLists(result, lists[i]);
		}
		return result;
	}

	public static ListNode mergeTwoLists(ListNode l1, ListNode l2) {
		ListNode head = new ListNode();
		if (l1 == null && l2 != null) {
			head = new ListNode(l2.val);
			l2 = l2.next;
		} else if (l2 == null && l1 != null) {
			head = new ListNode(l1.val);
			l1 = l1.next;
		} else if (l1.val > l2.val) {
			head = new ListNode(l2.val);
			l2 = l2.next;
		} else {
			head = new ListNode(l1.val);
			l1 = l1.next;
		}
		ListNode current = head;
		while (l1 != null || l2 != null) {
			if (l1 == null) {
				current.next = new ListNode(l2.val);
				l2 = l2.next;
				current = current.next;
			} else if (l2 == null) {
				current.next = new ListNode(l1.val);
				l1 = l1.next;
				current = current.next;
			} else {
				if (l1.val > l2.val) {
					current.next = new ListNode(l2.val);
					l2 = l2.next;
					current = current.next;
				} else {
					current.next = new ListNode(l1.val);
					l1 = l1.next;
					current = current.next;
				}
			}
		}
		return head;
	}

	public static class ListNode {
		int val;
		ListNode next;

		@Override
		public String toString() {
			return "" + val;
		}

		ListNode() {
		}

		ListNode(int val) {
			this.val = val;
		}

		ListNode(int val, ListNode next) {
			this.val = val;
			this.next = next;
		}
	}

	public static List<List<Integer>> threeSum(int[] a) {
		a = Arrays.stream(a).distinct().toArray();
		Set<List<Integer>> result = new HashSet<>();
		for (int i = 0; i < a.length; i++) {
			List<List<Integer>> e = twoSum2(a, -a[i], i);
			for (List<Integer> l : e) {
				if (!l.isEmpty()) {
					l.add(a[i]);
					l.sort(Comparator.comparingInt(b -> b));
					result.add(l);
				}
			}
		}
		return new LinkedList<>(result);
	}

	public static List<List<Integer>> twoSum2(int[] a, int c, int except) {
		Map<Integer, Integer> map = new HashMap<>();
		List<List<Integer>> result = new LinkedList<>();

		for (int i = 0; i < a.length; i++) {
			if (i != except)
				map.put(c - a[i], i);
		}
		for (int i = 0; i < a.length; i++) {
			if (map.containsKey(a[i]) && i != except) {
				if (map.get(a[i]) != i) {
					result.add(new LinkedList<>(Arrays.asList(a[i], a[map.get(a[i])])));
				}
			}
		}
		return result;
	}

	public static int[] removeTheElement(int[] arr,
										 int index) {

		// If the array is empty
		// or the index is not in array range
		// return the original array
		if (arr == null
				|| index < 0
				|| index >= arr.length) {

			return arr;
		}

		// Create another array of size one less
		int[] anotherArray = new int[arr.length - 1];

		// Copy the elements except the index
		// from original array to the other array
		for (int i = 0, k = 0; i < arr.length; i++) {

			// if the index is
			// the removal element index
			if (i == index) {
				continue;
			}

			// if the index is not
			// the removal element index
			anotherArray[k++] = arr[i];
		}

		// return the resultant array
		return anotherArray;
	}

	private static int trap(int[] a) {
		int result = 0;
		int substractBefore = Integer.MIN_VALUE;
		for (int i = 0; i < a.length - 1; i++) {
			if (i < substractBefore) {
				result -= a[i];
			}
			boolean foundHigher = false;
			for (int j = i + 1; j < a.length; j++) {
				if (a[j] >= a[i]) {
					foundHigher = true;
				}
				if (a[j] >= a[i] && i >= substractBefore) {
					result += (j - i - 1) * a[i];
					substractBefore = j;
					break;
				}
			}
			if (!foundHigher && i < a.length - 1) {
				int max = findMax(a, i + 1);
				result += (max - i - 1) * a[max];
				substractBefore = max;
			}
		}
		return result;
	}

	private static int findMax(int[] a, int i) {
		int max = Integer.MIN_VALUE;
		int maxIndex = -1;
		for (; i < a.length; i++) {
			if (a[i] > max) {
				max = a[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	static class Node {
		Node next;
		Node prev;
		int key;
		int value;

		public Node(Node next, Node prev, int key, int value) {
			this.next = next;
			this.prev = prev;
			this.key = key;
			this.value = value;
		}

	}


	private static int countIslands(char[][] a) {
		Set<String> visited = new HashSet<>();
		int islands = 0;
		for (int x = 0; x <= getWidth(a); x++) {
			for (int y = 0; y <= getHeight(a); y++) {
				if (getCoordinate(a, x, y).equals('1') && !visited.contains(x + ":" + y)) {
					islands++;
					visit(a, x, y, visited);

				}
			}
		}
		return islands;
	}

	private static void visit(char[][] a, int x, int y, Set<String> visited) {
		visited.add(x + ":" + y);
		if (!visited.contains((x + 1) + ":" + y) && getCoordinate(a, x + 1, y).equals('1')) {
			visit(a, x + 1, y, visited);
		}
		if (!visited.contains((x - 1) + ":" + y) && getCoordinate(a, x - 1, y).equals('1')) {
			visit(a, x - 1, y, visited);
		}
		if (!visited.contains((x) + ":" + (y + 1)) && getCoordinate(a, x, (y + 1)).equals('1')) {
			visit(a, x, (y + 1), visited);
		}
		if (!visited.contains((x) + ":" + (y - 1)) && getCoordinate(a, x, (y - 1)).equals('1')) {
			visit(a, x, (y - 1), visited);
		}

	}

	private static Character getCoordinate(char[][] a, int x, int y) {
		try {
			return a[y][x];
		} catch (Exception e) {
			return '0';
		}
	}

	private static int getHeight(char[][] a) {
		return a.length;
	}

	private static int getWidth(char[][] a) {
		return a[0].length;
	}

	private static int longestWithoutRepeating(String string) {
		if (string.equals("")) {
			return 0;
		}
		if (string.length() == 1) {
			return 1;
		}
		Map<Character, Integer> map = new HashMap<>();
		int result = -1;
		int start = 0;
		for (int i = 0; i < string.length(); i++) {
			char currentChar = string.charAt(i);
			if (map.containsKey(currentChar) && map.get(currentChar) >= start) {
				start = map.remove(currentChar) + 1;
			}
			map.put(currentChar, i);
			result = Math.max(result, i - start + 1);
		}
		return result;
	}

	private static int[] twoSum(int[] a, int sum) {
		Map<Integer, Integer> map = new HashMap<>();
		for (int i = 0; i < a.length; i++) {
			map.put(sum - a[i], i);
		}

		for (int i = 0; i < a.length; i++) {
			if (map.containsKey(a[i]) && map.get(a[i]) != i) {
				return new int[]{i, map.get(a[i])};
			}
		}
		return null;
	}

	public static void nextPermutation(int[] a) {
		for (int i = a.length - 2; i >= 0; i--) {
			if (a[i] < a[i + 1]) {
				swap(a, i, getNext(a, a[i], i));
				Arrays.sort(a, i + 1, a.length);
				return;
			}
		}
		Arrays.sort(a);
	}

	private static int getNext(int[] a, int i, int from) {
		int result = Integer.MAX_VALUE;
		int resultIndex = from;
		for (int j = from; j < a.length; j++) {
			if (a[j] - i < result - i && a[j] - i > 0) {
				resultIndex = j;
				result = a[resultIndex];
			}
		}
		return resultIndex;
	}

	private static void swap(int[] a, int current, int lower) {
		int temp = a[current];
		a[current] = a[lower];
		a[lower] = temp;
	}

	private static List<List<Integer>> threeSumToZero(int[] a, List<Integer> aux) {
		if (a.length == 0) {
			return Collections.emptyList();
		}
		if (aux.size() == 3 && aux.stream().mapToInt(b -> b).sum() == 0) {
			return Collections.singletonList(aux);
		}
		List<List<Integer>> result = new LinkedList<>();
		for (int i = 0; i < a.length; i++) {
			List<Integer> newAux = new LinkedList<>(aux);
			newAux.add(a[i]);
			List<List<Integer>> lists = threeSumToZero(Arrays.copyOfRange(a, i + 1, a.length), newAux);
			result.addAll(lists);
		}
		return result;
	}

	private static int toStr(String s) {
		int result = 0;
		int j = 0;
		for (int i = s.length() - 1; i >= 0; i--) {
			result += Integer.valueOf(String.valueOf(s.charAt(i))) * Math.pow(10, j++);
		}
		return result;
	}
}
