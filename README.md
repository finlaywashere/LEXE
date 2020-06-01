# LEXE - The Logical Expression Evaluator

LEXE is a library designed to simplify the task of handling math and logic parsing

## Usage

Using either MEXE (The Mathematical EXpression Evaluator) or LEXE (The Logical EXpression Evaluator) is simple,
it has no dependencies and can be called with `MathExpressionEvaluator.evaluate(expression);` where `expression`
is a `String`, it can also be passed an array of variables which are created by calling the `Variable` constructor
with the arguments `name` and `value` where `name` is a `String` and `value` is a `double`, a class implementing
FunctionCallback can also be passed as an argument or it can be left null.

## Examples

`MathExpressionEvaluator.evaluate("5+7");` returns `12.0`

`LogicalExpressionEvaluator.evaluate("true == false");` returns `false`

```
Variable[] variables = new Variable[2];
variables[0] = new Variable("x",4);
variables[1] = new Variable("xy",7);
MathExpressionEvaluator.evaluate("x*xy+5",variables);
```
which returns `33.0`
