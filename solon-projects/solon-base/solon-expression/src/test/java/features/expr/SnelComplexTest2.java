package features.expr;

import org.junit.jupiter.api.Test;
import org.noear.solon.expression.ExpressionEvaluator;
import org.noear.solon.expression.snel.SnelExpressionEvaluator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SnelComplexTest2 {

    private final ExpressionEvaluator evaluator = SnelExpressionEvaluator.getInstance();

    @Test
    public void testNestedTernaryExpression() {
        String expr = "x > 10 ? (y > 20 ? 'x>10 and y>20' : 'x>10 but y<=20') : (y > 20 ? 'x<=10 but y>20' : 'x<=10 and y<=20')";
        Map<String, Object> context = new HashMap<>();
        context.put("x", 15);
        context.put("y", 25);
        Object result = evaluator.eval(expr, context);
        assertEquals("x>10 and y>20", result);
    }

    @Test
    public void testComplexLogicalAndArithmeticExpression() {
        String expr = "(x + y > 30) && (z * 2 < 50) || (x - y < 0)";
        Map<String, Object> context = new HashMap<>();
        context.put("x", 20);
        context.put("y", 15);
        context.put("z", 20);
        Object result = evaluator.eval(expr, context);
        assertEquals(true, result);
    }

    @Test
    public void testMethodCallWithNestedProperty() {
        class User {
            class Address {
                String getCity() {
                    return "New York";
                }
            }

            Address getAddress() {
                return new Address();
            }
        }
        String expr = "user.address.getCity()";
        Map<String, Object> context = new HashMap<>();
        context.put("user", new User());
        Object result = evaluator.eval(expr, context);
        assertEquals("New York", result);
    }

    @Test
    public void testInOperatorWithVariableList() {
        String expr = "x IN list";
        Map<String, Object> context = new HashMap<>();
        context.put("x", 2);
        context.put("list", Arrays.asList(1, 2, 3));
        Object result = evaluator.eval(expr, context);
        assertEquals(true, result);
    }

    @Test
    public void testLikeOperatorWithVariable() {
        String expr = "name LIKE pattern";
        Map<String, Object> context = new HashMap<>();
        context.put("name", "John Doe");
        context.put("pattern", "%Doe%");
        Object result = evaluator.eval(expr, context);
        assertEquals(true, result);
    }

    @Test
    public void testNotInOperatorWithNestedList() {
        String expr = "x NOT IN nestedList[0]";
        Map<String, Object> context = new HashMap<>();
        context.put("x", 4);
        context.put("nestedList", Arrays.asList(Arrays.asList(1, 2, 3), Arrays.asList(5, 6, 7)));
        Object result = evaluator.eval(expr, context);
        assertEquals(true, result);
    }

    @Test
    public void testComplexPropertyAccessAndMethodCall() {
        class Order {
            class Product {
                String getName() {
                    return "Laptop";
                }
            }

            Product getProduct() {
                return new Product();
            }
        }
        String expr = "order.product.getName().length() > 5";
        Map<String, Object> context = new HashMap<>();
        context.put("order", new Order());
        Object result = evaluator.eval(expr, context);
        assertEquals(true, result);
    }

    public static class MathUtils {
       public static int add(int a, int b) {
            return a + b;
        }
    }

    @Test
    public void testTernaryExpressionWithMethodCall() {

        String expr = "x > 10 ? MathUtils.add(x, y) : x - y";
        Map<String, Object> context = new HashMap<>();
        context.put("x", 15);
        context.put("y", 5);
        context.put("MathUtils", MathUtils.class);
        Object result = evaluator.eval(expr, context);
        assertEquals(20, result);
    }

    @Test
    public void testLogicalExpressionWithMultipleOperators() {
        String expr = "(x > 10 && y < 20) || (z == 30 && w != 40)";
        Map<String, Object> context = new HashMap<>();
        context.put("x", 15);
        context.put("y", 18);
        context.put("z", 30);
        context.put("w", 45);
        Object result = evaluator.eval(expr, context);
        assertEquals(true, result);
    }

    @Test
    public void testArithmeticExpressionWithNestedParentheses() {
        String expr = "((x + y) * (z - w)) / (a + b)";
        Map<String, Object> context = new HashMap<>();
        context.put("x", 10);
        context.put("y", 5);
        context.put("z", 20);
        context.put("w", 3);
        context.put("a", 2);
        context.put("b", 3);
        Object result = evaluator.eval(expr, context);
        assertEquals(51, result);
    }

    @Test
    public void testMethodCallWithDynamicArguments() {
        class Calculator {
            int calculate(int a, int b, String op) {
                switch (op) {
                    case "+":
                        return a + b;
                    case "-":
                        return a - b;
                    case "*":
                        return a * b;
                    case "/":
                        return a / b;
                    default:
                        return 0;
                }
            }
        }
        String expr = "calculator.calculate(x, y, op)";
        Map<String, Object> context = new HashMap<>();
        context.put("x", 10);
        context.put("y", 5);
        context.put("op", "+");
        context.put("calculator", new Calculator());
        Object result = evaluator.eval(expr, context);
        assertEquals(15, result);
    }

    @Test
    public void testInOperatorWithMixedTypes() {
        String expr = "x IN list";
        Map<String, Object> context = new HashMap<>();
        context.put("x", "apple");
        context.put("list", Arrays.asList(1, "apple", true));
        Object result = evaluator.eval(expr, context);
        assertEquals(true, result);
    }

    @Test
    public void testLikeOperatorWithCaseInsensitive() {
        String expr = "name LIKE '%DOE%'";
        Map<String, Object> context = new HashMap<>();
        context.put("name", "John Doe");
        Object result = evaluator.eval(expr, context);
        assertEquals(true, result);
    }

    @Test
    public void testTernaryExpressionWithNestedLogicalExpression() {
        String expr = "(x > 10 && y < 20) ? 'Condition met' : 'Condition not met'";
        Map<String, Object> context = new HashMap<>();
        context.put("x", 15);
        context.put("y", 18);
        Object result = evaluator.eval(expr, context);
        assertEquals("Condition met", result);
    }

    @Test
    public void testComplexComparisonExpression() {
        String expr = "(x > y) && (x <= z) && (y != w)";
        Map<String, Object> context = new HashMap<>();
        context.put("x", 15);
        context.put("y", 10);
        context.put("z", 20);
        context.put("w", 10);
        Object result = evaluator.eval(expr, context);
        assertEquals(true, result);
    }

    @Test
    public void testMethodCallWithNullArguments() {
        class StringUtils {
            String concat(String a, String b) {
                return (a == null ? "" : a) + (b == null ? "" : b);
            }
        }
        String expr = "stringUtils.concat(x, y)";
        Map<String, Object> context = new HashMap<>();
        context.put("x", null);
        context.put("y", "world");
        context.put("stringUtils", new StringUtils());
        Object result = evaluator.eval(expr, context);
        assertEquals("world", result);
    }

    @Test
    public void testInOperatorWithEmptyList() {
        String expr = "x IN list";
        Map<String, Object> context = new HashMap<>();
        context.put("x", 1);
        context.put("list", Arrays.asList());
        Object result = evaluator.eval(expr, context);
        assertEquals(false, result);
    }

    @Test
    public void testLikeOperatorWithEmptyPattern() {
        String expr = "name LIKE ''";
        Map<String, Object> context = new HashMap<>();
        context.put("name", "John Doe");
        Object result = evaluator.eval(expr, context);
        assertEquals(false, result);
    }

    @Test
    public void testTernaryExpressionWithNullCondition() {
        String expr = "x ? 'True' : 'False'";
        Map<String, Object> context = new HashMap<>();
        context.put("x", null);
        Object result = evaluator.eval(expr, context);
        assertEquals("False", result);
    }

    @Test
    public void testComplexExpressionWithMultipleVariableTypes() {
        String expr = "(x > 10 && y.equals('test')) || (z instanceof java.lang.Integer)";
        Map<String, Object> context = new HashMap<>();
        context.put("x", 15);
        context.put("y", "test");
        context.put("z", 20);
        Object result = evaluator.eval(expr, context);
        assertEquals(true, result);
    }
}
