package xyz.finlaym.mexe;

public class MathExpressionEvaluatorTest {
	public static void main(String[] args) {
		String expression = "(-3) * 2 + 5";
		double result = MathExpressionEvaluator.evaluate(expression);
		System.out.println(result);
	}
}
