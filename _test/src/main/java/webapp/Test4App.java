package webapp;

import org.noear.solon.Solon;

public class Test4App {
    public static void main(String[] args) {
        Solon.start(Test4App.class, args, app -> {
            app.filter((ctx, chain) -> {
                if (ctx.path().startsWith("/nginx/")) {
                    ctx.pathNew(ctx.path().substring(7));
                }

                chain.doFilter(ctx);
            });
        });
    }
}
