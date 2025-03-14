package features.expr;

import org.junit.jupiter.api.Test;
import org.noear.solon.expression.exception.EvaluationException;
import org.noear.solon.expression.snel.SnEL;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SnelTest2 {

    // 测试包含多个算术运算符的复杂表达式
    @Test
    public void testComplexArithmeticExpression() {
        String expr = "2 + 3 * 4 - 5 / 2";
        Object result = SnEL.eval(expr);
        assertEquals(12, result);
    }

    // 测试包含多个逻辑运算符的复杂表达式
    @Test
    public void testComplexLogicalExpression() {
        String expr = "(true AND false) OR (true AND true)";
        Object result = SnEL.eval(expr);
        assertEquals(true, result);
    }

    // 测试包含括号的复杂算术表达式
    @Test
    public void testBracketedArithmeticExpression() {
        String expr = "(2 + 3) * (4 - 1)";
        Object result = SnEL.eval(expr);
        assertEquals(15, result);
    }

    // 测试包含多个比较运算符的复杂表达式
    @Test
    public void testComplexComparisonExpression() {
        String expr = "(2 < 3) AND (4 > 3) AND (5 == 5)";
        Object result = SnEL.eval(expr);
        assertEquals(true, result);
    }

    // 测试使用变量的复杂表达式
    @Test
    public void testComplexVariableExpression() {
        String expr = "x + y * z - w / 2";
        Map<String, Object> context = new HashMap<>();
        context.put("x", 2);
        context.put("y", 3);
        context.put("z", 4);
        context.put("w", 5);
        Object result = SnEL.eval(expr, context);
        assertEquals(12, result);
    }

    // 测试包含 IN 操作符和变量的表达式
    @Test
    public void testInOperatorWithVariable() {
        String expr = "x IN [1, 2, 3]";
        Map<String, Object> context = new HashMap<>();
        context.put("x", 2);
        Object result = SnEL.eval(expr, context);
        assertEquals(true, result);
    }

    // 测试包含 NOT IN 操作符和变量的表达式
    @Test
    public void testNotInOperatorWithVariable() {
        String expr = "x NOT IN [1, 2, 3]";
        Map<String, Object> context = new HashMap<>();
        context.put("x", 4);
        Object result = SnEL.eval(expr, context);
        assertEquals(true, result);
    }

    // 测试包含 LIKE 操作符和变量的表达式
    @Test
    public void testLikeOperatorWithVariable() {
        String expr = "name LIKE 'John'";
        Map<String, Object> context = new HashMap<>();
        context.put("name", "John Doe");
        Object result = SnEL.eval(expr, context);
        assertEquals(true, result);
    }

    // 测试包含 NOT LIKE 操作符和变量的表达式
    @Test
    public void testNotLikeOperatorWithVariable() {
        String expr = "name NOT LIKE '%Jane%'";
        Map<String, Object> context = new HashMap<>();
        context.put("name", "John Doe");
        Object result = SnEL.eval(expr, context);
        assertEquals(true, result);
    }

    // 测试包含嵌套三元表达式的复杂表达式
    @Test
    public void testNestedTernaryExpression() {
        String expr = "x > 10 ? (y > 20 ? 'Both true' : 'x true, y false') : (y > 20 ? 'x false, y true' : 'Both false')";
        Map<String, Object> context = new HashMap<>();
        context.put("x", 15);
        context.put("y", 25);
        Object result = SnEL.eval(expr, context);
        assertEquals("Both true", result);
    }

    // 测试包含空列表的 IN 操作符表达式
    @Test
    public void testInOperatorWithEmptyList() {
        String expr = "x IN []";
        Map<String, Object> context = new HashMap<>();
        context.put("x", 1);
        Object result = SnEL.eval(expr, context);
        assertEquals(false, result);
    }

    // 测试包含空变量的表达式
    @Test
    public void testExpressionWithNullVariable() {
        String expr = "x + 5";
        Map<String, Object> context = new HashMap<>();
        context.put("x", null);
        assertThrows(EvaluationException.class, () -> SnEL.eval(expr, context));
    }

    // 测试包含非法操作符的表达式
    @Test
    public void testExpressionWithInvalidOperator() {
        String expr = "2 & 3";
        assertThrows(RuntimeException.class, () -> SnEL.eval(expr));
    }

    // 测试包含非法变量名的表达式
    @Test
    public void testExpressionWithInvalidVariableName() {
        String expr = "2 + @invalid";
        assertThrows(RuntimeException.class, () -> SnEL.eval(expr));
    }

    // 测试包含未定义变量的表达式
    @Test
    public void testExpressionWithUndefinedVariable() {
        String expr = "x + 5";
        assertThrows(EvaluationException.class, () -> SnEL.eval(expr));
    }

    // 测试包含多个 IN 操作符的表达式
    @Test
    public void testExpressionWithMultipleInOperators() {
        String expr = "x IN [1, 2, 3] AND y IN [4, 5, 6]";
        Map<String, Object> context = new HashMap<>();
        context.put("x", 2);
        context.put("y", 5);
        Object result = SnEL.eval(expr, context);
        assertEquals(true, result);
    }

    // 测试包含多个 LIKE 操作符的表达式
    @Test
    public void testExpressionWithMultipleLikeOperators() {
        String expr = "name1 LIKE 'John' AND name2 LIKE 'Doe'";
        Map<String, Object> context = new HashMap<>();
        context.put("name1", "John Doe");
        context.put("name2", "John Doe");
        Object result = SnEL.eval(expr, context);
        assertEquals(true, result);
    }

    // 测试包含逻辑运算符和比较运算符的复杂表达式
    @Test
    public void testComplexLogicalAndComparisonExpression() {
        String expr = "(x > 10 AND y < 20) OR (z == 30)";
        Map<String, Object> context = new HashMap<>();
        context.put("x", 15);
        context.put("y", 18);
        context.put("z", 30);
        Object result = SnEL.eval(expr, context);
        assertEquals(true, result);
    }

    // 测试包含算术运算符和比较运算符的复杂表达式
    @Test
    public void testComplexArithmeticAndComparisonExpression() {
        String expr = "(x + y > 20) AND (z - w < 10)";
        Map<String, Object> context = new HashMap<>();
        context.put("x", 15);
        context.put("y", 8);
        context.put("z", 12);
        context.put("w", 5);
        Object result = SnEL.eval(expr, context);
        assertEquals(true, result);
    }
}