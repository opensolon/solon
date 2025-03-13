package demo.expr;

import org.junit.jupiter.api.Test;
import org.noear.solon.expression.snel.SnEL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author noear 2025/3/13 created
 */
public class DemoTest {
    @Test
    public void debug() {
        System.out.println(SnEL.eval("[1,2,3]"));
    }

    @Test
    public void case1() {
        System.out.println(SnEL.eval("'hello world!'"));
    }

    @Test
    public void case2() {
        System.out.println(SnEL.eval("1"));
        System.out.println(SnEL.eval("-1"));
        System.out.println(SnEL.eval("1 + 1"));
        System.out.println(SnEL.eval("1 * (1 + 2)"));
        System.out.println(SnEL.eval("'solon'"));
        System.out.println(SnEL.eval("true"));
        System.out.println(SnEL.eval("[1,2,3,-4]"));
    }

    @Test
    public void case3() {
        Map<String, String> map = new HashMap<>();
        map.put("code", "world");

        List<Integer> list = new ArrayList<>();
        list.add(1);

        Map<String, Object> context = new HashMap<>();
        context.put("name", "solon");
        context.put("list", list);
        context.put("map", map);

        System.out.println(SnEL.eval("name.length()", context)); //顺便调用个函数
        System.out.println(SnEL.eval("name.length() > 2 OR true", context));
        System.out.println(SnEL.eval("name.length() > 2 ? 'A' : 'B'", context));
        System.out.println(SnEL.eval("map['code']", context));
        System.out.println(SnEL.eval("list[0]", context));
        System.out.println(SnEL.eval("list[0] == 1", context));
    }

    @Test
    public void case4() {
        Map<String, Object> context = new HashMap<>();
        context.put("age", 25);
        context.put("salary", 4000);
        context.put("isMarried", false);
        context.put("label", "aa");
        context.put("title", "ee");
        context.put("vip", "l3");

        String expression = "(((age > 18 AND salary < 5000) OR (NOT isMarried)) AND label IN ['aa','bb'] AND title NOT IN ['cc','dd']) OR vip=='l3'";
        System.out.println(SnEL.eval(expression, context));
    }

    @Test
    public void case5() {
        Map<String, Object> context = new HashMap<>();
        context.put("Math", Math.class);
        System.out.println(SnEL.eval("Math.abs(-5) > 4 ? 'A' : 'B'", context));
    }
}