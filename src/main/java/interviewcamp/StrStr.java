package interviewcamp;

public class StrStr {


	public static void main(String[] args) {
		System.out.println(new StrStr().strStr("hello world", "llo"));
	}

	public int strStr(String str, String target) {
		if (str == null || target == null) throw new NullPointerException();
		if (target.isEmpty())
			return 0;
		if (target.length() > str.length()) return -1;
		int hashT = 0;
		int hash = 0;
		for (int i = 0; i < target.length(); i++) {
			hashT = hashT * 53 + target.charAt(i);
			hash = hash * 53 + str.charAt(i);
		}

		if (hashT == hash && target.equals(str.substring(0, target.length()))) return 0;

		int xPow = 1;
		for (int i = 0; i < target.length() - 1; i++) {
			xPow *= 53;
		}


		for (int i = target.length(); i < str.length(); i++) {
			hash -= str.charAt(i - target.length()) * xPow;
			hash *= 53;
			hash += str.charAt(i);
			if (hashT == hash && target.equals(str.substring(i - target.length() + 1, i + 1))) return i - target.length() + 1;
		}

		return -1;
	}
}
