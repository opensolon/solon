package features.expr;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.Props;
import org.noear.solon.expression.context.StandardContext;
import org.noear.solon.expression.snel.SnEL;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SnTmplComplexTest {

    @Test
    public void testNestedVariable() {
        String template = "Hello, #{user.name}!";
        Map<String, Object> user = new HashMap<>();
        user.put("name", "Tom");
        Map<String, Object> context = new HashMap<>();
        context.put("user", user);
        String result = SnEL.evalTmpl(template, context);
        assertEquals("Hello, Tom!", result);
    }

    @Test
    public void testVariableInList() {
        String template = "Hello, #{names[0]}!";
        Map<String, Object> context = new HashMap<>();
        context.put("names", new String[]{"Eve", "Frank"});
        String result = SnEL.evalTmpl(template, context);
        assertEquals("Hello, Eve!", result);
    }

    @Test
    public void testMultipleNestedVariables() {
        String template = "#{company.name} - #{company.department.name} - #{company.department.employee.name}";
        Map<String, Object> employee = new HashMap<>();
        employee.put("name", "Grace");

        Map<String, Object> department = new HashMap<>();
        department.put("name", "HR");
        department.put("employee", employee);

        Map<String, Object> company = new HashMap<>();
        company.put("name", "ABC Corp");
        company.put("department", department);

        Map<String, Object> context = new HashMap<>();
        context.put("company", company);

        String result = SnEL.evalTmpl(template, context);
        assertEquals("ABC Corp - HR - Grace", result);
    }

    @Test
    public void testVariableWithMethodCall() {
        String template = "Hello, #{user.getName()}!";
        class User {
            public String getName() {
                return "David";
            }
        }
        Map<String, Object> context = new HashMap<>();
        context.put("user", new User());
        String result = SnEL.evalTmpl(template, context);
        assertEquals("Hello, David!", result);
    }

    @Test
    public void testVariableWithArithmeticExpression() {
        String template = "Result: #{a + b}";
        Map<String, Integer> context = new HashMap<>();
        context.put("a", 2);
        context.put("b", 3);
        String result = SnEL.evalTmpl(template, context);
        assertEquals("Result: 5", result);
    }

    @Test
    public void testVariableWithLogicalExpression() {
        String template = "Is true: #{a > b}";
        Map<String, Integer> context = new HashMap<>();
        context.put("a", 5);
        context.put("b", 3);
        String result = SnEL.evalTmpl(template, context);
        assertEquals("Is true: true", result);
    }

    @Test
    public void testVariableWithTernaryExpression() {
        String template = "Result: #{a > b ? 'Greater' : 'Less'}";
        Map<String, Integer> context = new HashMap<>();
        context.put("a", 5);
        context.put("b", 3);
        String result = SnEL.evalTmpl(template, context);
        assertEquals("Result: Greater", result);
    }

    @Test
    public void testMultipleExpressions() {
        String template = "#{a} + #{b} = #{a + b}";
        Map<String, Integer> context = new HashMap<>();
        context.put("a", 2);
        context.put("b", 3);
        String result = SnEL.evalTmpl(template, context);
        assertEquals("2 + 3 = 5", result);
    }

    @Test
    public void testVariableWithNestedList() {
        String template = "Value: #{list[0][1]}";
        Map<String, Object> context = new HashMap<>();
        context.put("list", new Object[][]{new Object[]{1, 2}, new Object[]{3, 4}});
        String result = SnEL.evalTmpl(template, context);
        assertEquals("Value: 2", result);
    }

    public static class Address {
        public String city = "New York";
    }

    public static class Person {
        public Address address = new Address();
    }

    @Test
    public void testVariableWithComplexObject() {
        String template = "Info: #{person.address.city}";


        Map<String, Object> context = new HashMap<>();
        context.put("person", new Person());

        String result = SnEL.evalTmpl(template, context);
        assertEquals("Info: New York", result);
    }

    @Test
    public void case1() {
        Props props = new Props();
        props.put("v", "1");

        Map<String, Object> context = new HashMap<>();
        context.put(SnEL.CONTEXT_PROPS_KEY, props);

        String template = "Info: ${v}#{1}";
        String result = SnEL.evalTmpl(template, context);
        assertEquals("Info: 11", result);
    }

    @Test
    public void case2() {
        Props props = new Props();
        props.put("v", "1");

        Map<String, Object> context = new HashMap<>();
        context.put(SnEL.CONTEXT_PROPS_KEY, props);

        String template = "Info: ${v2:2}#{1}";
        String result = SnEL.evalTmpl(template, context);
        assertEquals("Info: 21", result);

        template = "Info: ${v2}#{1}";
        result = SnEL.evalTmpl(template, context);
        assertEquals("Info: 1", result);
    }

    @Test
    public void case2_2() {
        Props props = new Props();
        props.put("v", "1");

        StandardContext context = new StandardContext()
                .properties(props);

        String template = "Info: ${v2:2}#{1}";
        String result = SnEL.evalTmpl(template, context);
        assertEquals("Info: 21", result);

        template = "Info: ${v2}#{1}";
        result = SnEL.evalTmpl(template, context);
        assertEquals("Info: 1", result);
    }
}