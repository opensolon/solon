package features.flow.script;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.flow.core.Chain;
import org.noear.solon.flow.core.ChainContext;
import org.noear.solon.flow.core.FlowEngine;

/**
 * @author noear 2025/1/11 created
 */
public class ExprTest {
    @Test
    public void expr1() throws Throwable {
        Chain chain = Chain.parse(ResourceUtil.getResourceAsString("expr1.json"));

        FlowEngine chainExecutor = new FlowEngine();

        ChainContext context = new ChainContext();
        context.paramSet("a", 2);
        context.paramSet("b", 3);
        context.paramSet("c", 4);

        //完整执行

        chainExecutor.exec(context, chain);
        System.out.println("------------");

        context = new ChainContext();
        context.paramSet("a", 12);
        context.paramSet("b", 13);
        context.paramSet("c", 14);

        //执行一层
        chainExecutor.exec(context, chain, "n2", 1);
    }
}