package prep.stacks;

import java.util.*;

// LC 227 — Basic Calculator II.
// Template: two-stack shunting-yard.
//   Numbers always push to numStack.
//   On a new operator: while top of opStack has rank >= incoming op's rank,
//   pop-and-apply (push result back onto numStack). Then push the new op.
//   At end: drain remaining ops the same way. Final answer is top of numStack.
// The `>=` comparison is what gives `+ - * /` left-associativity for free —
// equal-precedence ops are flushed before the next is pushed. Switch `>=` to
// `>` for right-associative ops (e.g. exponentiation).
// Pattern transfer: Basic Calculator I/III (parens — `(` rank 0 / `)` drains
// to matching `(`), Evaluate RPN, mini-DSL evaluators in design rounds.
public class BasicCalculatorII {

    public int calculate(String[] strings) {
        ArrayDeque<String> operatorStack = new ArrayDeque<>();
        ArrayDeque<String> numberStack = new ArrayDeque<>();
        for (String s : strings) {
            if (isOperator(s)) {
                while (!operatorStack.isEmpty() && rank(operatorStack.peek()) >= rank(s)) {
                    String pop1 = numberStack.pop();
                    String pop2 = numberStack.pop();
                    String op = operatorStack.pop();
                    numberStack.push(String.valueOf(apply(op, pop1, pop2)));
                }
                operatorStack.push(s);
            } else {
                numberStack.push(s);
            }
        }
        while (numberStack.size() > 1) {
            String pop = numberStack.pop();
            String pop1 = numberStack.pop();
            String op = operatorStack.pop();
            numberStack.push(String.valueOf(apply(op, pop, pop1)));
        }
        return Integer.parseInt(numberStack.pop());
    }

    // n1 = first popped (RIGHT operand, pushed last); n2 = second popped (LEFT).
    // For `-` and `/` order matters: compute left op right.
    private int apply(String op, String n1, String n2) {
        int right = Integer.parseInt(n1);
        int left = Integer.parseInt(n2);
        return switch (op) {
            case "+" -> left + right;
            case "-" -> left - right;
            case "*" -> left * right;
            case "/" -> left / right;
            default -> throw new IllegalArgumentException("op: " + op);
        };
    }

    private boolean isOperator(String s) {
        return "+".equals(s) || "-".equals(s) || "*".equals(s) || "/".equals(s);
    }

    private int rank(String op) {
        return switch (op) {
            case "*", "/" -> 2;
            case "+", "-" -> 1;
            default -> 0;
        };
    }

    public static void main(String[] args) {
        BasicCalculatorII c = new BasicCalculatorII();
        System.out.println(c.calculate(new String[]{"3","+","2","*","2"}));        // 7
        System.out.println(c.calculate(new String[]{"3","/","2"}));                // 1
        System.out.println(c.calculate(new String[]{"3","+","5","/","2"}));        // 5
        System.out.println(c.calculate(new String[]{"14","-","3","/","2"}));       // 13
        System.out.println(c.calculate(new String[]{"2","-","1","-","1"}));        // 0  (left-assoc)
        System.out.println(c.calculate(new String[]{"1","-","1","+","1"}));        // 1
        System.out.println(c.calculate(new String[]{"5","-","2","-","1","-","1"}));// 1
    }
}
