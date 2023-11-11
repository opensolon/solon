package org.noear.solon.net.socketd.handle;

import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.listener.SimpleListener;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Socket.D 监听器，转 Mvc 处理
 *
 * @author noear
 * @since 2.0
 */
public class SocketdToMvc extends SimpleListener {
    private static final Logger log = LoggerFactory.getLogger(SocketdToMvc.class);

    @Override
    public void onMessage(Session session, Message message) throws IOException {
        if (Utils.isEmpty(message.getTopic())) {
            log.warn("This message is missing topic, sid={}", message.getSid());
            return;
        }

        try {
            SocketdContext ctx = new SocketdContext(session, message);

            Solon.app().tryHandle(ctx);

            if (ctx.getHandled() || ctx.status() != 404) {
                ctx.commit();
            }
        } catch (Throwable e) {
            //context 初始化时，可能会出错
            //
            log.warn(e.getMessage(), e);
        }
    }
}
