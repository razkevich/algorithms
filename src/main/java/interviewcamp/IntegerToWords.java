package interviewcamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class IntegerToWords {

	public static void main(String[] args) {
		System.out.println(new IntegerToWords().numberToWords(13333223));

	}

	public String numberToWords(int num) {
		String string = String.valueOf(num);
		List<String> blocks = divide(string);

		int i = 0;
		Stack<String> stack = new Stack<>();
		for (String block : blocks) {
			stack.add(toWord(block) + " " + abcd(i));
			i++;
		}

		String result = "";
		while (!stack.isEmpty()) {
			result += stack.pop();
			if (!stack.isEmpty()) result+=" ";
		}
		return result;
	}

	String abcd(int i) {
		switch (i) {
			case 0:
				return "";
			case 1:
				return "thousand";
			case 2:
				return "million";
			case 3:
				return "billion";
			case 4:
				return "trillion";
		}
		return "";
	}

	List<String> divide(String string) {
		List<String> result = new ArrayList<>();
		for (int i = string.length(); i >= 0; i -= 3) {
			String substring = string.substring(Math.max(0, i - 3), i);
			if (!substring.isEmpty())
				result.add(substring);
		}
		return result;
	}


	String toWord(String number) {
		if (number.length() == 1) {
			return digit(number.charAt(0));
		}
		if (number.length() == 2) {
			if (number.charAt(0) == '1') {
				return digit2(number);
			}
			return decimal(number.charAt(0)) + digit(number.charAt(1));
		}
		if (number.length() == 3) {
			if (number.charAt(1) == '1') {
				return digit(number.charAt(0)) + " hundred " + digit2(number);
			}
			return digit(number.charAt(0)) + " hundred " + decimal(number.charAt(1)) + " " + digit(number.charAt(2));
		}
		return "";
	}

	String digit(char digit) {
		switch (digit) {
			case '0':
				return "";
			case '1':
				return "One";
			case '2':
				return "Two";
			case '3':
				return "Three";
			case '4':
				return "Four";
			case '5':
				return "Five";
			case '6':
				return "Six";
			case '7':
				return "Seven";
			case '8':
				return "Eight";
			case '9':
				return "Nine";
		}
		return "";
	}

	String decimal(char digit) {
		switch (digit) {
			case '0':
				return "";

			case '2':
				return "Twenty";
			case '3':
				return "Thirty";
			case '4':
				return "Fourty";
			case '5':
				return "Fifty";
			case '6':
				return "Sixty";
			case '7':
				return "Seventy";
			case '8':
				return "Eighty";
			case '9':
				return "Ninety";
		}
		return "";
	}

	String digit2(String digit2) {
		switch (digit2) {
			case "10":
				return "Ten";
			case "11":
				return "Eleven";
			case "12":
				return "Twelve";
			case "13":
				return "Thirteen";
			case "14":
				return "Fourteen";
			case "15":
				return "Fifteen";
			case "16":
				return "Sixteen";
			case "17":
				return "Seventeen";
			case "18":
				return "Eighteen";
			case "19":
				return "Nineteen";
		}
		return "";
	}

}
