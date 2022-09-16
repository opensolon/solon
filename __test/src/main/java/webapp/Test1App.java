package webapp;

import org.noear.solon.Solon;

/**
 * @author noear 2022/7/5 created
 */
public class Test1App {
    public static void main(String[] args) {
        Solon.start(Test1App.class, args, app -> {
            app.filter(-1, (ctx, chain) -> {
                if (ctx.path().endsWith(".html")) {
                    ctx.pathNew(ctx.path().substring(0, ctx.path().length() - 5));
                }

                if (ctx.path().endsWith(".do")) {
                    ctx.pathNew(ctx.path().substring(0, ctx.path().length() - 3));
                }

                chain.doFilter(ctx);
            });
        });
    }
}
