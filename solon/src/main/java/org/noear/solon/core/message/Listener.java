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
    /**
     * 打开会话时
     * */
    default void onOpen(Session session) {
    }

    /**
     * 收到消息时
     * */
    void onMessage(Session session, Message message) throws IOException;

    /**
     * 关闭会话时
     * */
    default void onClose(Session session) {
    }

    /**
     * 出错时
     * */
    default void onError(Session session, Throwable error) {
    }
}
