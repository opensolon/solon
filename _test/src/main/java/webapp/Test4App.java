package webapp;

import org.noear.solon.Solon;

public class Test4App {
    public static void main(String[] args) {
        Solon.start(Test4App.class, args, app -> {
            app.filter((ctx, chain) -> {
                ctx.pathNew(ctx.path().replace("/nginx/",""));
                chain.doFilter(ctx);
            });
        });
    }
}
