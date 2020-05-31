package xyz.finlaym.lexe;

import static org.junit.Assert.*;

import org.junit.Test;

public class LogicalExpressionEvaluatorTest {

	@Test
	public void test() {
		String[] tests = {
				"2+1 == 3 || (true == false)", "true==false", "((true || false) && true) || 4 == 5+1",
				"false==false","false!=true","true == ((true || false))"
		};
		boolean[] expected = {
			true,false,true,
			true,true,true
		};
		for(int i = 0; i < tests.length; i++) {
			String test = tests[i];
			boolean expectedB = expected[i];
			boolean result = LogicalExpressionEvaluator.evaluate(test);
			System.out.println("Test: "+(i+1)+" result: "+result);
			assertEquals(expectedB, result);
		}
	}

}
