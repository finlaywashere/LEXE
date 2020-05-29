package xyz.finlaym.mexe;

import java.util.Arrays;

public class MathExpressionEvaluator {
	public static double evaluate(String expression) {
		return evaluate(expression, new Variable[0]);
	}
	public static double evaluate(String expression, Variable[] variables) {
		String newExp = "";
		for(char c : expression.toCharArray()) {
			if(c != ' ')
				newExp += c;
		}
		expression = newExp;
		
		Arrays.sort(variables);
		
		for(int i = variables.length - 1; i >= 0; i--) {
			Variable v = variables[i];
			String value = String.valueOf(v.getValue());
			if(value.startsWith("-")) {
				value = "("+value+")";
			}
			expression = expression.replaceAll(v.getName(), value);
		}
		
		if(expression.startsWith("-")) {
			expression = "n"+expression.substring(1);
		}
		
		while(expression.contains("(-")) {
			int i = expression.indexOf("(-");
			int closeI = expression.substring(i).indexOf(")")+i;
			expression = expression.substring(0, i)+"n"+expression.substring(i+2, closeI)+expression.substring(closeI+1);
		}
		// Handle brackets
		int[] bracketStarts = findBrackets(expression);
		int[] bracketEnds = findClosingBrackets(expression, bracketStarts);
		int offset = 0;
		for (int i = 0; i < bracketStarts.length; i++) {
			int start = bracketStarts[i];
			int end = bracketEnds[i];
			// Magic bracket recursion
			double result = evaluate(expression.substring(start + 1 - offset, end - offset));
			String resultS = String.valueOf(result);
			String newExpression = expression.substring(0, start - offset) + resultS
					+ expression.substring(end + 1 - offset);
			expression = newExpression;
			offset += end - start - resultS.length();
		}
		offset = 0;
		int count = 0;
		while(expression.contains("^")) {
			for(int i = 0; i < expression.length(); i++) {
				char c = expression.charAt(i);
				if(c == '^') {
					expression = solve(c, expression, i,count);
					count++;
					break;
				}
			}
		}
		count = 0;
		while(expression.contains("*") || expression.contains("/") || expression.contains("%")) {
			for(int i = 0; i < expression.length(); i++) {
				char c = expression.charAt(i);
				if(c == '*' || c == '/' || c == '%') {
					expression = solve(c, expression, i,count);
					count++;
					break;
				}
			}
		}
		count = 0;
		while(expression.contains("+") || expression.contains("-")) {
			for(int i = 0; i < expression.length(); i++) {
				char c = expression.charAt(i);
				if(c == '+' || c == '-') {
					expression = solve(c, expression, i,count);
					break;
				}
			}
		}
		return (expression.startsWith("n") ? -1 * Double.valueOf(expression.substring(1)) : Double.valueOf(expression));
	}
	private static String solve(char c, String expression, int i, int count) {
		int before = findDoubleBefore(i - 1, expression);
		int after = findDoubleAfter(i + 1, expression);
		String beforeS = expression.substring(before+(count > 0 ? 1 : 0), i);
		String afterS = expression.substring(i + 1, after);
		double beforeD = (beforeS.startsWith("n") ? -1 * Double.valueOf(beforeS.substring(1)) : Double.valueOf(beforeS));
		double afterD = (afterS.startsWith("n") ? -1 * Double.valueOf(afterS.substring(1)) : Double.valueOf(afterS));
		double newVal = 0;
		if (c == '*') {
			newVal = beforeD * afterD;
		} else if (c == '/'){
			newVal = beforeD / afterD;
		} else if(c == '+') {
			newVal = beforeD + afterD;
		}else if(c == '-') {
			newVal = beforeD - afterD;
		} else if(c == '^') {
			newVal = Math.pow(beforeD, afterD);
		} else if(c == '%') {
			newVal = beforeD % afterD;
		}
		String newValS = String.valueOf(newVal);
		newValS = newValS.replaceAll("-", "n");
		String newExpression = expression.substring(0, before+(count == 0 ? 0 : 1)) + newValS
				+ expression.substring(after);
		return newExpression;
	}

	private static final char[] OPERATORS = { '+', '-', '*', '/', '(' };

	private static int findDoubleBefore(int i, String expression) {
		for (int i1 = i; i1 > 0; i1--) {
			char c = expression.charAt(i1);
			for (char c1 : OPERATORS) {
				if (c == c1) {
					return i1;
				}
			}
		}
		return 0;
	}

	private static int findDoubleAfter(int i, String expression) {
		for (int i1 = i; i1 < expression.length(); i1++) {
			char c = expression.charAt(i1);
			for (char c1 : OPERATORS) {
				if (c == c1) {
					return i1;
				}
			}
		}
		return expression.length();
	}

	private static int[] findBrackets(String expression) {
		int count = 0;
		int bCount = 0;
		for (char c : expression.toCharArray()) {
			if (c == '(' && bCount == 0)
				count++;
			if (c == '(')
				bCount++;
			if (c == ')')
				bCount--;
		}
		int[] ret = new int[count];
		int curr = 0;
		bCount = 0;
		for (int i = 0; i < expression.length(); i++) {
			char c = expression.charAt(i);
			if (c == '(' && bCount == 0) {
				ret[curr] = i;
				curr++;
			}
			if (c == '(')
				bCount++;
			if (c == ')')
				bCount--;
		}
		return ret;
	}

	private static int[] findClosingBrackets(String expression, int[] bracketStarts) {
		int[] ret = new int[bracketStarts.length];
		int curr = 0;
		int bCount = 0;
		for (int i = 0; i < expression.length(); i++) {
			char c = expression.charAt(i);
			if (c == ')' && bCount == 1) {
				ret[curr] = i;
				curr++;
			}
			if (c == '(')
				bCount++;
			if (c == ')')
				bCount--;
		}
		return ret;
	}
}
