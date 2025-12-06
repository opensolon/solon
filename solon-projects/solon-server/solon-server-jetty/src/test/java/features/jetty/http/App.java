package features.jetty.http;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Param;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.MultiMap;

import javax.servlet.http.HttpSession;

/**
 * @author noear 2024/10/1 created
 */
@Controller
public class App {
    public static void main(String[] args) {
        Solon.start(
                ServerTest.class,
                MultiMap.from(args).then(x -> x.add("cfg", "app-http.yml"))
        );
    }

    @Mapping("hello")
    public String hello(HttpSession session) {
        assert session != null;
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

    @Mapping("session")
    public Object session(Context ctx, @Param(value = "name",required = false) String name) {
        if (name == null) {
            return ctx.session("name");
        } else {
            ctx.sessionSet("name", name);
            return name;
        }
    }
}
