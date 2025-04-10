package features.expr;

import org.junit.jupiter.api.Test;
import org.noear.solon.expression.snel.SnEL;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SnelComplexTest {

    // 复杂嵌套逻辑表达式
    @Test
    public void testComplexNestedLogicalExpression() {
        String expr = "(true AND (false OR true)) OR (false AND true)";
        Object result = SnEL.eval(expr);
        assertEquals(true, result);
    }

    @Test
    public void testComplexNestedLogicalExpression1() {
        String expr = "(true AND (false OR true)) OR (NOT true)";
        Object result = SnEL.eval(expr);
        assertEquals(true, result);
    }

    @Test
    public void testComplexNestedLogicalExpression2() {
        String expr = "(true && (false || true)) || (false && true)";
        Object result = SnEL.eval(expr);
        assertEquals(true, result);
    }

    @Test
    public void testComplexNestedLogicalExpression3() {
        String expr = "(true && (false || true)) || !true";
        Object result = SnEL.eval(expr);
        assertEquals(true, result);
    }

    // 嵌套方法调用和属性访问
    public static class User {
        private String name;

        public User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class Service {
        public String getUserName(User user) {
            return user.getName();
        }
    }

    @Test
    public void testNestedMethodCallAndPropertyAccess() {
        Map<String, Object> context = new HashMap<>();
        User user = new User("John");
        Service service = new Service();
        context.put("user", user);
        context.put("service", service);
        String expr = "service.getUserName(user)";
        Object result = SnEL.eval(expr, context, false);
        assertEquals("John", result);
    }

    // 复杂的算术和比较表达式
    @Test
    public void testComplexArithmeticAndComparisonExpression() {
        String expr = "((2 + 3) * 4) > (5 + 10) AND (15 % 2) == 1";
        Object result = SnEL.eval(expr);
        assertEquals(true, result);
    }

    // 嵌套三元表达式
    @Test
    public void testNestedTernaryExpression() {
        String expr = "true ? (false ? 1 : 2) : (true ? 3 : 4)";
        Object result = SnEL.eval(expr);
        assertEquals(2, result);
    }

    // 带有 IN 和 NOT IN 的复杂表达式
    @Test
    public void testComplexInAndNotInExpression() {
        String expr = "2 IN [1, 2, 3] AND 4 NOT IN [1, 2, 3]";
        Object result = SnEL.eval(expr);
        assertEquals(true, result);
    }

    // 多层嵌套属性访问
    public static class Address {
        private String city;

        public Address(String city) {
            this.city = city;
        }

        public String getCity() {
            return city;
        }
    }

    public static class NestedUser {
        private Address address;

        public NestedUser(Address address) {
            this.address = address;
        }

        public Address getAddress() {
            return address;
        }
    }

    @Test
    public void testMultiLevelNestedPropertyAccess() {
        Map<String, Object> context = new HashMap<>();
        Address address = new Address("New York");
        NestedUser nestedUser = new NestedUser(address);
        context.put("nestedUser", nestedUser);
        String expr = "nestedUser.address.city";

        Object result = SnEL.eval(expr, context, false);
        assertEquals("New York", result);
    }

    // 带有 LIKE 和 NOT LIKE 的复杂表达式
    @Test
    public void testComplexLikeAndNotLikeExpression() {
        String expr = "'hello' LIKE 'h' AND 'world' NOT LIKE 'x%'";
        Object result = SnEL.eval(expr);
        // 这里需要根据实际 LIKE 操作符的实现来判断结果
        // 目前假设 LIKE 操作符按预期工作
        assertEquals(true, result);
    }

    // 结合逻辑、算术和比较的复杂表达式
    @Test
    public void testCombinedLogicalArithmeticAndComparisonExpression() {
        String expr = "(2 + 3 > 4) AND (true OR false) AND (6 % 3 == 0)";
        Object result = SnEL.eval(expr);
        assertEquals(true, result);
    }

    // 带有列表和索引访问的复杂表达式
    @Test
    public void testComplexExpressionWithListAndIndexAccess() {
        Map<String, Object> context = new HashMap<>();
        List<Integer> list = Arrays.asList(1, 2, 3);
        context.put("list", list);
        String expr = "list[1] + 2";

        Object result = SnEL.eval(expr, context, false);
        assertEquals(4, result);
    }

    // 复杂的方法调用和条件判断
    public static class Calculator {
        public int add(int a, int b) {
            return a + b;
        }

        public boolean isEven(int num) {
            return num % 2 == 0;
        }
    }

    @Test
    public void testComplexMethodCallAndConditionJudgment() {
        Map<String, Object> context = new HashMap<>();
        Calculator calculator = new Calculator();
        context.put("calculator", calculator);
        String expr = "calculator.isEven(calculator.add(2, 3)) ? 'even' : 'odd'";

        Object result = SnEL.eval(expr, context, false);
        assertEquals("odd", result);
    }
}