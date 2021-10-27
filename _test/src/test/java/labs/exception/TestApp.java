package labs.exception;

import org.noear.solon.Solon;

/**
 * @author noear 2021/10/27 created
 */
public class TestApp {
    public static void main(String[] args) {
        Solon.start(TestApp.class, args, app -> {
//            app.filter((ctx, chain) -> {
//                try {
//                    chain.doFilter(ctx);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    ctx.output("我累了...休息下");
//                }
//            });

            app.get("/hello1", ctx -> {
                String name = ctx.param("name");

                if (name == null) {
                    throw new IllegalArgumentException("Name cannot be empty");
                } else {
                    ctx.output("Hello " + name);
                }
            });
        });
    }
}
