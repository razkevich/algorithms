package interviewcamp;

import java.util.Stack;

public class Chapter8 {
	public static void main(String[] args) {
		StackWithMax stackWithMax = new StackWithMax();
		stackWithMax.add(6);
		stackWithMax.add(7);
		stackWithMax.add(5);
		stackWithMax.add(8);
		stackWithMax.add(1);

		System.out.println(stackWithMax.max());
		stackWithMax.remove();
		stackWithMax.remove();
		System.out.println(stackWithMax.max());
		stackWithMax.remove();
		System.out.println(stackWithMax.max());
		stackWithMax.remove();
		System.out.println(stackWithMax.max());
		System.out.println(eval("1+3*2+2-1"));
		System.out.println(eval("1+2*(2+0)-2"));

	}

	private static char eval(String s) {
		Stack<Character> operatorStack = new Stack<>();
		Stack<Character> operandStack = new Stack<>();
		for (char ch : s.toCharArray()) {
			if (ch == ')') {
				while (operatorStack.peek() != '(') {
					char result = process(operatorStack.pop(), operandStack.pop(), operandStack.pop());
					operandStack.push(result);
				}
				operatorStack.pop();
			} else if (ch == '(') {
				operatorStack.push(ch);
				continue;
			} else if (isOperator(ch) && !operatorStack.isEmpty() && precedence(ch) <= precedence(operatorStack.peek())) {
				while (!operatorStack.isEmpty()) {
					char result = process(operatorStack.pop(), operandStack.pop(), operandStack.pop());
					operandStack.push(result);
				}
				operatorStack.push(ch);
			} else {
				(isOperator(ch) ? operatorStack : operandStack).push(ch);
			}
		}
		while (!operatorStack.isEmpty()) {
			char result = process(operatorStack.pop(), operandStack.pop(), operandStack.pop());
			operandStack.push(result);
		}
		return operandStack.pop();
	}

	static char process(char op, char num2, char num1) {
		int n1 = Integer.valueOf(new String(new char[]{num1}));
		int n2 = Integer.valueOf(new String(new char[]{num2}));
		int result = Integer.MAX_VALUE;
		switch (op) {
			case '/':
				result = n1 / n2;
				break;
			case '*':
				result = n1 * n2;
				break;
			case '+':
				result = n1 + n2;
				break;
			case '-':
				result = n1 - n2;
				break;
		}
		return String.valueOf(result).charAt(0);
	}

	private static int precedence(char ch) {
		switch (ch) {
			case '/':
			case '*':
				return 2;
			case '+':
			case '-':
				return 1;
			case '(':
			case ')':
				return 0;
			default:
				throw new IllegalArgumentException("Invalid operator: " + ch);
		}
	}

	private static boolean isOperator(char ch) {
		return ch == '+' || ch == '-' || ch == '*' || ch == '/';
	}

	private static boolean isOperand(char ch) {
		return (ch >= '0') && (ch <= '9');
	}

	static class StackWithMax {
		Stack<Integer> mainStack = new Stack<>();
		Stack<Integer> maxStack = new Stack<>();

		int max() {
			return maxStack.peek();
		}

		void add(int i) {
			mainStack.push(i);
			if (maxStack.isEmpty() || i > maxStack.peek()) {
				maxStack.push(i);
			}
		}

		int remove() {
			if (mainStack.peek() == maxStack.peek()) {
				maxStack.pop();
			}
			return mainStack.pop();
		}
	}
}
