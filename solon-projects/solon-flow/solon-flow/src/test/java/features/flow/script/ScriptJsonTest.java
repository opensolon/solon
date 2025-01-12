package features.flow.script;

import org.junit.jupiter.api.Test;
import org.noear.solon.flow.core.Chain;
import org.noear.solon.flow.core.ChainContext;
import org.noear.solon.flow.core.FlowEngine;

/**
 * @author noear 2025/1/11 created
 */
public class ScriptJsonTest {
    private FlowEngine flowEngine = FlowEngine.newInstance();

    @Test
    public void case1_demo() throws Throwable {
        Chain chain = Chain.parseByUri("classpath:flow/script_case1.json");

        flowEngine.eval(chain);
    }

    @Test
    public void case2_interrupt() throws Throwable {
        Chain chain = Chain.parseByUri("classpath:flow/script_case2.json");

        ChainContext context = new ChainContext();
        context.paramSet("a", 2);
        context.paramSet("b", 3);
        context.paramSet("c", 4);

        //完整执行

        flowEngine.eval(chain, context);
        assert "n3".equals(context.result);
    }

    @Test
    public void case2_interrupt2() throws Throwable {
        Chain chain = Chain.parseByUri("classpath:flow/script_case2.json");

        ChainContext context = new ChainContext();
        context.paramSet("a", 12);
        context.paramSet("b", 13);
        context.paramSet("c", 14);

        //执行一层
        flowEngine.eval(chain, "n2", 1, context);
        assert context.result.equals(123);
    }

    @Test
    public void case3_exclusive() throws Throwable {
        Chain chain = Chain.parseByUri("classpath:flow/script_case3.json");

        ChainContext context = new ChainContext();
        context.paramSet("day", 1);
        flowEngine.eval(chain, context);
        assert null == context.result;

        context = new ChainContext();
        context.paramSet("day", 3);
        flowEngine.eval(chain, context);
        assert context.result.equals(3);

        context = new ChainContext();
        context.paramSet("day", 7);
        flowEngine.eval(chain, context);
        assert context.result.equals(7);
    }

    @Test
    public void case4_inclusive() throws Throwable {
        Chain chain = Chain.parseByUri("classpath:flow/script_case4.json");

        ChainContext context = new ChainContext();
        context.paramSet("day", 1);
        flowEngine.eval(chain, context);
        assert context.result.equals(0);

        context = new ChainContext();
        context.paramSet("day", 3);
        flowEngine.eval(chain, context);
        assert context.result.equals(3);
    }

    @Test
    public void case4_inclusive2() throws Throwable {
        Chain chain = Chain.parseByUri("classpath:flow/script_case4.json");

        ChainContext context = new ChainContext();
        context.paramSet("day", 7);
        flowEngine.eval(chain, context);
        assert context.result.equals(10);
    }

    @Test
    public void case5_parallel() throws Throwable {
        Chain chain = Chain.parseByUri("classpath:flow/script_case5.yml");

        ChainContext context = new ChainContext();
        context.paramSet("day", 7);
        flowEngine.eval(chain, context);
        assert context.result.equals(10);
    }
}