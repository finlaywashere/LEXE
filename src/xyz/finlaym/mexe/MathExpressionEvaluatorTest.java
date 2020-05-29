package xyz.finlaym.mexe;

public class MathExpressionEvaluatorTest {
	public static void main(String[] args) {
		String expression = "-3 * 2 + (-5) * x + xy";
		System.out.println("Equation is: "+expression);
		System.out.println("Expected solution is: -19");
		double result = MathExpressionEvaluator.evaluate(expression, new Variable[] {new Variable("x",4), new Variable("xy",7)});
		System.out.println("Solution is: "+result);
	}
}
