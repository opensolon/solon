package org.noear.solon.extend.socketd;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;

/**
 * SocketD 上下文处理者
 *
 * @author noear
 * @since 1.0
 * */
class SocketContextHandler {

    public void handle(Session session, Message message, boolean messageIsString) {
        if (message == null) {
            return;
        }

        //没有资源描述的，不进入Handler体系
        if (Utils.isEmpty(message.resourceDescriptor())) {
            return;
        }

        try {
            SocketContext ctx = new SocketContext(session, message, messageIsString);

            Solon.global().tryHandle(ctx);

            if (ctx.getHandled() || ctx.status() != 404) {
                ctx.commit();
            }
        } catch (Throwable ex) {
            //context 初始化时，可能会出错
            //
            EventBus.push(ex);
        }
    }
}
