package benchmark;

import org.noear.solon.core.util.PropUtil;
import org.noear.solon.core.util.PropsExpressionContext;
import org.noear.solon.expression.snel.SnEL;

import java.util.Properties;

/**
 *
 * @author noear 2025/9/15 created
 *
 */
public class PropTmplTest {
    public static void main(String[] args) {
        String expr = "${user.dir:1}";

        for (int i = 0; i < 10; i++) {
            case1(System.getProperties(), expr);
            case2(System.getProperties(), expr);
        }

        /// /////////
        long timeStart = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            case1(System.getProperties(), expr);
        }
        System.out.println("case1:" + (System.currentTimeMillis() - timeStart));

        timeStart = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            case2(System.getProperties(), expr);
        }
        System.out.println("case2:" + (System.currentTimeMillis() - timeStart));

    }


    public static void case1(Properties props, String expr) {
        PropsExpressionContext context = new PropsExpressionContext(props)
                .forAllowPropertyDefault(true)
                .forAllowPropertyNesting(false);

        //ps: evalTmpl 会过滤无模板符号的并直接返回
        SnEL.parseTmpl(expr).eval(context);
    }

    public static void case2(Properties props, String expr) {
        PropUtil.getByTml(null, props, expr, null);
    }
}
