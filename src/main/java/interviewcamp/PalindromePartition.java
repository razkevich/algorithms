package interviewcamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PalindromePartition {
	public static void main(String[] args) {
		System.out.println(new PalindromePartition().partition("aab"));
	}

	public List<List<String>> partition(String s) {
		return partition(s, new ArrayList<>());
	}

	class Key {
		String s;
		List<String> sofar;

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Key key = (Key) o;
			return Objects.equals(s, key.s) &&
					Objects.equals(sofar, key.sofar);
		}

		@Override
		public int hashCode() {
			return Objects.hash(s, sofar);
		}

		public Key(String s, List<String> sofar) {
			this.s = s;
			this.sofar = sofar;
		}
	}

	public List<List<String>> partition(String s, List<String> soFar) {
		Key key = new Key(s, soFar);
		if (memo2.containsKey(key)) {
			return memo2.get(key);

		}
		List<List<String>> result = new ArrayList<>();
		if (s.isEmpty()) {
			memo2.put(key, List.of(soFar));
			return List.of(soFar);
		}
		for (int i = 0; i < s.length(); i++) {
			if (isPalindrome(s.substring(0, i + 1))) {
				List<String> sofarnew = new ArrayList<>(soFar);
				sofarnew.add(s.substring(0, i + 1));
				result.addAll(partition(s.substring(i + 1), sofarnew));
			}
		}
		memo2.put(key, result);
		return result;
	}

	Map<String, Boolean> memo1 = new HashMap<>();
	Map<Key, List<List<String>>> memo2 = new HashMap<>();

	boolean isPalindrome(String s) {
		if (memo1.containsKey(s)) return memo1.get(s);
		int n = s.length();
		for (int i = 0; i < (n / 2); ++i) {
			if (s.charAt(i) != s.charAt(n - i - 1)) {
				memo1.put(s, false);
				return false;
			}
		}
		memo1.put(s, true);
		return true;
	}
}
