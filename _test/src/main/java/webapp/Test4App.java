package webapp;

import org.noear.solon.Solon;
import org.noear.solon.core.message.Listener;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;

import java.io.IOException;

public class Test4App {
    public static void main(String[] args) {
        Solon.start(Test4App.class, args, app -> {
            app.filter((ctx, chain) -> {
                if (ctx.path().startsWith("/nginx/")) {
                    ctx.pathNew(ctx.path().substring(7));
                }

                chain.doFilter(ctx);
            });

            app.listenBefore(new Listener() {
                @Override
                public void onOpen(Session session) {
                    if (session.path().startsWith("/xx/")) {
                        session.pathNew(session.path().substring(4));
                    }
                }

                @Override
                public void onMessage(Session session, Message message) throws IOException {

                }
            });
        });
    }
}
