package org.noear.solon.core.message;

/**
 * XSocket 监听器（实现 XMessage + XListener 架构）
 *
 * <pre><code>
 * @XServerEndpoint(value = "/demoe/websocket")
 * public class XSocketListenerImp implements XListener {
 *     @Override
 *     public void onMessage(XSession session, XMessage message) {
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
public interface MessageListener {

    default void onOpen(MessageSession session){}

    void onMessage(MessageSession session, Message message, boolean messageIsString);

    default void onClose(MessageSession session){}

    default void onError(MessageSession session, Throwable error){}
}
