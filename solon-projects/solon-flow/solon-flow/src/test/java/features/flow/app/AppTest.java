package features.flow.app;

import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.solon.annotation.Inject;
import org.noear.solon.flow.core.ChainContext;
import org.noear.solon.flow.core.FlowEngine;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2025/1/12 created
 */
@SolonTest
public class AppTest {
    @Inject
    private FlowEngine flowEngine;

    @Test
    public void case1_demo() throws Throwable {
        flowEngine.eval("c1");
    }

    @Test
    public void case4_inclusive() throws Throwable {
        ChainContext context = new ChainContext();
        context.paramSet("day", 1);
        flowEngine.eval("c4", context);
        assert context.result.equals(0);

        context = new ChainContext();
        context.paramSet("day", 3);
        flowEngine.eval("c4", context);
        assert context.result.equals(3);
    }

    @Test
    public void case4_inclusive2() throws Throwable {
        ChainContext context = new ChainContext();
        context.paramSet("day", 7);
        flowEngine.eval("c4", context);
        assert context.result.equals(10);
    }

    @Test
    public void case5_parallel() throws Throwable {
        ChainContext context = new ChainContext();
        context.paramSet("day", 7);
        flowEngine.eval("c5", context);
        assert context.result.equals(10);

        System.out.println(ONode.load(context).toJson());
    }

    @Test
    public void context_test() {
        String json = "{\"params\":{\"day\":7},\"result\":10,\"attrs\":{}}";
        ChainContext context = ONode.load(json).toObject(ChainContext.class);

        String json2 = ONode.load(context).toJson();
        System.out.println(json2);

        assert json2.equals(json);
    }
}
