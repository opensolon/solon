package org.noear.solon.core.message;

import java.io.IOException;

/**
 * SocketD 监听器（实现 Message + Listener 架构）
 *
 * <pre><code>
 * @ServerEndpoint(value = "/demoe/websocket")
 * public class SocketListenerImp implements Listener {
 *     @Override
 *     public void onMessage(Session session, Message message) {
 *         //表示已处理
 *         message.setHandled(true);
 *
 *         //把消息内容广播给别的会话
 *         session.getOpenSessions().forEach(s -> {
 *             s.send(message.toString());
 *         });
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
@FunctionalInterface
public interface Listener {
    default void onOpen(Session session) {
    }

    void onMessage(Session session, Message message, boolean messageIsString) throws IOException;

    default void onClose(Session session) {
    }

    default void onError(Session session, Throwable error) {
    }
}
