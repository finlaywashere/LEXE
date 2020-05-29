package xyz.finlaym.mexe;

public class MathExpressionEvaluatorTest {
	public static void main(String[] args) {
		String expression = "(-3) * 2 + 5 * x + xy";
		double result = MathExpressionEvaluator.evaluate(expression, new Variable[] {new Variable("x",4), new Variable("xy",7)});
		System.out.println(result);
	}
}
