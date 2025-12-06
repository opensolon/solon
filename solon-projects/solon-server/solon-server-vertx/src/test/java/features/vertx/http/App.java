package features.vertx.http;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

/**
 * @author noear 2024/10/1 created
 */
@Controller
public class App {
    public static void main(String[] args) {
        Solon.start(ServerTest.class, args);
    }

    @Mapping("hello")
    public String hello() {
        return "hello";
    }

    @Mapping("async")
    public void async(Context ctx) {
        try {
            ctx.asyncStart();
            ctx.output("async");
        } finally {
            ctx.asyncComplete();
        }
    }

    @Mapping("async_timeout")
    public void async_timeout(Context ctx) {
        ctx.asyncStart(100L, null);
    }
}
