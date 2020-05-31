package xyz.finlaym.lexe;

import static org.junit.Assert.*;

import org.junit.Test;

public class LogicalExpressionEvaluatorTest {

	@Test
	public void test() {
		String[] tests = {
				"2+1 == 3 || (true == false)"
		};
		boolean[] expected = {
			true	
		};
		for(int i = 0; i < tests.length; i++) {
			String test = tests[i];
			boolean expectedB = expected[i];
			boolean result = LogicalExpressionEvaluator.evaluate(test);
			assertEquals(expectedB, result);
		}
	}

}
