package xyz.finlaym.mexe;

import static org.junit.Assert.*;

import org.junit.Test;

public class MathExpressionEvaluatorTest {

	@Test
	public void testBasicEquations() {
		// Test equations with a bunch of random spacing to make sure it works with any
		// spacing
		// NOTE: numbers can have spaces in their names only when being used, all spaces are removed by the parser
		String[] tests = { 
				"1 +  1", "-5 * (  7   +3)", "3^   2*  9",
				"(3+4)   ^2 +1", "9+(-1)+   1 +1 +1+3/3", "1   7    % 3+7" ,
				"(((3+2)))*5","((((5*3)+5))/4)+2","((((((((1))))))))"
		};
		double[] expectedVals = { 
				2, -50, 81,
				50, 12, 9,
				25,7,1
		};
		for (int i = 0; i < tests.length; i++) {
			String equation = tests[i];
			double expected = expectedVals[i];
			double result = MathExpressionEvaluator.evaluate(equation);
			System.out.println("Test: "+(i+1)+" result: "+result);
			assertEquals(expected, result, 0.3);
		}
	}

	@Test
	public void testVariables() {
		// Test equations with a bunch of random spacing to make sure it works with any
		// spacing
		// NOTE: variables and numbers can have spaces in their names only when being used, all spaces are removed by the parser
		Variable[] vars = new Variable[] {
				new Variable("x",3),new Variable("y",2), new Variable("z",5),
				new Variable("xy",7), new Variable ("xyz",11)
		};
		String[] tests = {
				"  x+     x  y ","z %    x + 4", " (  xy + y ) /          x +1",
				"11/xyz + 1   3-x    y","(xyz+y+x)/4","x  y   z   % x + 3"
		};
		double[] expectedVals = {
				10,6,4,
				7,4,5
		};
		for (int i = 0; i < tests.length; i++) {
			String equation = tests[i];
			double expected = expectedVals[i];
			double result = MathExpressionEvaluator.evaluate(equation,vars);
			System.out.println("Test: "+(i+7)+" result: "+result);
			assertEquals(expected, result, 0.3);
		}
	}
	@Test
	public void testFunctions() {
		// Test equations with a bunch of random spacing to make sure it works with any
		// spacing
		// NOTE: functions and numbers can have spaces in their names only when being used, all spaces are removed by the parser
		String[] tests = { 
				"arcsin(sin(30))","sin(30)","tan(5+3)",
				"abs(-25)","abs(abs(abs(-15)))","arctan(tan(9/3))"
		};
		double[] expectedVals = { 
			30,Math.sin(Math.toRadians(30)),Math.tan(Math.toRadians(8)),
			25,15,3
		};
		for (int i = 0; i < tests.length; i++) {
			String equation = tests[i];
			double expected = expectedVals[i];
			double result = MathExpressionEvaluator.evaluate(equation);
			System.out.println("Test: "+(i+13)+" result: "+result);
			assertEquals(expected, result, 0.3);
		}
	}
}
