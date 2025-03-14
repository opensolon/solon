package lab.expr;

import org.noear.solon.expression.snel.SnEL;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2025/3/14 created
 */
public class Test1 {
    public static void main(String[] args) {
        Map<Object, Object> context = new HashMap<>();
        context.put("a", 1);
        context.put("b", 2);

        long start = System.currentTimeMillis();

        for (int i = 0; i < 10_000_000; i++) {
//            SnEL.eval("a+b", context);
            SnEL.eval("1+1");
        }

        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}