package features.expr;

import org.junit.jupiter.api.Test;
import org.noear.solon.expression.Expression;
import org.noear.solon.expression.snel.SnEL;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author noear 2025/3/13 created
 */
public class PropTest {

    @Test
    public void case1() {
        Map<String, Object> user = new HashMap<>();
        user.put("name", "world");
        user.put("age", 20);
        user.put("age2", 10);

        Map<String, Object> context = new HashMap();
        context.put("user", user);

        Object result = SnEL.eval("user.age == 20 ? true : false", context);
        assert true == (Boolean) result;


        result = SnEL.eval("user.age == user.age2 ? true : false", context);
        assert false == (Boolean) result;

        result = SnEL.eval("false ? true : false", context);
        assert false == (Boolean) result;
    }

    @Test
    public void case2() {
        Map<String, Object> user = new HashMap<>();
        user.put("name", "world");
        user.put("age", 20);
        user.put("age2", 10);

        Map<String, Object> order = new HashMap<>();
        order.put("id", 1);
        order.put("user", user);

        Map<String, Object> context = new HashMap();
        context.put("order", order);

        Object result = SnEL.eval("order.user.age == 20 ? true : false", context);
        assert true == (Boolean) result;


        result = SnEL.eval("order.user.age == order.user.age2 ? true : false", context);
        assert false == (Boolean) result;
    }

    @Test
    public void case3() {
        Map<String, Object> user = new HashMap<>();
        user.put("name", "world");
        user.put("age", 20);
        user.put("age2", 10);

        Map<String, Object> order = new HashMap<>();
        order.put("id", 1);
        order.put("user", user);

        Map<String, Object> context = new HashMap();
        context.put("order", order);

        Object result = SnEL.eval("order['user']['age'] == 20 ? true : false", context);
        assert true == (Boolean) result;

        result = SnEL.eval("order['user']['age'] == order.user['age2'] ? true : false", context);
        assert false == (Boolean) result;
    }

    @Test
    public void case4() {
        // 测试集合的整数索引访问
        Map<String, Object> context = new HashMap<>();
        List<String> items = Arrays.asList("item1", "item2", "item3");
        Map<String, Object> order = new HashMap<>();
        order.put("items", items);
        context.put("order", order);

        // 测试 order.items[0]
        Expression expr1 = SnEL.parse("order.items[0]");
        System.out.println(expr1.eval(context::get)); // 输出: item1

        // 测试 order.items[1]
        Expression expr2 = SnEL.parse("order.items[1]");
        System.out.println(expr2.eval(context::get)); // 输出: item2

        // 测试数组的整数索引访问
        int[] numbers = {10, 20, 30};
        context.put("numbers", numbers);

        // 测试 numbers[0]
        Expression expr3 = SnEL.parse("numbers[0]");
        assert 10 == (int) (expr3.eval(context::get)); // 输出: 10

        // 测试 numbers[2]
        Expression expr4 = SnEL.parse("numbers[2]");
        assert 30 == (int) (expr4.eval(context::get)); // 输出: 30
    }
}
