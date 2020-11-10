package org.noear.solon.core;

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
public interface XListener {

    default void onOpen(XSession session){}

    void onMessage(XSession session, XMessage message, boolean messageIsString);

    default void onClose(XSession session){}

    default void onError(XSession session, Throwable error){}
}
