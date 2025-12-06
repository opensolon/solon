package features.grizzly.https;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Body;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.MultiMap;

/**
 * @author noear 2024/10/1 created
 */
@Controller
public class App {
    public static void main(String[] args) {
        Solon.start(
                App.class,
                MultiMap.from(args).then(x -> x.add("cfg", "app-https.yml"))
        );
    }

    @Mapping("hello")
    public String hello(String name) {
        return "hello " + name;
    }

    @Mapping("body")
    public String body(String tag, @Body String body) {
        return tag + ":" + body;
    }

    @Post
    @Mapping("post")
    public String post() {
        return "ok";
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

    @Mapping("/redirect/h5")
    public void h5(Context ctx, int code) throws Exception {
        ctx.redirect("https://h5.noear.org/", code);
    }

    @Mapping("/redirect/jump")
    public void jump(Context ctx, int code) throws Exception {
        ctx.redirect("target", code);
    }

    @Mapping("/redirect/target")
    public String target() throws Exception {
        return "ok";
    }
}
