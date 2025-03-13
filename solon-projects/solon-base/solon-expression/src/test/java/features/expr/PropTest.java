package features.expr;

import org.junit.jupiter.api.Test;
import org.noear.solon.expression.ExpressionEvaluator;
import org.noear.solon.expression.snel.SnelExpressionEvaluator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2025/3/13 created
 */
public class PropTest {
    ExpressionEvaluator evaluator = SnelExpressionEvaluator.getInstance();

    @Test
    public void case10() {
        Map<String,Object> user = new HashMap<>();
        user.put("name", "world");
        user.put("age", 20);
        user.put("age2", 10);

        Map<String,Object> context = new HashMap();
        context.put("user", user);

        Object result = evaluator.eval("user.age == 20 ? true : false", context);
        assert true == (Boolean) result;


        result = evaluator.eval("user.age == user.age2 ? true : false", context);
        assert false == (Boolean) result;

        result = evaluator.eval("false ? true : false", context);
        assert false == (Boolean) result;
    }

    @Test
    public void case11() {
        Map<String,Object> user = new HashMap<>();
        user.put("name", "world");
        user.put("age", 20);
        user.put("age2", 10);

        Map<String,Object> order = new HashMap<>();
        order.put("id",1);
        order.put("user", user);

        Map<String,Object> context = new HashMap();
        context.put("order", order);

        Object result = evaluator.eval("order.user.age == 20 ? true : false", context);
        assert true == (Boolean) result;


        result = evaluator.eval("order.user.age == order.user.age2 ? true : false", context);
        assert false == (Boolean) result;
    }
}
