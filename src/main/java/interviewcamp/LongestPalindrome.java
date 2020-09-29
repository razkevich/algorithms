package interviewcamp;

public class LongestPalindrome {
	public static void main(String[] args) {
		System.out.println(new LongestPalindrome().longestPalindrome("babad"));
	}


	public String longestPalindrome(String s) {
		if (s.isEmpty()) return "";
		int resultStart = -1, resultEnd = -1;
		outer:
		for (int i = 0; i < s.length(); i++) {
			int j = 1;
			while (i - j >= 0 && i + j < s.length()) {
				if (s.charAt(i + j) == s.charAt(i - j)) {
					if (2 * j > resultEnd - resultStart) {
						resultStart = i - j;
						resultEnd = i + j;
					}
				} else break ;
				j++;
			}
			j = 0;
			while (i - j >= 0 && i + j + 1 < s.length()) {
				if (s.charAt(i - j) == s.charAt(i + j + 1)) {
					if (2 * j + 1 > resultEnd - resultStart) {
						resultStart = i - j;
						resultEnd = i + j + 1;
					}
				} else break ;
				j++;
			}
		}
		try {
			return s.substring(resultStart, resultEnd + 1);
		} catch (Exception e) {
			return s.substring(0, 1);
		}
	}
}
