package features.flow.app;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.solon.annotation.Inject;
import org.noear.solon.flow.ChainContext;
import org.noear.solon.flow.FlowEngine;
import org.noear.solon.test.SolonTest;

/**
 * 自动装配风格
 *
 * @author noear 2025/1/12 created
 */
@Slf4j
@SolonTest
public class AppTest {
    @Inject
    private FlowEngine flowEngine;

    @Test
    public void case1_demo() throws Throwable {
        flowEngine.eval("c1");
    }

    @Test
    public void case6() throws Throwable {
        ChainContext context = new ChainContext();
        flowEngine.eval("c6", context);
        assert context.result.equals(112);

        log.trace("counter: {}", context.counter());
    }

    @Test
    public void case4_inclusive() throws Throwable {
        ChainContext context = new ChainContext();
        context.put("day", 1);
        flowEngine.eval("c4", context);
        assert context.result.equals(0);
        log.trace("counter: {}", context.counter());

        context = new ChainContext();
        context.put("day", 3);
        flowEngine.eval("c4", context);
        assert context.result.equals(3);
        log.trace("counter: {}", context.counter());
    }

    @Test
    public void case4_inclusive2() throws Throwable {
        ChainContext context = new ChainContext();
        context.put("day", 7);
        flowEngine.eval("c4", context);
        assert context.result.equals(10);
        log.trace("counter: {}", context.counter());
    }

    @Test
    public void case5_parallel() throws Throwable {
        ChainContext context = new ChainContext();
        context.put("day", 7);
        flowEngine.eval("c5", context);
        assert context.result.equals(10);

        log.trace("counter: {}", context.counter());
        log.trace(ONode.load(context).toJson());
    }

    @Test
    public void case7() throws Throwable {
        ChainContext context;

        context = new ChainContext();
        context.put("day", 1);
        flowEngine.eval("c7", context);
        assert context.result.equals(2);

        log.trace("counter: {}", context.counter());
        log.trace(ONode.load(context).toJson());

        //----------------

        context = new ChainContext();
        context.put("day", 3);
        flowEngine.eval("c7", context);
        assert context.result.equals(3);

        log.trace("counter: {}", context.counter());
        log.trace(ONode.load(context).toJson());

        //----------------

        context = new ChainContext();
        context.put("day", 7);
        flowEngine.eval("c7", context);
        assert context.result.equals(4);

        log.trace("counter: {}", context.counter());
        log.trace(ONode.load(context).toJson());

        //----------------

        context = new ChainContext();
        context.put("day", 10);
        flowEngine.eval("c7", context);
        assert context.result.equals(5);

        log.trace("counter: {}", context.counter());
        log.trace(ONode.load(context).toJson());

        //----------------

        context = new ChainContext();
        context.put("day", 15);
        flowEngine.eval("c7", context);
        assert context.result.equals(6);

        log.trace("counter: {}", context.counter());
        log.trace(ONode.load(context).toJson());


        //----------------

        context = new ChainContext();
        context.put("day", 20);
        flowEngine.eval("c7", context);
        assert context.result.equals(7);

        log.trace("counter: {}", context.counter());
        log.trace(ONode.load(context).toJson());

        //----------------

        context = new ChainContext();
        context.put("day", 30);
        flowEngine.eval("c7", context);
        assert context.result.equals(8);

        log.trace("counter: {}", context.counter());
        log.trace(ONode.load(context).toJson());
    }

    @Test
    public void case7_2() throws Throwable {
        ChainContext context = new ChainContext();
        context.put("day", 30);
        flowEngine.eval("c7", context);
        assert context.result.equals(8);

        log.trace("counter: {}", context.counter());
        log.trace(ONode.load(context).toJson());
    }

    @Test
    public void d2_test() throws Throwable {
        ChainContext context = new ChainContext();
        context.put("score", 7);

        flowEngine.eval("d2", context);
    }

    @Test
    public void r1_test() throws Throwable {
        ChainContext context = new ChainContext();
        Order order = new Order();

        order.amount = 100;
        context.put("s", order);
        flowEngine.eval("r1", context);
        assert order.getScore() == 0;

        order.amount = 200;
        context.put("s", order);
        flowEngine.eval("r1", context);
        assert order.getScore() == 100;

        order.amount = 800;
        context.put("s", order);
        flowEngine.eval("r1", context);
        assert order.getScore() == 500;
    }
}
