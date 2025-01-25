package demo.flow.rule;

import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Inject;
import org.noear.solon.flow.ChainContext;
import org.noear.solon.flow.FlowEngine;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2025/1/25 created
 */
@SolonTest
public class BookTest {
    @Inject
    FlowEngine flowEngine;

    @Test
    public void case1()  throws Throwable {
        BookOrder bookOrder = new BookOrder();
        bookOrder.setOriginalPrice(10);

        ChainContext ctx = new ChainContext();
        ctx.put("order", bookOrder);

        flowEngine.eval("book_discount", ctx);

        assert bookOrder.getRealPrice() == 10;
    }

    @Test
    public void case2()  throws Throwable {
        BookOrder bookOrder = new BookOrder();
        bookOrder.setOriginalPrice(500);

        ChainContext ctx = new ChainContext();
        ctx.put("order", bookOrder);

        flowEngine.eval("book_discount", ctx);

        assert bookOrder.getRealPrice() == 400;
    }

    @Test
    public void case3()  throws Throwable {
        BookOrder bookOrder = new BookOrder();
        bookOrder.setOriginalPrice(120);

        ChainContext ctx = new ChainContext();
        ctx.put("order", bookOrder);

        flowEngine.eval("book_discount", ctx);

        assert bookOrder.getRealPrice() == 100;
    }
}
