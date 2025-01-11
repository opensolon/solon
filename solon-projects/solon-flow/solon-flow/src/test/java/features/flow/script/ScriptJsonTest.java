package features.flow.script;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.flow.core.Chain;
import org.noear.solon.flow.core.ChainContext;
import org.noear.solon.flow.core.FlowEngine;

/**
 * @author noear 2025/1/11 created
 */
public class ScriptJsonTest {
    FlowEngine chainExecutor = new FlowEngine();

    @Test
    public void case1() throws Throwable {
        Chain chain = Chain.parseByUri("classpath:script_case1.json");

        ChainContext context = new ChainContext();
        context.paramSet("a", 2);
        context.paramSet("b", 3);
        context.paramSet("c", 4);

        //完整执行
        chainExecutor.exec(context, chain);
    }
    @Test
    public void case2() throws Throwable {
        Chain chain = Chain.parseByUri("classpath:script_case2.json");

        ChainContext context = new ChainContext();
        context.paramSet("a", 2);
        context.paramSet("b", 3);
        context.paramSet("c", 4);

        //完整执行

        chainExecutor.exec(context, chain);
        assert "n3".equals(context.result);

        System.out.println("------------");

        context = new ChainContext();
        context.paramSet("a", 12);
        context.paramSet("b", 13);
        context.paramSet("c", 14);

        //执行一层
        chainExecutor.exec(context, chain, "n2", 1);
        assert context.result.equals(123);
    }
}