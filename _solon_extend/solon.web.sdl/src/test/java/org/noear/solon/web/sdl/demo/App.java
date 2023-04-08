package org.noear.solon.web.sdl.demo;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.SolonMain;

/**
 * @author noear 2023/4/5 created
 */
@SolonMain
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args, app -> {
            app.filter((ctx, chain) -> {
                try {
                    chain.doFilter(ctx);
                } catch (Throwable e) {
                    ctx.output(Utils.throwableToString(e));
                }
            });
        });
    }
}
