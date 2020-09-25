package interviewcamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnagramsGroup {
	public static void main(String[] args) {
		System.out.println(new AnagramsGroup().groupAnagrams(new String[]{"eat", "tea", "tan", "ate", "nat", "bat"}));

	}


	public List<List<String>> groupAnagrams(String[] strs) {
		List<List<String>> res = new ArrayList<List<String>>();
		Set<Integer> taken = new HashSet<>();
		for (int i = 0; i < strs.length; i++) {

			if (!taken.contains(i)) {
				List<String> cur = new ArrayList<>();

				cur.add(strs[i]);
				taken.add(i);
				for (int j = i + 1; j < strs.length; j++) {

					if (isAna(strs[i], strs[j])) {
						cur.add(strs[j]);
						taken.add(j);
					}
				}
				res.add(cur);

			}

		}
		return res;
	}

	Map<String, String> memo = new HashMap<>();

	boolean isAna(String s1, String s2) {
		char[] c1;
		char[] c2;
		if (memo.containsKey(s1)) {
			c1 = memo.get(s1).toCharArray();
		} else {
			c1 = s1.toCharArray();
			Arrays.sort(c1);
			memo.put(s1,new String(c1));
		}
		if (memo.containsKey(s2)) {
			c2 = memo.get(s2).toCharArray();
		} else {
			c2 = s2.toCharArray();
			Arrays.sort(c2);
			memo.put(s2,new String(c2));
		}
		return (Arrays.equals(c1, c2));
	}
}
