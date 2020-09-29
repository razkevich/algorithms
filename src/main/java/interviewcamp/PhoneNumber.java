package interviewcamp;

public class PhoneNumber {
	public static void main(String[] args) {
		printWords("", new int[]{4, 5, 6}, 0);
	}

	public static void printWords(String soFar, int[] phoneNumber, int from) {
		if (from == phoneNumber.length) {
			System.out.println(soFar);
		} else {
			char[] letters = getLetters(phoneNumber[from]);
			if (letters.length == 0) {
				printWords(soFar, phoneNumber, from + 1);
			} else
				for (char p : letters) {
					printWords(soFar + p, phoneNumber, from + 1);
				}
		}
	}

	public static char[] getLetters(int digit) {
		switch (digit) {
			case 0:
				return new char[]{};
			case 1:
				return new char[]{};
			case 2:
				return new char[]{'a', 'b', 'c'};
			case 3:
				return new char[]{'d', 'e', 'f'};
			case 4:
				return new char[]{'g', 'h', 'i'};
			case 5:
				return new char[]{'j', 'k', 'l'};
			case 6:
				return new char[]{'m', 'n', 'o'};
			case 7:
				return new char[]{'p', 'q', 'r', 's'};
			case 8:
				return new char[]{'t', 'u', 'v'};
			case 9:
				return new char[]{'w', 'x', 'y', 'z'};
		}
		throw new IllegalArgumentException("Invalid Digit " + digit);
	}
}
