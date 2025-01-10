package demo.solon.flow.script;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.flow.core.Chain;
import org.noear.solon.flow.core.ChainContext;
import org.noear.solon.flow.core.FlowExecutor;

/**
 * @author noear 2025/1/11 created
 */
public class ExprTest {
    @Test
    public void expr1() throws Throwable {
        Chain chain = Chain.parse(ResourceUtil.getResourceAsString("expr1.json"));

        FlowExecutor chainExecutor = new FlowExecutor();

        ChainContext context = new ChainContext();
        context.set("a", 2);
        context.set("b", 3);
        context.set("c", 4);

        //完整执行

        chainExecutor.exec(context, chain);
        System.out.println("------------");

        context = new ChainContext();
        context.set("a", 12);
        context.set("b", 13);
        context.set("c", 14);

        //执行一层
        chainExecutor.exec(context, chain, "n2", 1);
    }
}