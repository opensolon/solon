package features.expr;

import org.junit.jupiter.api.Test;
import org.noear.solon.expression.snel.SnEL;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2025/3/13 created
 */
public class MethodTest {
    Order order = new Order(1, new User("world", 20));

    @Test
    public void case1() {
        Map<String, Object> context = new HashMap();
        context.put("order", order);

        assert 20 == (Integer) SnEL.eval("order.user.age", context);
    }

    @Test
    public void case2() {
        Map<String, Object> context = new HashMap();
        context.put("order", order);

        assert 10 == (Integer) SnEL.eval("order.user.getAge2()", context);

        assert 3 == (Integer) SnEL.eval("order.add(1,2)", context);
    }

    @Test
    public void case3() {
        Map<String, Object> context = new HashMap();
        context.put("order", order);

        Object result = SnEL.eval("order['user']['age'] == 20 ? true : false", context);
        assert true == (Boolean) result;

        result = SnEL.eval("order['user']['age'] == order.user.getAge2() ? true : false", context);
        assert false == (Boolean) result;
    }

    public static class Order {
        public int id;
        public User user;

        public Order(int id, User user) {
            this.id = id;
            this.user = user;
        }

        public int add(int a, int b) {
            return a + b;
        }
    }

    public static class User {
        public String name;
        public int age;

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public int getAge2() {
            return 10;
        }
    }
}
