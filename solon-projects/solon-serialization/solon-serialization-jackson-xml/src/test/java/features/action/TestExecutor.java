package features.action;

import org.junit.jupiter.api.Test;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2024/9/17 created
 */
@SolonTest
public class TestExecutor {
    @Test
    public void test() throws Throwable {
        ContextEmpty ctx = new ContextEmpty();
        ctx.headerMap().add("Content-Type", "text/xml");
        ctx.pathNew("/");
        ctx.bodyNew("<xml><name>noear</name><label>A</label></xml>");

        Solon.app().tryHandle(ctx);

        System.out.println(ctx.result);

        assert "Hello noear A".equals(ctx.result);
    }

    @Controller
    public static class Demo {
        @Mapping
        public String hello(String name, Label label) {
            return "Hello " + name + " " + label;
        }
    }

    public enum Label {
        A,
        B
    }
}