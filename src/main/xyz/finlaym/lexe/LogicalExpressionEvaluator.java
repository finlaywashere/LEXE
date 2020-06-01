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

	private static final String[] OPERATORS = { "||", "&&", "(", "==", "!=" };

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
			expression = expression.substring(0, index) + String.valueOf(result) + expression.substring(end + 1);
		}
		newExp = "";
		for (int i = 0; i < expression.length(); i++) {
			if (expression.charAt(i) == '(') {
				bCount++;
				newExp += '(';
				last++;
				continue;
			}
			if (expression.charAt(i) == ')') {
				bCount--;
				newExp += ')';
				last++;
				continue;
			}
			// if (bCount != 0) {
			// newExp += expression.charAt(i);
			// continue;
			// }
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
				continue;
			}
			continue;
		}
		expression = newExp;

		// Evaluate boolean logic
		while (true) {
			boolean found = false;
			for (String s : OPERATORS) {
				if (expression.contains(s)) {
					found = true;
					break;
				}
			}
			if (!found)
				break;
			for (int i = 0; i < expression.length(); i++) {
				found = false;
				for (String s : OPERATORS) {
					if (i + s.length() - 1 >= expression.length())
						continue;
					String sub = expression.substring(i, i + s.length());
					if (sub.equals(s)) {
						String beforeS = expression.substring(0, i);
						int end = expression.length();
						for (int i1 = i + sub.length(); i1 < expression.length(); i1++) {
							for (String s2 : OPERATORS) {
								if (i1 + s2.length() - 1 >= expression.length())
									continue;
								String sub2 = expression.substring(i1, i1 + s2.length());
								if (sub2.equals(s2)) {
									// Found operator
									end = i1;
									break;
								}
							}
						}
						String afterS = expression.substring(i + sub.length(), end);
						boolean result = false;
						if (sub.equals("||")) {
							boolean beforeB = (isInt(beforeS) ? true : Boolean.valueOf(beforeS));
							boolean afterB = (isInt(afterS) ? true : Boolean.valueOf(afterS));
							result = Boolean.valueOf(beforeB) || Boolean.valueOf(afterB);
						} else if (sub.equals("&&")) {
							boolean beforeB = (isInt(beforeS) ? true : Boolean.valueOf(beforeS));
							boolean afterB = (isInt(afterS) ? true : Boolean.valueOf(afterS));
							result = Boolean.valueOf(beforeB) && Boolean.valueOf(afterB);
						} else if (sub.equals("==")) {
							result = beforeS.equals(afterS);
						} else if (sub.equals("!=")) {
							result = !beforeS.equals(afterS);
						}
						expression = result + expression.substring(end);
						break;
					}
				}
			}
		}
		return Boolean.valueOf(expression);
	}
	private static boolean isInt(String str) {
		try {
			@SuppressWarnings("unused")
			int i = Integer.valueOf(str);
			return true;
		}catch(NumberFormatException e) {
			return false;
		}
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
				if (s.length() == 0)
					continue;
				if (s.equals("true") || s.equals("false"))
					continue;
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
