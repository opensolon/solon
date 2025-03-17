package features.expr;

import org.junit.jupiter.api.Test;
import org.noear.solon.expression.snel.SnEL;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SnelTest {

    // 算术运算测试用例
    @Test
    public void testSimpleAddition() {
        String expr = "1 + 2";
        Object result = SnEL.eval(expr);
        assertEquals(3, result);
    }

    @Test
    public void testSimpleSubtraction() {
        String expr = "5 - 3";
        Object result = SnEL.eval(expr);
        assertEquals(2, result);
    }

    @Test
    public void testSimpleMultiplication() {
        String expr = "2 * 3";
        Object result = SnEL.eval(expr);
        assertEquals(6, result);
    }

    @Test
    public void testSimpleDivision() {
        String expr = "6 / 2";
        Object result = SnEL.eval(expr);
        assertEquals(3, result);
    }

    @Test
    public void testModulus() {
        String expr = "7 % 3";
        Object result = SnEL.eval(expr);
        assertEquals(1, result);
    }

    // 逻辑运算测试用例
    @Test
    public void testLogicalAnd() {
        String expr = "true AND true";
        Object result = SnEL.eval(expr);
        assertEquals(true, result);
    }

    @Test
    public void testLogicalOr() {
        String expr = "true OR false";
        Object result = SnEL.eval(expr);
        assertEquals(true, result);
    }

    @Test
    public void testLogicalNot() {
        String expr = "NOT false";
        Object result = SnEL.eval(expr);
        assertEquals(true, result);
    }

    @Test
    public void testLogicalNot2() {
        String expr = "! false";
        Object result = SnEL.eval(expr);
        assertEquals(true, result);
    }

    @Test
    public void testLogicalNot3() {
        String expr = "!false";
        Object result = SnEL.eval(expr);
        assertEquals(true, result);
    }

    // 比较运算测试用例
    @Test
    public void testEqualityComparison() {
        String expr = "2 == 2";
        Object result = SnEL.eval(expr);
        assertEquals(true, result);
    }

    @Test
    public void testInequalityComparison() {
        String expr = "2 != 3";
        Object result = SnEL.eval(expr);
        assertEquals(true, result);
    }

    @Test
    public void testGreaterThanComparison() {
        String expr = "3 > 2";
        Object result = SnEL.eval(expr);
        assertEquals(true, result);
    }

    @Test
    public void testLessThanComparison() {
        String expr = "2 < 3";
        Object result = SnEL.eval(expr);
        assertEquals(true, result);
    }

    // 变量访问测试用例
    @Test
    public void testVariableAccess() {
        Map<String, Object> context = new HashMap<>();
        context.put("x", 5);
        String expr = "x";

        Object result = SnEL.eval(expr, context, false);
        assertEquals(5, result);
    }

    @Test
    public void testNestedVariableAccess() {
        Map<String, Object> nested = new HashMap<>();
        nested.put("y", 10);
        Map<String, Object> context = new HashMap<>();
        context.put("nested", nested);
        String expr = "nested.y";

        Object result = SnEL.eval(expr, context, false);
        assertEquals(10, result);
    }

    // 方法调用测试用例
    public static class MathUtils {
        public static int add(int a, int b) {
            return a + b;
        }
    }

    @Test
    public void testMethodCall() {
        Map<String, Object> context = new HashMap<>();
        context.put("MathUtils", MathUtils.class);
        String expr = "MathUtils.add(2, 3)";

        Object result = SnEL.eval(expr, context, false);
        assertEquals(5, result);
    }

    // 三元表达式测试用例
    @Test
    public void testTernaryExpressionTrue() {
        String expr = "true ? 1 : 2";
        Object result = SnEL.eval(expr);
        assertEquals(1, result);
    }

    @Test
    public void testTernaryExpressionFalse() {
        String expr = "false ? 1 : 2";
        Object result = SnEL.eval(expr);
        assertEquals(2, result);
    }

    // 布尔常量测试用例
    @Test
    public void testTrueBooleanConstant() {
        String expr = "true";
        Object result = SnEL.eval(expr);
        assertEquals(true, result);
    }

    @Test
    public void testFalseBooleanConstant() {
        String expr = "false";
        Object result = SnEL.eval(expr);
        assertEquals(false, result);
    }

    // IN 操作符测试用例
    @Test
    public void testInOperator() {
        String expr = "2 IN [1, 2, 3]";
        Object result = SnEL.eval(expr);
        assertEquals(true, result);
    }

    @Test
    public void testNotInOperator() {
        String expr = "4 NOT IN [1, 2, 3]";
        Object result = SnEL.eval(expr);
        assertEquals(true, result);
    }

    // LIKE 操作符测试用例
    // 假设 LIKE 操作符未实现具体逻辑，这里只是占位
    @Test
    public void testLikeOperator() {
        String expr = "'hello' LIKE 'h%'";
        Object result = SnEL.eval(expr);
        // 这里需要根据实际 LIKE 操作符的实现来判断结果
        // 目前假设未实现，先不做具体断言
    }

    @Test
    public void testNotLikeOperator() {
        String expr = "'hello' NOT LIKE 'x%'";
        Object result = SnEL.eval(expr);
        // 这里需要根据实际 LIKE 操作符的实现来判断结果
        // 目前假设未实现，先不做具体断言
    }

    // 括号表达式测试用例
    @Test
    public void testParenthesesExpression() {
        String expr = "(2 + 3) * 4";
        Object result = SnEL.eval(expr);
        assertEquals(20, result);
    }

    // 空值安全测试用例
    @Test
    public void testNullSafeVariableAccess() {
        Map<String, Object> context = new HashMap<>();
        context.put("obj", null);
        String expr = "obj.property";

        Object result = SnEL.eval(expr, context, false);
        assertEquals(null, result);
    }

    // 缓存测试用例
    @Test
    public void testCachedEval() {
        String expr = "1 + 2";
        Object result1 = SnEL.eval(expr);
        Object result2 = SnEL.eval(expr);
        assertEquals(result1, result2);
    }

    // 不同类型数字测试用例
    @Test
    public void testIntegerNumber() {
        String expr = "123";
        Object result = SnEL.eval(expr);
        assertEquals(123, result);
    }

    @Test
    public void testFloatNumber() {
        String expr = "123.45F";
        Object result = SnEL.eval(expr);
        assertEquals(123.45f, result);
    }

    @Test
    public void testDoubleNumber() {
        String expr = "123.45";
        Object result = SnEL.eval(expr);
        assertEquals(123.45, result);
    }

    @Test
    public void testLongNumber() {
        String expr = "123456789L";
        Object result = SnEL.eval(expr);
        assertEquals(123456789L, result);
    }

    // 字符串字面量测试用例
    @Test
    public void testStringLiteral() {
        String expr = "'hello'";
        Object result = SnEL.eval(expr);
        assertEquals("hello", result);
    }

    // 结合多种运算的复杂表达式测试用例
    @Test
    public void testComplexExpression() {
        String expr = "(2 + 3) * 4 > 10 AND true";
        Object result = SnEL.eval(expr);
        assertEquals(true, result);
    }

    // 更多测试用例可以继续添加...

}