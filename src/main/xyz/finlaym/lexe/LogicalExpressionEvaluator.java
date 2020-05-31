package xyz.finlaym.lexe;

import java.util.Arrays;

import xyz.finlaym.mexe.FunctionCallback;
import xyz.finlaym.mexe.MathExpressionEvaluator;
import xyz.finlaym.mexe.Variable;

public class LogicalExpressionEvaluator {
	public static boolean evaluate(String expression) {
		return evaluate(expression, new Variable[0], null);
	}

	public static boolean evaluate(String expression, Variable[] variables) {
		return evaluate(expression, variables, null);
	}

	// NOTE: != must come before ! in this array
	private static final String[] OPERATORS = { "||", "&&", "(", "==", "!=", "!" };

	public static boolean evaluate(String expression, Variable[] variables, FunctionCallback callback) {
		// Remove all the whitespaces
		String newExp = "";
		for (char c : expression.toCharArray()) {
			if (c != ' ')
				newExp += c;
		}
		expression = newExp;

		Arrays.sort(variables);

		// Identify and solve math equations
		int bCount = 0;
		int last = 0;
		newExp = "";
		for (int i = 0; i < expression.length(); i++) {
			if (expression.charAt(i) == '(') {
				bCount++;
				newExp += '(';
				continue;
			}
			if (expression.charAt(i) == ')') {
				bCount--;
				newExp += ')';
				continue;
			}
			if (bCount != 0) {
				newExp += expression.charAt(i);
				continue;
			}
			boolean found = false;
			for (String s : OPERATORS) {
				if (i + s.length() - 1 >= expression.length())
					continue;
				String sub = expression.substring(i, i + s.length());
				if (sub.equals(s)) {
					// Found operator
					String sEquation = expression.substring(last, i);
					last = i + sub.length();
					boolean bEquation = isEquation(sEquation, variables);
					if (bEquation) {
						double result = MathExpressionEvaluator.evaluate(sEquation, variables, callback);
						newExp += String.valueOf(result) + sub;
						found = true;
						break;
					}
					newExp += sEquation + sub;
				}
			}
			if (found == true)
				continue;
			if (i == expression.length() - 1) {
				// Found operator
				String sEquation = expression.substring(last, i + 1);
				boolean bEquation = isEquation(sEquation, variables);
				if (bEquation) {
					double result = MathExpressionEvaluator.evaluate(sEquation, variables, callback);
					newExp += String.valueOf(result);
					found = true;
					break;
				}
				newExp += sEquation;
			}
			continue;
		}
		expression = newExp;

		// Recursively solve brackets
		while (expression.contains("(")) {
			int index = expression.indexOf("(");
			int end = -1;
			bCount = 0;
			for (int i = index + 1; i < expression.length(); i++) {
				char c = expression.charAt(i);
				if (c == '(') {
					bCount++;
					continue;
				}
				if (c == ')') {
					if (bCount == 0) {
						end = i;
						break;
					} else {
						bCount--;
						continue;
					}
				}
			}
			if (end == -1)
				throw new RuntimeException("No closing bracket found in logical expression!");
			String insideBracket = expression.substring(index + 1, end);
			boolean result = evaluate(insideBracket, variables, callback);
			expression = expression.substring(0, index-1) + String.valueOf(result) + expression.substring(end + 1);
		}

		return false;
	}

	private static boolean isEquation(String expression, Variable[] variables) {
		if (expression.startsWith("-")) {
			return true;
		}
		int c = (int) expression.charAt(0);
		if (c >= 48 && c <= 57) {
			return true;
		}
		for (int i = variables.length - 1; i >= 0; i--) {
			String vName = variables[i].getName();
			if (expression.startsWith(vName))
				return true;
		}
		if (expression.contains("(")) {
			String[] split = expression.split("\\(");
			for (String s : split) {
				char lastChar = s.charAt(s.length() - 1);
				int lastCharValue = (int) lastChar;
				// If its a letter than its a function
				if ((lastCharValue >= 65 && lastCharValue <= 90) || (lastCharValue >= 97 && lastCharValue <= 122))
					return true;
			}
		}
		if (expression.startsWith("("))
			return isEquation(expression.substring(1), variables);
		return false;
	}
}
