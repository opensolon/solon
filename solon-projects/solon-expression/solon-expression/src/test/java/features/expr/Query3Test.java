package features.expr;

import org.junit.jupiter.api.Test;
import org.noear.solon.expression.*;
import org.noear.solon.expression.snel.SnEL;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2025/3/12 created
 */
public class Query3Test {

    @Test
    public void case1() {
        Map<String, Object> context = new HashMap<>();
        context.put("age", 25);
        context.put("salary", 4000);
        context.put("isMarried", false);
        context.put("label", "aa");
        context.put("title", "ee");
        context.put("vip", "l3");

        String expression = "(((age > 18 AND salary < 5000) OR (NOT isMarried)) AND label IN ['aa','bb'] AND title NOT IN ['cc','dd']) OR vip=='l3'";
        Expression root = SnEL.parse(expression);

        // 打印表达式树
        System.out.println("Expression Tree: " + root);

        // 计算表达式结果
        Object result = root.eval(context::get);
        System.out.println("Result: " + result); // Output: Result: true
        assert result instanceof Boolean;

        PrintUtil.printTree2(root);
    }

    @Test
    public void case1_2() {
        Map<String, Object> context = new HashMap<>();
        context.put("age", 25);
        context.put("salary", 4000);
        context.put("isMarried", false);
        context.put("label", "aa");
        context.put("title", "ee");
        context.put("vip", "l3");

        String expression = "(((age > 18 && salary < 5000) || (!isMarried)) && label IN ['aa','bb'] && title NOT IN ['cc','dd']) || vip=='l3'";
        Expression root = SnEL.parse(expression);

        // 打印表达式树
        System.out.println("Expression Tree: " + root);

        // 计算表达式结果
        Object result = root.eval(context::get);
        System.out.println("Result: " + result); // Output: Result: true
        assert result instanceof Boolean;

        PrintUtil.printTree2(root);
    }

    @Test
    public void case2() {
        Map<String, Object> context = new HashMap();
        context.put("age", 25);
        context.put("salary", 4000);
        context.put("isMarried", false);
        context.put("label", "aa");
        context.put("title", "ee");
        context.put("vip", "l3");

        String expression = "((age > 18 OR salary < 5000) AND (NOT isMarried) AND label IN ['aa','bb'] AND title NOT IN ['cc','dd']) OR vip=='l3'";

        Expression root = SnEL.parse(expression);

        // 打印表达式树
        System.out.println("Expression Tree: " + root);

        // 计算表达式结果
        Object result = root.eval(context::get);
        System.out.println("Result: " + result); // Output: Result: true
        assert ((Boolean) result) == true;

        PrintUtil.printTree2(root);
    }

    @Test
    public void case3() {
        Map<String, Object> context = new HashMap();
        context.put("age", 25);
        context.put("salary", 4000);
        context.put("isMarried", false);
        context.put("label", "aa");
        context.put("title", "ee");
        context.put("vip", "l3");

        String expression = "((age > 18 OR salary < 5000) AND (isMarried == false) AND label IN ['aa','bb'] AND title NOT IN ['cc','dd']) OR vip=='l3'";
        Expression root = SnEL.parse(expression);

        // 打印表达式树
        System.out.println("Expression Tree: " + root);

        // 计算表达式结果
        Object result = root.eval(context::get);
        System.out.println("Result: " + result); // Output: Result: true
        assert ((Boolean) result) == true;

        PrintUtil.printTree2(root);
    }

    @Test
    public void case4() {
        Map<String, Object> context = new HashMap();
        context.put("age", 25);
        context.put("salary", 4000);
        context.put("salaryV", 5000);
        context.put("isMarried", false);
        context.put("label", "aa");
        context.put("title", "ee");
        context.put("vip", "l3");

        String expression = "((age > 18 OR salary < salaryV) AND (isMarried == false) AND label IN ['aa','bb'] AND title NOT IN ['cc','dd']) OR vip=='l3'";
        Expression root = SnEL.parse(expression);

        // 打印表达式树
        System.out.println("Expression Tree: " + root);

        // 计算表达式结果
        Object result = root.eval(context::get);
        System.out.println("Result: " + result); // Output: Result: true
        assert ((Boolean) result) == true;

        PrintUtil.printTree2(root);
    }

    @Test
    public void case5() {
        // 数学运算 (Long)
        Integer result = (Integer) SnEL.eval("1+2+3");
        System.out.println(result); // 6
        assert 6 == result;

        // 数学运算 (Double)
        Double result2 = (Double) SnEL.eval("1.1+2.2+3.3");
        System.out.println(result2); // 6.6
        assert 6.6D == result2;

        // 包含关系运算和逻辑运算
        Boolean result3 = (Boolean) SnEL.eval("(1>0||0<1)&&1!=0");
        System.out.println(result3); // true
        assert result3 == true;

        // 三元运算
        String result4 = (String) SnEL.eval("4 > 3 ? \"4 > 3\" : 999");
        System.out.println(result4); // 4 > 3
        assert "4 > 3".equals(result4);
    }

    @Test
    public void case6() {
        Map<String, Object> context = new HashMap();
        context.put("a", 1);
        context.put("b", 2);

        Integer result = (Integer) SnEL.eval("(a + b) * 2", context);
        assert result == 6;
    }

    @Test
    public void case7() {
        Map<String, Object> context = new HashMap();
        context.put("age", 20);
        context.put("salary", 4000);

        Object result = SnEL.eval("(age > 18 AND salary < 5000) ? \n" +
                "'Eligible' : 'Not Eligible'", context);
        assert "Eligible".equals(result);
    }

    @Test
    public void case8() {
        Number result1 = (Number) SnEL.eval("1");
        assert result1.intValue() == 1;

        String result2 = (String) SnEL.eval("'2'");
        assert "2".equals(result2);

        String result3 = (String) SnEL.eval("'hello ' + 'world!'");
        assert "hello world!".equals(result3);
    }

    @Test
    public void case9() {
        Map<String, Object> context = new HashMap();
        context.put("user_name", "world");

        Object result = SnEL.eval("user_name", context);
        assert "world".equals(result);
    }

    @Test
    public void case10() {
        Object result = SnEL.eval("1 > 1.1D");
        assert false == (Boolean) result;
    }

    @Test
    public void case11() {
        Object result = SnEL.eval("1 == 1.0D");
        assert true == (Boolean) result;
        assert 1 == 1.0D;
    }

    @Test
    public void case12() {
        Object rst = SnEL.eval("'Hello'.concat(' World')");
        System.out.println(rst);

        assert "Hello World".equals(rst);
    }

    @Test
    public void case13() {
        Map<String, Object> context = new HashMap();
        context.put("end", "!");
        Object rst = SnEL.eval("('Hello' + ' World').concat(end)", context);
        System.out.println(rst);

        assert "Hello World!".equals(rst);
    }
}