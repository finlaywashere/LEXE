package xyz.finlaym.mexe;

import java.util.Arrays;

public class MathExpressionEvaluator {
	public static double evaluate(String expression) {
		return evaluate(expression, new Variable[0], null);
	}

	public static double evaluate(String expression, Variable[] variables) {
		return evaluate(expression, variables, null);
	}

	public static double evaluate(String expression, Variable[] variables, FunctionCallback callback) {
		String newExp = "";
		for (char c : expression.toCharArray()) {
			if (c != ' ')
				newExp += c;
		}
		expression = newExp;

		Arrays.sort(variables);

		for (int i = variables.length - 1; i >= 0; i--) {
			Variable v = variables[i];
			String value = String.valueOf(v.getValue());
			if (value.startsWith("-")) {
				value = "(" + value + ")";
			}
			expression = expression.replaceAll(v.getName(), value);
		}

		if (expression.startsWith("-")) {
			expression = "n" + expression.substring(1);
		}

		while (expression.contains("(-")) {
			int i = expression.indexOf("(-");
			int closeI = expression.substring(i).indexOf(")") + i;
			if(i != 0) {
				char c = expression.charAt(i-1);
				boolean operator = false;
				for(char c1 : OPERATORS) {
					if(c == c1) {
						operator = true;
						break;
					}
				}
				if(operator) {
					expression = expression.substring(0, i) + "n" + expression.substring(i + 2, closeI)
					+ expression.substring(closeI + 1);
				}else {
					expression = expression.substring(0, i) + "&&-" + expression.substring(i + 2, closeI+1)
					+ expression.substring(closeI + 1);
				}
			}
		}
		expression = expression.replaceAll("&&-", "(-");
		// Handle brackets
		int start = findBracket(expression);
		while(start != -1) {
			int end = findClosingBracket(expression);
			boolean func = true;
			if (start != 0) {
				char preOp = expression.charAt(start - 1);
				for (char c : OPERATORS) {
					if (preOp == c) {
						func = false;
						break;
					}
				}
			}else 
				func = false;
			
			if (!func) {
				// Magic bracket recursion
				double result = evaluate(expression.substring(start + 1, end));
				String resultS = String.valueOf(result);
				String newExpression = expression.substring(0, start) + resultS
						+ expression.substring(end + 1);
				expression = newExpression;
			} else {
				int startOp = -1;
				// Function handling
				for (int i1 = start - 1; i1 >= 0; i1--) {
					char c = expression.charAt(i1);
					for (char c1 : OPERATORS) {
						if (c == c1) {
							startOp = i1;
							break;
						}
					}
					if (startOp != -1)
						break;
				}
				String name = expression.substring(startOp + 1 , start);
				String args = expression.substring(start + 1, end);
				double[] evaluatedArgs = parseArguments(args, variables, callback);
				double result = evaluateFunction(name, evaluatedArgs, callback);
				String resultS = String.valueOf(result);
				String newExpression = expression.substring(0, startOp + 1) + resultS
						+ expression.substring(end + 1);
				expression = newExpression;
			}
			start = findBracket(expression);
		}
		while (expression.contains("^")) {
			for (int i = 0; i < expression.length(); i++) {
				char c = expression.charAt(i);
				if (c == '^') {
					expression = solve(c, expression, i);
					break;
				}
			}
		}
		while (expression.contains("*") || expression.contains("/") || expression.contains("%")) {
			for (int i = 0; i < expression.length(); i++) {
				char c = expression.charAt(i);
				if (c == '*' || c == '/' || c == '%') {
					expression = solve(c, expression, i);
					break;
				}
			}
		}
		while (expression.contains("+") || expression.contains("-")) {
			for (int i = 0; i < expression.length(); i++) {
				char c = expression.charAt(i);
				if (c == '+' || c == '-') {
					expression = solve(c, expression, i);
					break;
				}
			}
		}
		return (expression.startsWith("n") ? -1 * Double.valueOf(expression.substring(1)) : Double.valueOf(expression));
	}

	private static double evaluateFunction(String name, double[] args, FunctionCallback callback) {
		if (callback != null && callback.hasCallback(name))
			return callback.execute(name, args);
		if (name.equals("sin")) {
			if (args.length != 1)
				throw new RuntimeException("Only 1 argument can be passed to sin()!");
			return Math.sin(Math.toRadians(args[0]));
		} else if (name.equals("cos")) {
			if (args.length != 1)
				throw new RuntimeException("Only 1 argument can be passed to cos()!");
			return Math.cos(Math.toRadians(args[0]));
		} else if (name.equals("tan")) {
			if (args.length != 1)
				throw new RuntimeException("Only 1 argument can be passed to tan()!");
			return Math.tan(Math.toRadians(args[0]));
		} else if (name.equals("abs")) {
			if (args.length != 1)
				throw new RuntimeException("Only 1 argument can be passed to abs()!");
			return Math.abs(args[0]);
		} else if (name.equals("log")) {
			if (args.length != 1)
				throw new RuntimeException("Only 1 argument can be passed to log()!");
			return Math.log(args[0]);
		} else if (name.equals("rad")) {
			if (args.length != 1)
				throw new RuntimeException("Only 1 argument can be passed to rad()!");
			return Math.toRadians(args[0]);
		} else if (name.equals("deg")) {
			if (args.length != 1)
				throw new RuntimeException("Only 1 argument can be passed to deg()!");
			return Math.toDegrees(args[0]);
		} else if (name.equals("arcsin")) {
			if (args.length != 1)
				throw new RuntimeException("Only 1 argument can be passed to arcsin()!");
			return Math.toDegrees(Math.asin(args[0]));
		} else if (name.equals("arccos")) {
			if (args.length != 1)
				throw new RuntimeException("Only 1 argument can be passed to arccos()!");
			return Math.toDegrees(Math.acos(args[0]));
		} else if (name.equals("arctan")) {
			if (args.length != 1)
				throw new RuntimeException("Only 1 argument can be passed to arctan()!");
			return Math.toDegrees(Math.atan(args[0]));
		}
		throw new RuntimeException("Failed to find function by the name of " + name + "()!");
	}

	private static double[] parseArguments(String arg, Variable[] variables, FunctionCallback callback) {
		int count = 0;
		int bCount = 0;
		for (char c : arg.toCharArray()) {
			if (c == '(')
				bCount++;
			if (c == ')')
				bCount--;
			if (c == ',' && bCount == 0)
				count++;
		}
		String[] args = new String[count + 1];
		bCount = 0;
		count = 0;
		int last = 0;
		for (int i = 0; i < arg.length(); i++) {
			char c = arg.toCharArray()[i];
			if (c == '(')
				bCount++;
			if (c == ')')
				bCount--;
			if (c == ',') {
				args[count] = arg.substring(last, i);
				last = i + 1;
				count++;
			}
		}
		args[count] = arg.substring(last, arg.length());
		double[] retVal = new double[args.length];
		for (int i = 0; i < args.length; i++) {
			retVal[i] = evaluate(args[i], variables, callback);
		}
		return retVal;
	}

	private static String solve(char c, String expression, int i) {
		int before = findDoubleBefore(i - 1, expression);
		int after = findDoubleAfter(i + 1, expression);
		String beforeS = expression.substring(before + (before != 0 ? 1 : 0), i);
		String afterS = expression.substring(i + 1, after);
		double beforeD = (beforeS.startsWith("n") ? -1 * Double.valueOf(beforeS.substring(1))
				: Double.valueOf(beforeS));
		double afterD = (afterS.startsWith("n") ? -1 * Double.valueOf(afterS.substring(1)) : Double.valueOf(afterS));
		double newVal = 0;
		if (c == '*') {
			newVal = beforeD * afterD;
		} else if (c == '/') {
			newVal = beforeD / afterD;
		} else if (c == '+') {
			newVal = beforeD + afterD;
		} else if (c == '-') {
			newVal = beforeD - afterD;
		} else if (c == '^') {
			newVal = Math.pow(beforeD, afterD);
		} else if (c == '%') {
			newVal = beforeD % afterD;
		}
		String newValS = String.valueOf(newVal);
		newValS = newValS.replaceAll("-", "n");
		String newExpression = expression.substring(0, before + (before != 0 ? 1 : 0)) + newValS
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

	private static int findBracket(String expression) {
		int bCount = 0;
		for (int i = 0; i < expression.length(); i++) {
			char c = expression.charAt(i);
			if (c == '(' && bCount == 0) {
				return i;
			}
			if (c == '(')
				bCount++;
			if (c == ')')
				bCount--;
		}
		return -1;
	}

	private static int findClosingBracket(String expression) {
		int bCount = 0;
		for (int i = 0; i < expression.length(); i++) {
			char c = expression.charAt(i);
			if (c == ')' && bCount == 1) {
				return i;
			}
			if (c == '(')
				bCount++;
			if (c == ')')
				bCount--;
		}
		return -1;
	}
}
