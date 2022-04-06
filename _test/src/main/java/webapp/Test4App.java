package webapp;

import org.noear.solon.Solon;
import org.noear.solon.core.message.ListenerEmpty;
import org.noear.solon.core.message.Session;

public class Test4App {
    public static void main(String[] args) {
        Solon.start(Test4App.class, args, app -> {
            app.filter((ctx, chain) -> {
                if (ctx.path().startsWith("/nginx/")) {
                    ctx.pathNew(ctx.path().substring(7));
                }

                chain.doFilter(ctx);
            });

            app.listenBefore(new ListenerEmpty() {
                @Override
                public void onOpen(Session session) {
                    if (session.path().startsWith("/xx/")) {
                        session.pathNew(session.path().substring(4));
                    }
                }
            });
        });

        ;
    }
}
